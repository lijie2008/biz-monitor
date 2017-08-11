/**
 * Project Name:security-center-common
 * File Name:JsonUtil.java
 * Package Name:com.huntkey.rx.sceo.security.center.commom.utils
 * Date:2017年7月3日下午5:49:36
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.utils;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * ClassName:JsonUtil json操作工具类
 * @author   lijie
 * @version  
 * @see 	 
 */
public class JsonUtil {
    
    /**
     * 
     * listToJsonArray:将list转成 JSONArray
     * @author lijie
     * @param list
     * @return
     */
    public static JSONArray listToJsonArray(List<?> list){
        if(isEmpity(list)){
            return null;
        }
        return JSON.parseArray(JSON.toJSONString(list));
    }
    
    /**
     * 
     * getObject:Json字符串 转 实体对象  
     * @author lijie
     * @param jsonStr
     * @param clazz
     * @return
     * @throws IOException
     */
    public static final <T> T getObject(String jsonStr,Class<T> clazz) {
        if(isEmpity(jsonStr)){
            return null;
        }
        return JSONObject.parseObject(jsonStr, clazz);  
    }
    
    
    /**
     * 
     * getJsonString:对象 转 Json字符串  
     * @author lijie
     * @param o
     * @return
     * @throws IOException
     */
    public static final String getJsonString(Object o) {
        if(isEmpity(o)){
            return null;
        }
        return JSONObject.toJSONString(o);  
    } 
    
    /**
     * 
     * getJsonArrayByAttr:将某个对象的某个 数组属性取出作为 JSONArray
     * @author lijie
     * @param o
     * @param attr
     * @return
     * @throws IOException 
     */
    @SuppressWarnings("unchecked")
    public static final JSONArray getJsonArrayByAttr(Object o,String attr) {
        if(isEmpity(o)){
            return null;
        }
        JSONArray arry= null;
        try{
            JSONObject json = new JSONObject((Map<String, Object>)o);
            arry = json.getJSONArray(attr);
        }catch(Exception e){
            throw new RuntimeException("json数据不正确!",e);
        }
        return arry;
    }
    
    /**
     * 
     * getJson:对象 转 JSONObject对象  此处的对象 不能是String类型
     * @author lijie
     * @param o
     * @return
     * @throws IOException
     */
    public static final JSONObject getJson(Object o) {  
        if(isEmpity(o)){
            return null;
        }
        try{
            return (JSONObject)JSONObject.toJSON(o);  
        }catch(Exception e){
            throw new RuntimeException("json数据不正确!",e);
        }
    }
    
    
    /**
     * 
     * getJsonArrayString:数组对象 转 Json字符串 
     * @author lijie
     * @param o
     * @return
     * @throws IOException
     */
    public static final String getJsonArrayString(Object o) { 
        if(isEmpity(o)){
            return null;
        }
        return JSONArray.toJSONString(o);  
    }  
    
    /**
     * 
     * getJsonObject:将字符串转成jsonOBJECT
     * @param str
     * @return
     */
    public static final JSONObject getJsonObject(String str){
        if(isEmpity(str)){
            return null;
        }
        return JSONObject.parseObject(str);
    }
    
    /**
     * getJsonArray:将格式化的str转成JSONARRAY "[\"bill\",\"green\",\"maks\",\"jim\"]"
     * @author lijie
     * @param str
     * @return
     */
    public static final JSONArray getJsonArray(String str){
        if(isEmpity(str)){
            return null;
        }
        return JSON.parseArray(str);
    }
    
    
    /**
     * 
     * mergeJsonObject: 两个json对象合并
     * @author lijie
     * @param a
     * @param b
     * @return
     */
    public static final JSONObject mergeJsonObject(JSONObject a, JSONObject b){
        if(isEmpity(a) || isEmpity(b)){
            return a==null? b:a;
        }
        JSONObject obj = new JSONObject();
        for(Entry<String, Object> entry : a.entrySet()){
            obj.put(entry.getKey(), entry.getValue());
        }
        for(Entry<String, Object> entry : b.entrySet()){
            obj.put(entry.getKey(), entry.getValue());
        }
        return obj; 
    }
    
