/**
 * Project Name:biz-monitor-common
 * File Name:TargetResourceTo.java
 * Package Name:com.huntkey.rx.sceo.monitor.commom.model
 * Date:2017年8月9日下午5:56:08
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.model;
/**
 * ClassName:TargetResourceTo 目标表关联资源属性集
 * Date:     2017年8月9日 下午5:56:08
 * @author   lijie
 * @version  
 * @see 	 
 */
public class TargetResourceTo {
    
    private String id;
    
    private String pid;
    
    /**
     * 资源对象
     */
    private String moni016;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getMoni016() {
        return moni016;
    }

    public void setMoni016(String moni016) {
        this.moni016 = moni016;
    }
    
}

