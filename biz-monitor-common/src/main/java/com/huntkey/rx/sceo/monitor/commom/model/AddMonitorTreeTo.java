package com.huntkey.rx.sceo.monitor.commom.model;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

public class AddMonitorTreeTo {
	@Range(min=1,max=2)
	int type; 
	String beginDate; 
	String endDate;
	@NotBlank(message="监管类ID不能为空")
	String classId; 
	String rootId;
	@NotBlank(message="EDM类英文名不能为空")
	String edmcNameEn;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getClassId() {
		return classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
	}
	public String getRootId() {
		return rootId;
	}
	public void setRootId(String rootId) {
		this.rootId = rootId;
	}
	public String getEdmcNameEn() {
		return edmcNameEn;
	}
	public void setEdmcNameEn(String edmcNameEn) {
		this.edmcNameEn = edmcNameEn;
	}
}