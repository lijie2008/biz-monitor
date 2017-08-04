/**
 * Project Name:biz-monitor-common
 * File Name:ErrorCodeConstants.java
 * Package Name:com.huntkey.rx.sceo.monitor.commom.contants
 * Date:2017年8月3日下午5:35:41
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.enums;
/**
 * ClassName:ErrorMessage 错误码  60000 - 70000
 * Date:     2017年8月3日 下午5:35:41
 * @author   lijie
 * @version  
 * @see 	 
 */
public enum ErrorMessage {
    
    _60000(60000, "初始化堆栈"),
    
    _60001(60001, "节点操作"),
    
    _60002(60002, "节点详情操作");
        
    private int code;
    
    private String msg;
    
    ErrorMessage(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
}

