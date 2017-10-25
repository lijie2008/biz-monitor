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
    
    _60000(60000, "枚举类型不正确"),
    
    _60001(60001, "revoked注解在参数上时，必须指明key"),
    
    _60002(60002, "ORM调用异常"),
	
	_60003(60003, "未查到相关数据"),
    
    _60004(60004, "输入参数不正确"),
    
    _60005(60005, "记录不存在"),
    
    _60006(60006, "当前节点选择的资源已被其他节点使用, 请重新修改时间区间"),
    
    _60007(60007, "EDMClient调用异常"),
    
    _60008(60008, "EDM类数据不正确"),
	
	_60009(60009, "数据错误,删除节点未找到父节点"),
	
	_60010(60010, "传入日期格式错误"),
    
    _60011(60011, "禁止执行撤销"),
	
	_60012(60012, "不能定位到资源表"),
    
    _60013(60013, "克隆对象失败"),
    
	_60014(60014, "未找到根节点数据"),
	
	_60015(60015, "失效时间必须大于生效时间"),
	
	_60016(60016, "本节点时间区间必须在上级节点时间区间以内"),
	
	_60017(60017, "当前时间区间内,本节点在的资源被其他节点占用"),
	
	_60018(60018, "移动节点的时间段不在目标节点的时间段范围内");
    
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

