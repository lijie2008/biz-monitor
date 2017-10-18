/**
 * Project Name:biz-monitor-common
 * File Name:ResourceTo.java
 * Package Name:com.huntkey.rx.sceo.monitor.commom.model
 * Date:2017年10月17日上午10:50:52
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.model;

import java.io.Serializable;

/**
 * ClassName:ResourceTo 资源信息
 * Date:     2017年10月17日 上午10:50:52
 * @author   lijie
 * @version  
 * @see 	 
 */
public class ResourceTo implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private String resId;
    
    private String text;

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
}

