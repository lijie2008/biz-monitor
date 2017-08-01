package com.huntkey.rx.sceo.monitor.commom.utils;

import com.huntkey.rx.commons.utils.string.StringUtil;

/**
 * Created by xuyf on 2017/5/22 0022.
 */
public class MaskUtil {

    /**
     * 为11位的手机号添加掩码
     * @param mobileNumber
     * @return
     */
    public static String addMaskForMobileNumber(String mobileNumber){
        if (StringUtil.isNullOrEmpty(mobileNumber)){
            return mobileNumber;
        }
        String maskMobileNumber = mobileNumber.replaceAll("(\\d{3})\\d{6}(\\d{2})","$1******$2");
        return maskMobileNumber;
    }

    /**
     * 为邮箱添加掩码
     * @param email
     * @return
     */
    public static String addMaskForEmail(String email){
        if (StringUtil.isNullOrEmpty(email)){
            return email;
        }
        int userNameLen = email.substring(0,email.indexOf("@")).length();
        int maskLen =  userNameLen >=2 ? userNameLen- 2 : 2;
        String maskEmail = email.replaceAll("(\\w{2})\\w{" + maskLen + "}(@.)","$1...$2");
        return maskEmail;
    }

    /**
     * 为身份证号码添加掩码
     * @param idCard
     * @return
     */
    public static String addMaskForIdCard(String idCard){
        if (StringUtil.isNullOrEmpty(idCard)){
            return idCard;
        }
        String maskIdNumber = idCard.replaceAll("(\\d{4})\\d{10}(\\w{4})","$1**********$2");
        return maskIdNumber;
    }

}
