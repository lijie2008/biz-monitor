/**
 * Project Name:biz-monitor-provider
 * File Name:WebAppConfig.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.config
 * Date:2017年12月27日下午4:15:31
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * ClassName:WebAppConfig
 * Function: TODO ADD FUNCTION
 * Reason:	 TODO ADD REASON
 * Date:     2017年12月27日 下午4:15:31
 * @author   lijie
 * @version  
 * @see 	 
 */
public class WebAppConfig extends WebMvcConfigurerAdapter{
    
    @Autowired
    private LoginInterceptor 
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        
        
        super.addInterceptors(registry);
    }
    
}

