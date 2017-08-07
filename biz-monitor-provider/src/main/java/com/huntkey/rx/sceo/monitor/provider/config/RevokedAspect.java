/**
 * Project Name:biz-monitor-provider
 * File Name:RevokedAspect.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.config
 * Date:2017年8月7日下午2:33:47
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Component;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.enums.OperateType;

/**
 * ClassName:RevokedAspect
 * Function: TODO ADD FUNCTION
 * Reason:	 TODO ADD REASON
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
    
    @Before(value="@annotation(revoked)",argNames="revoked")
    public void serviceStart(JoinPoint point, Revoked revoked){
        log.info("服务开始前 service start before!");
        OperateType type = revoked.type();
        System.out.println("type : " + type);
        log.info("服务前切结束 end");
    }
    
    @AfterThrowing(value="@annotation(revoked)", throwing="e")
    public void serviceException(JoinPoint point, Revoked revoked,Exception e){
        
        
        
    }
    
    
    @AfterReturning(value="@annotation(revoked)",argNames="revoked,result",returning = "result")
    public void serviceEnd(JoinPoint point, Revoked revoked,Result result){
        
    }
}

