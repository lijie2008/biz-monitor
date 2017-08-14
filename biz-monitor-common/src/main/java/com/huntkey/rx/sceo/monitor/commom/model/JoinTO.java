package com.huntkey.rx.sceo.monitor.commom.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

public class JoinTO extends JSONObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private String sourceKey;//主表连接字段
    private String mergeKey;//从表接连字段
	private String[] joinColumns;//连接字段名称
	public String getSourceKey() {
		return sourceKey;
	}
	public String getMergeKey() {
		return mergeKey;
	}
	public String[] getJoinColumns() {
		return joinColumns;
	}
	public JoinTO(String sourceKey
			,String mergeKey,String[] joinColumns) {
		// TODO Auto-generated constructor stub
		this.sourceKey = sourceKey;
		this.mergeKey = mergeKey;
		this.joinColumns = joinColumns;
	}

}
