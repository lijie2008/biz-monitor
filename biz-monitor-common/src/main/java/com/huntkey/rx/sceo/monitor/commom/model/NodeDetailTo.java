/**
 * Project Name:biz-monitor-common
 * File Name:NodeTo.java
 * Package Name:com.huntkey.rx.sceo.monitor.commom.model
 * Date:2017年8月8日下午2:27:40
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;

/**
 * ClassName:NodeDetailTo
 * Date:     2017年8月8日 下午2:27:40
 * @author   lijie
 * @version  
 * @see 	 
 */
public class NodeDetailTo extends NodeTo implements Cloneable{
    
    /**
     * 关联资源对象集合
     */
    private List<ResourceTo> mtor019;
    
    public List<ResourceTo> getMtor019() {
        return mtor019;
    }

    public void setMtor019(List<ResourceTo> mtor019) {
        this.mtor019 = mtor019;
    }
    
    /**
     * 
     * applyMapper: 临时单 - 和目标表字段对应关系
     * @author lijie
     * @return
     */
    private static Map<String,String> applyMapper(){
        Map<String,String> map = new HashMap<String, String>();
        map.put("id","id");
        map.put("mtor006","moni001");
        map.put("mtor007","moni002");
        map.put("mtor008","moni003");
        map.put("mtor011","moni004");
        map.put("mtor012","moni005");
        map.put("mtor013","moni006");
        map.put("mtor014","moni007");
        map.put("mtor015","moni008");
        map.put("mtor016","moni009");
        map.put("mtor017","moni013");
        map.put("mtor018","moni014");
        map.put("mtor022","moni017");
        return map;
    }
    
    /**
     * 
     * parseArrayMapper: list字段的转换
     * @author lijie
     * @param array
     * @param map
     * @return
     */
    public static JSONArray parseArrayMapper(JSONArray array){
        Map<String,String> map = applyMapper();
        JSONArray copy = new JSONArray();
        if(JsonUtil.isEmpity(map) || JsonUtil.isEmpity(array)){
            return copy;
        }
        Iterator<Object> it = array.iterator();
        while(it.hasNext()){
            JSONObject obj = (JSONObject)it.next();
            JSONObject copy_obj = new JSONObject();
            for(Entry<String, String> entry : map.entrySet()){
                String key = entry.getKey();
                String value = entry.getValue();
                if(obj.containsKey(value)){
                    Object obj_value = obj.get(value);
                    copy_obj.put(key, obj_value);
                }else{
                    copy_obj.put(key,obj.get(key));
                }
            }
            copy.add(copy_obj);
        }
        return copy;
    }

    @Override
    public NodeDetailTo clone() throws CloneNotSupportedException {
        return (NodeDetailTo)super.clone();
    }

    
}

