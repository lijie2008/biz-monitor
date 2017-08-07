/**
 * Project Name:biz-monitor-common
 * File Name:ChangeType.java
 * Package Name:com.huntkey.rx.sceo.monitor.commom.enums
 * Date:2017年8月5日下午5:27:43
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * ClassName:ChangeType 变更类型
 * Date:     2017年8月5日 下午5:27:43
 * @author   lijie
 * @version  
 * @see 	 
 */
public enum ChangeType {
    
    ADD(1, "新增"),
    
    UPDATE(2, "修改"),
    
    INVALID(3, "失效");

    private int value;
    
    private String text;
    
    ChangeType(int value, String text) {
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
    
    public static ChangeType valueOf(int value) {
        
        switch (value) {
            case 1:
                return ADD;
            case 2:
                return UPDATE;
            case 3:
                return INVALID;
            default:
                return null;
        }
    }
    
    @JsonValue
    public String toString() {
        return Integer.toString(this.value);
    }
}

