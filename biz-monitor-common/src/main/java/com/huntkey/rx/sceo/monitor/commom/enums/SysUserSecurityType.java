package com.huntkey.rx.sceo.monitor.commom.enums;

/**
 * Created by xuyf on 2017/4/27 0027.
 */
public enum SysUserSecurityType {

    MODIFY_PASSWORD(0,"修改密码"),

    MODIFY_MOBILE_NUMBER(1,"认证手机号码"),

    MODIFY_EMAIL(2,"认证邮箱"),

    MODIFY_LOGIN_NAME(3,"修改锐信号"),

    MODIFY_SECRET_QUESTION(4,"修改密保问题");

    private final int value;
    private final String desc;

    private SysUserSecurityType(int value, String desc){
        this.value = value;
        this.desc = desc;
    }

    public String toString() {
        return Integer.toString(this.value);
    }
}
