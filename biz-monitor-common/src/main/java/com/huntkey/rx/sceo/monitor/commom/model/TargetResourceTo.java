/**
 * Project Name:biz-monitor-common
 * File Name:TargetResourceTo.java
 * Package Name:com.huntkey.rx.sceo.monitor.commom.model
 * Date:2017年8月9日下午5:56:08
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.model;

import com.alibaba.fastjson.annotation.JSONField;

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
    
    /**
     * 创建人
     */
    private String adduser;
    
    /**
     * 修改人
     */
    private String moduser;

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
    
    @JSONField(name="mtor020")
    public void setMoni016(String moni016) {
        this.moni016 = moni016;
    }

    public String getAdduser() {
        return adduser;
    }

    public void setAdduser(String adduser) {
        this.adduser = adduser;
    }

    public String getModuser() {
        return moduser;
    }

    public void setModuser(String moduser) {
        this.moduser = moduser;
    }
    
    
}

