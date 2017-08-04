/**
 * Project Name:biz-monitor-common
 * File Name:OperateType.java
 * Package Name:com.huntkey.rx.sceo.monitor.commom.enums
 * Date:2017年8月4日下午3:43:32
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.enums;
/**
 * ClassName:OperateType 撤销操作类型
 * Date:     2017年8月4日 下午3:43:32
 * @author   lijie
 * @version  
 * @see 	 
 */
public enum OperateType {
    
    INITIALIZE(1, "初始化堆栈"),
    
    NODE(2, "节点操作"),
    
    DETAIL(3, "节点详情操作");

    private int value;
    
    private String text;
    
    OperateType(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public static OperateType valueOf(int value) {
        switch (value) {
            case 1:
                return INITIALIZE;
            case 2:
                return NODE;
            case 3:
                return DETAIL;
            default:
                return null;
        }
    }
    
}

