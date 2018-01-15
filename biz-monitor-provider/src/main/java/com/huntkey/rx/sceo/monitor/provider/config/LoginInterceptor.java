/**
 * Project Name:biz-monitor-provider
 * File Name:LoginIntercepter.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.config
 * Date:2017年12月27日下午4:11:42
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.huntkey.rx.sceo.monitor.provider.service.BizFormService;

/**
 * ClassName:LoginIntercepter
 * Function: TODO ADD FUNCTION
 * Reason:	 TODO ADD REASON
 * Date:     2017年12月27日 下午4:11:42
 * @author   lijie
 * @version  
 * @see 	 
 */
@Component
public class LoginInterceptor implements HandlerInterceptor{
    
    @Autowired
    private BizFormService formService;
    
    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2,
                                Exception arg3)
            throws Exception {
        
        // TODO Auto-generated method stub
        
    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2,
                           ModelAndView arg3)
            throws Exception {
        
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2)
            throws Exception {
        
        // TODO Auto-generated method stub
        return false;
    }

}

