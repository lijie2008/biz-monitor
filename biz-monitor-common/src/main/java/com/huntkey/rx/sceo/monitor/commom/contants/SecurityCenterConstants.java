package com.huntkey.rx.sceo.monitor.commom.contants;

/**
 * Created by zhaomj on 2017/5/4.
 */
public interface SecurityCenterConstants {

    String UNDER_LINE = "_";
    /**
     * 类型：手机=1
     */
    int COMMON_TYPE_MOBILE = 1;

    /**
     * 类型：邮箱=2
     */
    int COMMON_TYPE_EMAIL = 2;

    /**
     * 验证码存放redis key - 手机号码(MobileSend:)
     */
    String REDIS_KEY_MOBILE = "MobileSend:";

    /**
     * 验证码存放redis key - 邮箱(EmailSend:)
     */
    String REDIS_KEY_EMAIL = "EmailSend:";

    /**
     * 消息发送频繁标志前缀
     */
    String MSG_SEND_BUSY_PREFIX = "msg_send_busy_";



}
