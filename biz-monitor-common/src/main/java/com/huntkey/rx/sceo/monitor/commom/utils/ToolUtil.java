package com.huntkey.rx.sceo.monitor.commom.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;

public class ToolUtil {
    /**
     * 日期格式转化
     * @param dateStr 日期字符串 
     * @param formatStr 日期格式
     * @return
     */
    public static  String formatDateStr(String dateStr,String formatStr) {
    	String formatDateStr=null;
    	if(!StringUtil.isNullOrEmpty(dateStr)){
	    	SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
	    	Date datetime=null;
	    	try {
				datetime=(Date) sdf.parse(dateStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				ApplicationException.throwCodeMesg(ErrorMessage._60010.getCode(),
						ErrorMessage._60010.getMsg());
			}
	    	formatDateStr= sdf.format(datetime);
    	}
    	return formatDateStr;
    }
    
    /**
     * 日期格式转化
     * @param dateStr 日期字符串 
     * @param formatStr 日期格式
     * @return
     */
    public static  String getNowDateStr(String formatStr) {
    	SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
    	String formatDateStr= sdf.format(new Date());
    	return formatDateStr;
    } 
}