    /**
     * 两个JSONArray合并
     * @param a
     * @param b
     * @return
     */
    public static final JSONArray mergeJsonArray(JSONArray a , JSONArray b){
    	JSONArray arry = new JSONArray();
    	if(!isEmpity(a)){
    		Iterator<Object> it = a.iterator();
    		while(it.hasNext()){
    			JSONObject obj = (JSONObject)it.next();
    			arry.add(obj);
    		}
    	}
    	if(!isEmpity(b)){
    		Iterator<Object> it = b.iterator();
    		while(it.hasNext()){
    			JSONObject obj = (JSONObject)it.next();
    			arry.add(obj);
    		}
    	}
    	return arry;
    }
    
    /**
     * 
     * getList: 将jsonarry  转成 需要的class 在定义的实体类中需定义@JSONField
     * @author lijie 
     * @param arry
     * @param clazz 
     * @return
     */
    public static <T> List<T> getList(List<Object> arry,Class<T> clazz){
        List<T> list = new ArrayList<T>();
        if(isEmpity(arry)){
            return null;
        }
        try{
            Iterator<Object> it = arry.iterator();
            while(it.hasNext()){
                JSONObject obj = JsonUtil.getJson(it.next());
                list.add(JSONObject.toJavaObject(obj, clazz));
            }
        }catch(Exception e){
            throw new RuntimeException("json数据不正确!",e);
        }
        return list;
    }
    
    /**
     * 给JsonArray 中增加属性值
     * @param a
     * @param attr
     * @param map  key - value
     * @return
     */
    public static final JSONArray addAttr(JSONArray a, Map<String,Object> map){
        JSONArray arry = new JSONArray();
    	if(isEmpity(a)){
    	    return arry;
    	}
    	Iterator<Object> it = a.iterator();
    	while(it.hasNext()){
    	    JSONObject obj = (JSONObject)((JSONObject)it.next()).clone();
    	    for(Entry<String, Object> entry1 : map.entrySet()){
                obj.put(entry1.getKey(), entry1.getValue());
            }
    	    arry.add(obj);
    	}
    	return arry;
    }
    
    /**
     * 给JsonArray 中删除属性值
     * @param a
     * @param attrs
     * @return
     */
    public static final JSONArray removeAttr(JSONArray a, String... attrs){
        
        JSONArray arry = new JSONArray();
        if(isEmpity(a) || isEmpity(attrs[0])){
            return arry;
        }
        Iterator<Object> it = a.iterator();
        while(it.hasNext()){
            JSONObject obj = (JSONObject)((JSONObject)it.next()).clone();
            for(String attr : attrs)
                obj.remove(attr);
            arry.add(obj);
        }
        return arry;
    }
    
    /**
     * 
     * isEmpity:判断对象是否为空
     * @author lijie
     * @param o
     * @return
     */
    public static Boolean isEmpity(Object o){
        if(o == null){
            return true;
        }else if(o instanceof Map){
            if(((Map<?,?>) o).isEmpty()){
                return true;
            }
        }else if(o instanceof List<?>){
            if(((List<?>) o).isEmpty()){
                return true;
            }
        }else if(o instanceof String){
            if(((String) o).length() == 0){
                return true;
            }
        }else if(o instanceof Object[]){
            if(((Object[]) o).length == 0){
                return true;
            }
        }
        return false;
    }
    
    /**
     * 
     * validDate:比较当前时间是否在 生效时间 和 失效时间之间
     *  临时使用
     *  date ∈ [ beforeDate, afterDate)
     * @author lijie
     * @param beforeDate
     * @param afterDate
     * @return
     */
    public static Boolean validDate(String beforeDate, String afterDate){
        try{
            Date b_date = Date.valueOf(beforeDate);
            Date m_date = new Date(System.currentTimeMillis());
            Date a_date = Date.valueOf(afterDate);
            if(!m_date.before(b_date) && m_date.before(a_date)){
                return true;
            }
        }catch(Exception e){
            
        }
        return false;
    }
    /**
     * 校验jsonarray是否为null 或者 大小为0
     * @param jsonArr
     * @return
     */
    public static Boolean isNullOrEmpty(JSONArray jsonArr){
    	if(jsonArr==null || jsonArr.size()<1){
    		return true;
    	}
        return false;
    }
}
