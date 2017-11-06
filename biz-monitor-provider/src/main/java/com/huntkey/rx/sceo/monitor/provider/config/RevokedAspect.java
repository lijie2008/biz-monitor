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

import javax.annotation.Resource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.enums.OperateType;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.commom.model.RevokedTo;

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
    
    private static final String REVOKE_KEY = "REVOKE";
    
    private static final String KEY = "key";
    private static final String LVLCODE = "lvlCode";
    
    private static final Map<String,Object> originalMap = new ConcurrentHashMap<String, Object>();
    
    @Resource(name="redisTemplate")
    private HashOperations<String,String,NodeTo> hashOps;
    
    @Resource(name="redisTemplate")
    private ListOperations<String, RevokedTo> listOps;
    
    // 服务前
    @Before(value="@annotation(revoked)",argNames="revoked")
    public void serviceStart(JoinPoint point, Revoked revoked) {
        
        JSONObject args = getKey(point);
        
        String key = args.getString(KEY);
        String lvlCode = args.getString(LVLCODE);

        if(StringUtil.isNullOrEmpty(key))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "临时单key" + ErrorMessage._60005.getMsg());
        
        if(OperateType.QUERY == revoked.type())
            return;
        
        if(OperateType.DETAIL == revoked.type() && StringUtil.isNullOrEmpty(lvlCode))
            ApplicationException.throwCodeMesg(ErrorMessage._60004.getCode(), LVLCODE + ErrorMessage._60004.getMsg());
        
        // 提前保留一份记录
        List<NodeTo> nodes = hashOps.values(key);
        
        RevokedTo to = new RevokedTo();
        to.setNodes(nodes);
        to.setType(revoked.type());
        
        if(OperateType.DETAIL == revoked.type())
            to.setCode(lvlCode);
        else
            to.setCode(key);
        originalMap.put(key+lvlCode, to);
    }
    
    // 服务正常完成后
    @AfterReturning(value="@annotation(revoked)",argNames="revoked,result",returning = "result")
    public void serviceEnd(JoinPoint point, Revoked revoked,Result result){
        
        JSONObject data = new JSONObject();
        JSONObject args = getKey(point);
        
        String key = args.getString(KEY);
        
        String lvlCode = args.getString(LVLCODE);
        
        if(revoked.type() == OperateType.QUERY){
            data.put("data", result.getData());
            data.put("revoke", listOps.size(key+REVOKE_KEY));
            result.setData(data);
            return;
        }
        
        
        listOps.leftPush(key+REVOKE_KEY, (RevokedTo)originalMap.get(key+lvlCode));
        
        originalMap.remove(key+lvlCode);
        
        if(OperateType.DETAIL == revoked.type()){
            data.put("data", result.getData());
            data.put("revoke", listOps.size(key+REVOKE_KEY));
            result.setData(data);
        }
    }
    
    
    @AfterThrowing(value="@annotation(revoked)", throwing="e")
    public void serviceException(JoinPoint point, Revoked revoked,Exception e){
        
        JSONObject args = getKey(point);
        
        String key = args.getString(KEY);
        String lvlCode = args.getString(LVLCODE);
        
        if(!StringUtil.isNullOrEmpty(key))
            originalMap.remove(key+lvlCode);
        
    }
    
    /**
     * getKey: 取有revoked注解参数的值
     * @author lijie
     * @param point
     * @return
     */
    public JSONObject getKey(JoinPoint point){
        
        Method method = ((MethodSignature)point.getSignature()).getMethod();
        
        Annotation[][] types = method.getParameterAnnotations();
        
        JSONObject obj = new JSONObject();
        
        for(int i = 0; i< types.length; i++){
            
            for(int j = 0; j< types[i].length;j++){
                
                if(Revoked.class.isInstance(types[i][j])){
                    Revoked revoked = (Revoked)types[i][j];
                    Object arg = point.getArgs()[i];
                    
                    if(!StringUtil.isNullOrEmpty(revoked.key())){
                        obj.put(revoked.key(), (String)point.getArgs()[i]);
                    }else{
                        JSONObject node = JSON.parseObject(JSON.toJSONString(arg));
                        obj.put(KEY, node.getString(KEY));
                        obj.put(LVLCODE, node.getString(LVLCODE));
                    }
                }
            }
        }
        return obj;
    }
}

