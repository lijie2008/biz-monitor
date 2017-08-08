package com.huntkey.rx.sceo.monitor.commom.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/***********************************************************************
 * @author chenxj
 * 
 * @email: kaleson@163.com
 * 
 * @date : 2017年7月13日 下午12:00:47
 * 
 *       前端调用新增、修改服务的入参对象
 * 
 **********************************************************************/
public class FullInputArgument implements InputArgs {

	// 新增或者修改的参数
	private String json;

	// edmName
	private String edmName;

	// 业务数据
	private JSONArray params;

	// 查询数据
	private JSONObject search;

	// 查询的列
	private JSONArray columns;

	public FullInputArgument(String json) {
		this.json = json;
		initParams();
	}
	
	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public String getEdmName() {
		return edmName;
	}

	public void setEdmName(String edmName) {
		this.edmName = edmName;
	}

	public JSONArray getParams() {
		return params;
	}

	public void setParams(JSONArray params) {
		this.params = params;
	}

	public JSONObject getSearch() {
		return search;
	}

	public void setSearch(JSONObject search) {
		this.search = search;
	}

	public JSONArray getColumns() {
		return columns;
	}

	public void setColumns(JSONArray columns) {
		this.columns = columns;
	}

	private void initParams() {
		JSONObject root = JSONObject.parseObject(json);

		if (root.containsKey(InputArgs.P_EDMNAME)) {
			this.setEdmName(root.getString(InputArgs.P_EDMNAME));
		}

		if (root.containsKey(InputArgs.P_PARAMS)) {
			this.setParams(root.getJSONArray(InputArgs.P_PARAMS));
		}

		if (root.containsKey(InputArgs.P_SEARCH)) {
			this.setSearch(root.getJSONObject(InputArgs.P_SEARCH));
		}

		if (root.containsKey(InputArgs.P_COLUMNS)) {
			this.setColumns(root.getJSONArray(InputArgs.P_COLUMNS));
		}
	}
}
