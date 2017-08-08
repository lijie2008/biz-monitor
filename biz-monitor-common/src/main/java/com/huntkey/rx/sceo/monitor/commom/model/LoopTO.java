package com.huntkey.rx.sceo.monitor.commom.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class LoopTO extends JSONObject implements Serializable {
    private String tableName;
    private String sourceKey;
    private String mergeKey;
    public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getSourceKey() {
		return sourceKey;
	}
	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}
	public String getMergeKey() {
		return mergeKey;
	}
	public void setMergeKey(String mergeKey) {
		this.mergeKey = mergeKey;
	}
	public String[] getColumns() {
		return columns;
	}
	public void setColumns(String[] columns) {
		this.columns = columns;
	}
	public Condition getCondition() {
		return condition;
	}
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	private String[] columns;
    private Condition condition;
}
