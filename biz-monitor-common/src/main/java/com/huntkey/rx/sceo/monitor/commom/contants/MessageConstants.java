package com.huntkey.rx.sceo.monitor.commom.contants;

/**
 * Created by xuyf on 2017/5/4 0004.
 */
public interface MessageConstants {

    /**
     * 短信验证码消息
     */
    String MSG_SMS_CAPTCHA = "message.sms.captcha";

    /**
     * 邮箱验证码消息
     */
    String MSG_EMAIL_CAPTCHA = "message.email.captcha";

    /**
     * 账号注册消息标题
     */
    String MSG_SMS_CAPTCHA_TITLE_REG = "message.sms.captcha.title.register";

    /**
     * 消息发送失败提示信息
     */
    String MSG_SMS_CAPTCHA_ERR_SEND = "message.sms.captcha.error.send";

    /**
     * 手机号码格式不正确
     */
    String ERR_FORMAT_MOBILE = "error.validate.format.mobile";
    /**
     * 邮箱格式不正确
     */
    String ERR_FORMAT_EMAIL = "error.validate.format.email";

    /**
     * 验证码校验失败
     */
    String ERR_VALID_CAPTCHA = "error.validate.captcha";

    /**
     * 手机号校验失败
     */
    String ERR_VALID_MOBILE = "error.validate.mobile";

    /**
     * 验邮箱校验失败
     */
    String ERR_VALID_EMAIL = "error.validate.email";

    /**
     * 密保问题校验失败
     */
    String ERR_VALID_QUESTION = "error.validate.question";

    /**
     * 锐信号校验失败
     */
    String ERR_VALID_SCEO = "error.validate.sceo";

    /**
     * 检查无此账号
     */
    String ERR_VALID_ACCOUNT = "error.validate.account";

    /**
     * 消息发送次数用完提示
     */
    String MSG_CAPTCHA_ERR_LEFT = "message.sms.captcha.error.left";

    /**
     * 验证码类型错误
     */
    String MSG_CAPTCHA_ERR_TYPE = "message.sms.captcha.error.type";

    /**
     * 规定时间内只能发一次信息
     */
    String MSG_CAPTCHA_ERR_BUSY = "message.sms.captcha.error.busy";

}
