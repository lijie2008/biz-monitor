package com.huntkey.rx.sceo.monitor.commom.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.sceo.monitor.commom.constant.Constant;
import com.huntkey.rx.sceo.monitor.commom.exception.ServiceException;

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
				throw new ServiceException("传入日期格式错误！");
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
    	json.put("moni019", "mtor009");
    	json.put("moni020", "mtor010");    	
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
    /**
     * 日期比较时分秒
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    public static  Boolean dateCompare(String dateBegin,String dateEnd) {
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		Date date1,date2;
		Boolean b=false;
		if(StringUtil.isNullOrEmpty(dateBegin)){
			dateBegin=getNowDateStr(Constant.YYYY_MM_DD)+Constant.STARTTIME;
		}else if(dateBegin.length()==10){
			dateBegin=dateBegin+Constant.STARTTIME;
		}
		if(StringUtil.isNullOrEmpty(dateEnd)){
			dateEnd=getNowDateStr(Constant.YYYY_MM_DD)+Constant.STARTTIME;
		}else if(dateEnd.length()==10){
			dateEnd=dateEnd+Constant.STARTTIME;
		}
		try {
			date1 = df.parse(dateBegin);
			date2=df.parse(dateEnd);
			if(date1.getTime()<date2.getTime())
			{
				b=true;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return b;
		
    }
    
    public static Date getDate(String str){
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            return format.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException("日期转换错误"+ str);
        }
    }
}
