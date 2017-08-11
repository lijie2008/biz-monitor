package com.huntkey.rx.sceo.monitor.commom.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
public class InputArgument {
	
	// 1. 需要持久化的数据集合
    private JSONArray params;

    // 2. 操作的edm类名称
    private String edmName;

    // 3. 查询的条件
    private JSONArray conditions;

    // 5. 需要返回的属性集合
    private Object[] columns;     

    // 6. 分页信息
    private JSONObject pagenation;

    // 9. 需要排序的字段
    private JSONArray orders;

    public JSONArray getData() {
        return params;
    }

    public void addData(Object data) {
    	if(data instanceof JSONObject){
	    	if(params==null){
	    		params=new JSONArray();
	    	}
	        this.params.add(data);
    	}else if(data instanceof JSONArray){
    		this.params=(JSONArray) data;
    	}
    }

    public String getEdmName() {
        return edmName;
    }

    public void setEdmName(String edmName) {
        this.edmName = edmName;
    }

    public JSONArray getConditions() {
        return conditions;
    }

    public JSONArray getParams() {
		return params;
	}

	public void setParams(JSONArray params) {
		this.params = params;
	}

	public JSONObject getPagenation() {
		return pagenation;
	}

	public void setPagenation(JSONObject pagenation) {
		this.pagenation = pagenation;
	}

	public void setConditions(JSONArray conditions) {
		this.conditions = conditions;
	}

	public void setOrders(JSONArray orders) {
		this.orders = orders;
	}

	public Object[] getColumns() {
        return columns;
    }

    public void setColumns(Object[] columns) {
        this.columns = columns;
    }

    public JSONArray getOrders() {
        return orders;
    }


    @Override
    public String toString(){
        JSONObject jsonObject = new JSONObject();
        JSONObject search = new JSONObject();
        if(!JsonUtil.isNullOrEmpty(params)){
        	jsonObject.put("params", params);
        }
        if(!StringUtil.isNullOrEmpty(edmName)){
        	jsonObject.put("edmName", edmName);
        }
        if(!JsonUtil.isNullOrEmpty(conditions)){
        	search.put("conditions", conditions);
        }
        if(columns!=null && columns.length>0){
        	search.put("columns", columns);
        }
        if(pagenation!=null){
        	search.put("pagenation", pagenation);
        }
        if(!StringUtil.isNullOrEmpty(orders)){
        	search.put("orders", orders);
        }
        if(search!=null && !search.isEmpty()){
        	jsonObject.put("search", search);
        }
        return jsonObject.toString();
    }
}
