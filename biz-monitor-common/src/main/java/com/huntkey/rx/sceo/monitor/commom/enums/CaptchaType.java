package com.huntkey.rx.sceo.monitor.commom.enums;

/**
 * Created by zhaomj on 2017/5/17.
 */
public enum CaptchaType {

    REG_ACCOUNT(1, "register account"),
    FIND_PASSWORD(2, "find password"),
    MODIFY_PASSWORD(3, "modify password"),
    MODIFY_MOBILE(4, "modify mobile"),
    MODIFY_EMAIL(5, "modify email"),
    MODIFY_LOGIN_NAME(6, "modify login name"),
    RESET_PASSWORD(7, "reset password"),
    MODIFY_QUESTION(8, "reset question");

    @SuppressWarnings("unused")
    private final int value;
    
    @SuppressWarnings("unused")
    private final String text;

    CaptchaType(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public static CaptchaType valueOf(int value) {
        switch (value) {
            case 1:
                return REG_ACCOUNT;
            case 2:
                return FIND_PASSWORD;
            case 3:
                return MODIFY_PASSWORD;
            case 4:
                return MODIFY_MOBILE;
            case 5:
                return MODIFY_EMAIL;
            case 6:
                return MODIFY_LOGIN_NAME;
            case 7:
                return RESET_PASSWORD;
            case 8:
                return MODIFY_QUESTION;
            default:
                return null;
        }

    }



}
