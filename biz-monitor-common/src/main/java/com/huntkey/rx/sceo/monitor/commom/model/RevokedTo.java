/**
 * Project Name:biz-monitor-common
 * File Name:RevokedTo.java
 * Package Name:com.huntkey.rx.sceo.monitor.commom.model
 * Date:2017年8月7日上午9:34:39
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.model;

import java.io.Serializable;
import java.util.List;

import com.huntkey.rx.sceo.monitor.commom.enums.OperateType;

/**
 * ClassName:RevokedTo 撤销对象的结构
 * Date:     2017年8月7日 上午9:34:39
 * @author   lijie
 * @version  
 * @see 	 
 */
public class RevokedTo implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private List<NodeTo> nodes;
    
    private OperateType type;
    
    private String code;

    public List<NodeTo> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeTo> nodes) {
        this.nodes = nodes;
    }

    public OperateType getType() {
        return type;
    }

    public void setType(OperateType type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}

