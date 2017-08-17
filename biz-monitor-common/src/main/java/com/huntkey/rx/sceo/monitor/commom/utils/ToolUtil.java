package com.huntkey.rx.sceo.monitor.commom.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
    
    /**
     * 正式树节点字段转换成临时树节点字段
     * @param dateStr 日期字符串 
     * @param formatStr 日期格式
     * @return
     */
    public static  JSONArray formal2Temp(JSONArray treeFormal,Map<String, String> map) {
    		
    	if(JsonUtil.isNullOrEmpty(treeFormal))
    		return null;
    	
    	JSONArray copy_arr=new JSONArray();
    	for(Object obj:treeFormal){
    		copy_arr.add(formal2Temp(JsonUtil.getJson(obj),map));
    	}
    	return copy_arr;
    } 
    
    public static Map<String, String> treeConvert(){
    	Map<String, String> json=new HashMap<String, String>();
    	
    	json.put("moni001", "mtor006");
    	json.put("moni002", "mtor007");
    	json.put("moni003", "mtor008");
    	json.put("moni004", "mtor011");
    	
    	json.put("moni005", "mtor012");
    	json.put("moni006", "mtor013");
    	json.put("moni007", "mtor014");
    	json.put("moni008", "mtor015");
    	
    	json.put("moni009", "mtor016");
    	json.put("moni013", "mtor017");
    	json.put("moni014", "mtor018");
    	json.put("moni017", "mtor022");
    	json.put("id", "id");
    	json.put("pid", "pid");
    	return json;
    }
    public static Map<String, String> resourceConvert(){
    	Map<String, String> json=new HashMap<String, String>();
    	json.put("moni016", "mtor020");
    	json.put("id", "id");
    	json.put("pid", "pid");
    	return json;
    }
    /**
     * 正式树节点字段转换成临时树节点字段
     * @param dateStr 日期字符串 
     * @param formatStr 日期格式
     * @return
     */
    public static  JSONObject formal2Temp(JSONObject treeFormal,Map<String, String> map) {
    		
    	if(JsonUtil.isEmpity(treeFormal))
    		return null;
    	
    	JSONObject copy_obj=new JSONObject();
    	for(Entry<String, Object> entry : treeFormal.entrySet()){
    		String key = entry.getKey();
            Object value = entry.getValue();
            if(map.containsKey(key)){
                String convertField = map.get(key);
                copy_obj.put(convertField, value);
            }
    	}
    	return copy_obj;
    } 
}
