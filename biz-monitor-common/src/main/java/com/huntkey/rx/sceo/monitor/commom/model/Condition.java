package com.huntkey.rx.sceo.monitor.commom.model;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
public class Condition {
	private String attr;
	private String operator;
	private String value;
	private JSONArray condition;
	private JSONObject pagenation;
	private JSONObject orderBy;
	public JSONObject getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String attr,String sort) {
		if(orderBy==null){
			orderBy=new JSONObject();
		}
		orderBy.put("attr", attr);
		orderBy.put("sort", sort);
	}
	public JSONObject getPagenation() {
		return pagenation;
	}
	public void setPagenation(int startPage,int rows) {
		if(pagenation==null){
			pagenation=new JSONObject();
		}
		pagenation.put("startPage", startPage);
		pagenation.put("rows", rows);
	}
	public String getAttr() {
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void addCondition(String attr,String operator,String value,Boolean isNew){
		if(isNew || null==condition){//如果重新开启个查询条件  则需要置空条件和分页
			condition=new JSONArray();
			pagenation=null;
			orderBy=null;
		}
		this.attr=attr;
		this.operator=operator;
		this.value=value;
		condition.add(getCondition());
	}
	
	private JSONObject getCondition(){
		JSONObject jsonCondition=new JSONObject();
		jsonCondition.put("attr", this.attr);
		jsonCondition.put("operator", this.operator);
		jsonCondition.put("value", this.value);
		return jsonCondition;
	}
	public JSONArray getConditions(){
		return condition;
	}
}
