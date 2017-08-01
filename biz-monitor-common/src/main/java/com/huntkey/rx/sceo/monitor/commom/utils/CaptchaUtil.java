package com.huntkey.rx.sceo.monitor.commom.utils;

import java.util.Random;

/**
 * Created by xuyf on 2017/4/27 0027.
 */
public class CaptchaUtil {

    /**
     * 获取验证码
     * @param charCount 验证码位数
     * @return
     */
    public static String getRandNum(int charCount) {
        StringBuffer charValue = new StringBuffer();
        for (int i = 0; i < charCount; i++) {
            char c = (char) (randomInt(0, 10) + '0');
            charValue.append(String.valueOf(c));
        }
        return charValue.toString();
    }

    private static int randomInt(int from, int to) {
        Random r = new Random();
        return from + r.nextInt(to - from);
    }

}
