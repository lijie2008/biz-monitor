package com.huntkey.rx.sceo.monitor.commom.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xuyf on 2017/5/4 0004.
 */
public class ValidatorUtil {

    //禁止实例化
    private ValidatorUtil(){}

    //正则表达式常量

    /**
     * 邮箱正则
     */
    public static final String EMAIL_REGEX = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$";

    /**
     * 手机号码正则
     */
    public static final String MOBILE_REGEX = "^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$";

    /**
     * 固话号码的正则(包括验证国内区号,国际区号,分机号)
     */
    public static final String PHONE_REGEX = "^(([0\\+]\\d{2,3}-)?(0\\d{2,3})-)?(\\d{7,8})(-(\\d{3,}))?$";

    /**
     * 身份证号码的正则（包括15位和18位）
     */
    public static final String ID_CARD_REGEX = "^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$";

    /**
     * 姓名的正则（包括中文、英文）
     */
    public static final String NAME_REGEX = "^[\\u4E00-\\u9FA5A-Za-z·]+$";

    /**
     * 判断是否为正确的邮件
     * @param email
     * @return boolean
     */
    public static boolean isEmail(String email){
        return match(EMAIL_REGEX, email);
    }

    /**
     * 判断是否为正确的手机号码
     * @param mobile
     * @return boolean
     */
    public static boolean isMobile(String mobile){
        return match(MOBILE_REGEX, mobile);
    }

    /**
     * 判断是否为正确的固话号码
     * @param phone
     * @return boolean
     */
    public static boolean isPhone(String phone){
        return match(PHONE_REGEX, phone);
    }

    /**
     * 判断是否为正确的身份证号码
     * @param idCard
     * @return boolean
     */
    public static boolean isIDCard(String idCard){
        return match(ID_CARD_REGEX, idCard);
    }

    /**
     * 判断姓名是否只含中英文
     * @param name
     * @return boolean
     */
    public static boolean isName(String name) {
        return match(NAME_REGEX, name);
    }

    /**
     *
     * @param regex 正则表达式字符串
     * @param str 要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private static boolean match(String regex, String str)
    {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

}
