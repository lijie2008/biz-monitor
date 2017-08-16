/**
 * Project Name:biz-monitor-common
 * File Name:ResourceTo.java
 * Package Name:com.huntkey.rx.sceo.monitor.commom.model
 * Date:2017年8月5日下午5:25:59
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.model;

import java.io.Serializable;

/**
 * ClassName:ResourceTo 节点关联资源信息
 * Date:     2017年8月5日 下午5:25:59
 * @author   lijie
 * @version  
 * @see 	 
 */
public class ResourceTo implements Serializable{
    
    private static final long serialVersionUID = 1L;

    /**
     * 关联资源id
     */
    private String id;
    
    /**
     * 关联节点id
     */
    private String pid;
    
    /**
     * 资源对象
     */
    private String mtor020;

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

    public String getMtor020() {
        return mtor020;
    }

    public void setMtor020(String mtor020) {
        this.mtor020 = mtor020;
    }

    @Override
    public boolean equals(Object obj) {
        if(((ResourceTo)obj).getId().equals(this.id))
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
}

