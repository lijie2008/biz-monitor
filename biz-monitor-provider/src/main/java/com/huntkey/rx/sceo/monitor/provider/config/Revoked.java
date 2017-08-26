/**
 * Project Name:biz-monitor-provider
 * File Name:Revoked.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.config
 * Date:2017年8月4日下午3:41:42
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.huntkey.rx.sceo.monitor.commom.enums.OperateType;

/**
 * ClassName:Revoked 撤销操作类型注解
 * Date:     2017年8月4日 下午3:41:42
 * @author   lijie
 * @version  
 * @see 	 
 */
@Target({ElementType.METHOD,ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Revoked {
    
    /**
     * type: 操作类型
     * @author lijie
     * @return
     */
    OperateType type() default OperateType.INITIALIZE;
    
    String key() default "";
    
    String character() default "";
}

