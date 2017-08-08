/**
 * Project Name:biz-monitor-common
 * File Name:MonitorTreeOrderTo.java
 * Package Name:com.huntkey.rx.sceo.monitor.commom.model
 * Date:2017年8月5日下午5:15:35
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.model;

import java.io.Serializable;

/**
 * ClassName:MonitorTreeOrderTo 临时单To
 * Date:     2017年8月5日 下午5:15:35
 * @author   lijie
 * @version  
 * @see 	 
 */
public class MonitorTreeOrderTo implements Serializable{
    
    private static final long serialVersionUID = 1L;

    /**
     * 临时单id
     */
    private String id;
    
    /**
     * 单据编号
     */
    private String mtor001;
    
    /**
     * 变更类型
     */
    private int mtor002;
    
    /**
     * EDM类
     */
    private String mtor003;
    
    /**
     * 变更树对象根节点
     */
    private String mtor004;
    


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMtor001() {
        return mtor001;
    }

    public void setMtor001(String mtor001) {
        this.mtor001 = mtor001;
    }

    public int getMtor002() {
        return mtor002;
    }

    public void setMtor002(int mtor002) {
        this.mtor002 = mtor002;
    }

    public String getMtor003() {
        return mtor003;
    }

    public void setMtor003(String mtor003) {
        this.mtor003 = mtor003;
    }

    public String getMtor004() {
        return mtor004;
    }

    public void setMtor004(String mtor004) {
        this.mtor004 = mtor004;
    }
    
}

