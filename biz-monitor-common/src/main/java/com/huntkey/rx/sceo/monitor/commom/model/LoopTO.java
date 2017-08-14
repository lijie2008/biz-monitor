package com.huntkey.rx.sceo.monitor.commom.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

public class LoopTO extends JSONObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tableName;
    private String sourceKey;
    private String mergeKey;
	private String[] columns;
    private Condition condition;
    public String getTableName() {
		return tableName;
	}
	public String getSourceKey() {
		return sourceKey;
	}
	public String getMergeKey() {
		return mergeKey;
	}
	public String[] getColumns() {
		return columns;
	}
	public Condition getCondition() {
		return condition;
	}
	public LoopTO(String tableName,String sourceKey,String mergeKey,
			String[] columns,Condition condition){
		this.tableName = tableName;
		this.sourceKey = sourceKey;
		this.mergeKey = mergeKey;
		this.columns = columns;
		this.condition = condition;
	}
}
