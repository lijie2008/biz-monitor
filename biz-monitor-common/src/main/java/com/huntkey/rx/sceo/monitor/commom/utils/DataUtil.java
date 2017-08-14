/**
 * Project Name:security-center-common
 * File Name:JsonUtil.java
 * Package Name:com.huntkey.rx.sceo.security.center.commom.utils
 * Date:2017年7月3日下午5:49:36
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.sceo.monitor.commom.model.JoinTO;

/**
 * ClassName:JsonUtils json资源的操作 例如合并等
 * @author   fk
 * @version  
 * @see 	 
 */
public class DataUtil {
    
	/***
	 * 两个数据集 连接
	 * @param sourceJsonArr 主表数据集
	 * @param mergeJsonArr 从表数据
	 * @param joinInfo
	 * @return
	 */
    public static final JSONArray mergeJsonArray(JSONArray sourceJsonArr,JSONArray mergeJsonArr,
			JoinTO joinInfo){
			JSONObject sourceJson=null;
			JSONArray joinJson=null;
			JSONArray jsonArrNew=new JSONArray();
			String[] cols= joinInfo.getJoinColumns();
			if(sourceJsonArr!=null && sourceJsonArr.size()>0){//如果主表数据集不为空
				for(Object obj :sourceJsonArr){//主表循环连接从表
					sourceJson=JsonUtil.getJson(obj);
					joinJson=joinRecord(sourceJson,mergeJsonArr,joinInfo);//循环一次获得到的结果集
					if(!JsonUtil.isNullOrEmpty(joinJson)){
						jsonArrNew=JsonUtil.mergeJsonArray(jsonArrNew,joinJson);	
					}else{//如果未得到结果集  说明没有匹配
						for(String key :cols){
							sourceJson.put(key, "");
						}
						jsonArrNew.add(sourceJson);
					}
				}
			}
			return jsonArrNew;
		}
	/***
	 * 记录循环匹配
	 * @param sourceJson
	 * @param mergeJsonArr
	 * @param joinInfo
	 * @return
	 */
	public static final JSONArray joinRecord(JSONObject sourceJson,JSONArray mergeJsonArr,
			JoinTO joinInfo){
		JSONObject mergeObj=null;
		JSONArray jsonArrNew=new JSONArray();
		JSONObject joinObj=null;
		String[] column=null;
		if(joinInfo!=null){
			column=joinInfo.getJoinColumns();
		}
		if(!JsonUtil.isNullOrEmpty(mergeJsonArr)){
			for(Object json: mergeJsonArr){
				mergeObj=JsonUtil.getJson(json);
				joinObj=addBatchInner(sourceJson,mergeObj,joinInfo);//逐条遍历添加字段
				if(joinObj!=null){
					jsonArrNew.add(joinObj);
				}
			}
		}
		if(jsonArrNew==null || jsonArrNew.size()<1){
			for(String key:column){
				sourceJson.put(key, "");
			}
			jsonArrNew.add(sourceJson);
		}
			
		return jsonArrNew;
	}
	//json批量添加key value (做inner)
	public static final JSONObject addBatchInner(JSONObject o,JSONObject m,
			JoinTO joinInfo){
		String sourceKey="";  
		String mergeKey="";		
		String[] column=null;
		if(joinInfo!=null){
			sourceKey=joinInfo.getSourceKey();
			mergeKey=joinInfo.getMergeKey();
			column=joinInfo.getJoinColumns();
		}else{
			return o;
		}
		if(o==null)
			return o;
		if(m!=null){
			if(StringUtil.isEqual(o.getString(sourceKey),m.getString(mergeKey))){
				for(String key :column){
					o.put(key, m.getString(key));
				}
			}else{
				o=null;
			}
		}else{
			o=null;
		}
		return o;
	}
    
	
}
