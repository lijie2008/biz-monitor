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
import java.util.List;

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
    private String mtor_002;
    
    /**
     * 变更类型
     */
    private String mtor_003;
    
    /**
     * EDM类
     */
    private String mtor_004;
    
    /**
     * 变更树对象根节点
     */
    private String mtor_005;
    
    /**
     * 关联的节点集合
     */
    private List<NodeTo> mtor_006;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMtor_002() {
        return mtor_002;
    }

    public void setMtor_002(String mtor_002) {
        this.mtor_002 = mtor_002;
    }

    public String getMtor_003() {
        return mtor_003;
    }

    public void setMtor_003(String mtor_003) {
        this.mtor_003 = mtor_003;
    }

    public String getMtor_004() {
        return mtor_004;
    }

    public void setMtor_004(String mtor_004) {
        this.mtor_004 = mtor_004;
    }

    public String getMtor_005() {
        return mtor_005;
    }

    public void setMtor_005(String mtor_005) {
        this.mtor_005 = mtor_005;
    }

    public List<NodeTo> getMtor_006() {
        return mtor_006;
    }

    public void setMtor_006(List<NodeTo> mtor_006) {
        this.mtor_006 = mtor_006;
    }
    
}

