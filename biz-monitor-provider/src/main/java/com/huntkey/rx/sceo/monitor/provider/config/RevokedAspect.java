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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.enums.OperateType;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.commom.model.RevokedTo;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
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
    
    private static Logger log = Logger.getLogger(RevokedAspect.class);
    
    private static Map<String,Object> originalMap = new ConcurrentHashMap<String, Object>();
    
    @Autowired 
    private RedisService redisService;
    
    @SuppressWarnings("incomplete-switch")
    @Before(value="@annotation(revoked)",argNames="revoked")
    public void serviceStart(JoinPoint point, Revoked revoked) {
        log.info("服务开始前1 service start before!");
        
        OperateType type = revoked.type();
        if(type == OperateType.INITIALIZE)
            return;
        String key = getKey(point);
        Object value = null;
        
        switch(type){
            case NODE:
                // key - 查临时单
                
                break;
            case DETAIL:
                // key - 查节点详情
                
                break;
        }
        
        originalMap.put(key, value);
        
        log.info("服务前切结束2 end");
    }
    
    @AfterThrowing(value="@annotation(revoked)", throwing="e")
    public void serviceException(JoinPoint point, Revoked revoked,Exception e){
        log.info("服务异常前3 exception!");
        
        String key = getKey(point);
        originalMap.remove(key);
        
        log.info("服务异常后4 exception!");
    }
    
    
    @AfterReturning(value="@annotation(revoked)",argNames="revoked,result",returning = "result")
    public void serviceEnd(JoinPoint point, Revoked revoked,Result result){
        log.info("服务调用正常完成3 service end before!");
        
        String key = getKey(point);
        if(result.getRetCode() != Result.RECODE_SUCCESS){
            originalMap.remove(key);
            return;
        }
        
        log.info("服务调用正常完成4 service end before!");
        
        OperateType type = revoked.type();
        RevokedTo re = new RevokedTo();
        re.setType(type);
        Object obj = originalMap.get(key);
        if(JsonUtil.isEmpity(obj))
            return;
        re.setObj(obj);
        String id = null;
        
        switch(type){
            case INITIALIZE:
                
                id = JsonUtil.getJson(result.getData()).getString("id");
                if(id == null)
                    return;
                if(!redisService.isEmpity(id))
                   redisService.delete(id);
                break;
                
            case NODE:
                id = JsonUtil.getJson(obj).getString("id");
                if(id == null || redisService.isEmpity(id)) // 未初始化、非法操作不允许撤销
                    return;
                break;
                
            case DETAIL:
                id = JsonUtil.getJson(obj).getString("pid");
                if(id == null || redisService.isEmpity(id)) // 未初始化、非法操作不允许撤销
                    return;
                break;
        }
        
        redisService.lPush(id, re);
        originalMap.remove(key);
        
        log.info("服务正常调用完成5 service end after!");
    }
    
    /**
     * 
     * getKey: 取有revoked注解参数的值
     * @author lijie
     * @param point
     * @return
     */
    public String getKey(JoinPoint point){
        Method method = ((MethodSignature)point.getSignature()).getMethod();
        Annotation[][] types = method.getParameterAnnotations();
        
        for(int i = 0; i< types.length; i++){
            
            for(int j = 0; j< types[i].length;j++){
                
                if(Revoked.class.isInstance(types[i][j])){
                    Revoked revoked = (Revoked)types[i][j];
                    String key = revoked.key();
                    if(JsonUtil.isEmpity(key))
                        ApplicationException.throwCodeMesg(ErrorMessage._60001.getCode(), ErrorMessage._60001.getMsg());
                    Object arg = point.getArgs()[i];
                    if(arg instanceof java.lang.String){
                        return (String)arg;
                    }else{
                        return (String)JsonUtil.getJson(arg).get(key);
                    }
                }
            }
            
        }
        return null;
    }
}

