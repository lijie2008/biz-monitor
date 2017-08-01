package com.huntkey.rx.sceo.monitor.commom.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Created by xuyf on 2017/4/28 0028.
 */
public class BCryptUtil {

    /**
     * 采用BCrypt方式加密
     * @param characterStr 明文字符串
     * @return
     */
    public static String encode(String characterStr){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(characterStr);
    }

    /**
     * 明文字符串与加密字符串匹配
     * @param characterStr 明文字符串
     * @param encodedStr 密文字符串
     * @return
     */
    public static boolean matches(String characterStr, String encodedStr){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(characterStr, encodedStr);
    }

}
