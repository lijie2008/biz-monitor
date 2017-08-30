/**
 * Project Name:biz-monitor-provider
 * File Name:RevokedAspect.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.config
 * Date:2017年8月7日下午2:33:47
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.constant.Constant;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.enums.OperateType;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.commom.model.NodeDetailTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.commom.model.ResourceTo;
import com.huntkey.rx.sceo.monitor.commom.model.RevokedTo;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeOrderService;
import com.huntkey.rx.sceo.monitor.provider.service.RedisService;

/**
 * ClassName:RevokedAspect 与撤销有关的切面信息
 * Date:     2017年8月7日 下午2:33:47
 * @author   lijie
 * @version  
 * @see 	 
 */
@Aspect
@Component
public class RevokedAspect {
    
    private static final Map<String,Object> originalMap = new ConcurrentHashMap<String, Object>();
    
    private static final Logger logger = LoggerFactory.getLogger(RevokedAspect.class);
    
    @Autowired 
    private RedisService redisService;
    
    @Autowired
    private MonitorTreeOrderService service;
    
    // 服务前
    @Before(value="@annotation(revoked)",argNames="revoked")
    public void serviceStart(JoinPoint point, Revoked revoked) {
        
        String key = getKey(point,revoked.type());
        
        logger.info("服务开始前, 取出 key 值  ： " + key);
        
        switch(revoked.type()){
            
            case INITIALIZE: 
                return;
            case NODE: 
                
                String orderId = null;
                if(JsonUtil.isEmpity(revoked.character()))
                    orderId = getNode(key).getPid();
                else
                    orderId = key;
                
                logger.info("服务开始前, 节点型操作， 取出表单 单号 ： " + orderId);
                
                List<NodeDetailTo> nodes = service.getAllNodesAndResource(orderId);
                
                originalMap.put(key, nodes);
                break;
                
            case DETAIL: 
                
                originalMap.put(key,  getNode(key));
                break;
        }
    }
    
    // 服务异常
    @AfterThrowing(value="@annotation(revoked)", throwing="e")
    public void serviceException(JoinPoint point, Revoked revoked,Exception e){
        
        String key = getKey(point,revoked.type());
        
        if(!JsonUtil.isEmpity(key))
            originalMap.remove(key);
        
    }
    
    // 服务正常完成后
    @AfterReturning(value="@annotation(revoked)",argNames="revoked,result",returning = "result")
    public void serviceEnd(JoinPoint point, Revoked revoked,Result result){
        
        String key = getKey(point,revoked.type());
        
        logger.info("服务完成后, 取出key值  ： " + key);
        
        Object value = null;
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS){
            
            value = JsonUtil.isEmpity(key) ? null: originalMap.remove(key);
            return;
        }
        
        value = (revoked.type() == OperateType.INITIALIZE || JsonUtil.isEmpity(key))? null : originalMap.get(key);
        
        logger.info("服务完成后, 取出Map里面的值  ： " + JsonUtil.getJsonString(value));
        
        String orderId = null;
        
        switch(revoked.type()){
            
            case INITIALIZE:
                
                orderId = result.getData().toString();
                
                if(orderId == null)
                    return;
                
                if(!redisService.isEmpity(orderId))
                   redisService.delete(orderId);
                
                redisService.lPush(orderId, new RevokedTo(null, revoked.type()));
                return;
                
            case NODE:
                
                JSONArray arry = JSON.parseArray(JSONArray.toJSONString(value));
                
                logger.info("服务完成后, 节点型操作arry值 ： " + JsonUtil.getJsonArrayString(arry));
                
                orderId = JsonUtil.isEmpity(arry) ? null : arry.getJSONObject(0).getString(Constant.PID);
                
                logger.info("服务完成后, 取出orderId ： " + orderId);
                break;
                
            case DETAIL:
                
                orderId = JsonUtil.getJson(value).getString(Constant.PID);
                break;
        }
        
        if(orderId == null || redisService.isEmpity(orderId)) // 未初始化堆栈进行的操作 不做撤销储备
            return;
        
        redisService.lPush(orderId, new RevokedTo(value, revoked.type()));
        
        if(!JsonUtil.isEmpity(key))
            originalMap.remove(key);
        
    }
    
    /**
     * 
     * getKey: 取有revoked注解参数的值
     * @author lijie
     * @param point
     * @return
     */
    public String getKey(JoinPoint point,OperateType type){
        
        if(type == OperateType.INITIALIZE)
            return null;
        
        Method method = ((MethodSignature)point.getSignature()).getMethod();
        
        Annotation[][] types = method.getParameterAnnotations();
        
        for(int i = 0; i< types.length; i++){
            
            for(int j = 0; j< types[i].length;j++){
                
                if(Revoked.class.isInstance(types[i][j])){
                    
                    Revoked revoked = (Revoked)types[i][j];
                    
                    String key = revoked.key();
                    
                    Object arg = point.getArgs()[i];
                    
                    if(arg instanceof java.lang.String){
                        
                        return (String)arg;
                    }else{
                        
                        if(JsonUtil.isEmpity(key))
                            ApplicationException.throwCodeMesg(ErrorMessage._60001.getCode(), ErrorMessage._60001.getMsg());
                        
                        return (String)JsonUtil.getJson(arg).get(key);
                        
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * 
     * getNode:查询节点详细
     * @author lijie
     * @param key
     * @return
     */
    private NodeDetailTo getNode(String key) {
        
        NodeTo node = service.queryNode(key);
        
        List<ResourceTo> resources = service.queryResource(key);
        
        NodeDetailTo detail = JSON.parseObject(JSON.toJSONString(node),NodeDetailTo.class);
        
        if(JsonUtil.isEmpity(detail))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), ErrorMessage._60005.getMsg());
        
        detail.setMtor019(resources);
        
        return detail;
    }
}

