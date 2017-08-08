package com.huntkey.rx.sceo.monitor.commom.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/***********************************************************************
 * @author chenxj
 * 
 * @email: kaleson@163.com
 * 
 * @date : 2017年7月13日 上午11:37:38
 * 
 **********************************************************************/
public interface InputArgs {
	public static String P_EDMNAME = "edmName";
	public static String P_DATASET = "dataset";
	public static String P_PARAMS = "params";
	public static String P_SEARCH = "search";
	public static String P_COLUMNS = "columns";
	public static String P_CONDITIONS = "conditions";
	public static String P_ES = "esFileds";
	public static String P_ID = "id";
	public static String P_PID = "pid";

	/**
	 * 获取EDM 模型类名称
	 * 
	 * @return
	 */
	public String getEdmName();

	/**
	 * 获取业务数据
	 * 
	 * @return
	 */
	public JSONArray getParams();

	/**
	 * 获取查询数据
	 * 
	 * @return
	 */
	public JSONObject getSearch();
	
	/**
	 * 获取原始的json数据
	 * 
	 * @return
	 */
	public String getJson();
	
	/**
	 * 获取查询的列
	 * 
	 * @return
	 */
	public JSONArray getColumns();
}
