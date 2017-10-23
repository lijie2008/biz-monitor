package com.huntkey.rx.sceo.monitor.commom.model;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.sceo.monitor.commom.constant.Constant;
import com.huntkey.rx.sceo.monitor.commom.utils.ToolUtil;

public class AddMonitorTreeTo {
    
    // 1 - 新增  ， 2 - 复制 
	@Range(min=1,max=2)
	private int type; 
	
	private String beginDate; 
	
	private String endDate;
	
	@NotBlank(message="监管类ID不能为空")
	private String classId; 
	
	private String rootId;
	
	@NotBlank(message="EDM类英文名不能为空")
	private String edmcNameEn;
	
	// 复制时 树所在的表名称
	private String rootEdmcNameEn;
	
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
		beginDate=ToolUtil.formatDateStr(beginDate, Constant.YYYY_MM_DD);
		this.beginDate = (StringUtil.isNullOrEmpty(beginDate)?ToolUtil.getNowDateStr(Constant.YYYY_MM_DD):beginDate)+Constant.STARTTIME;
	}
	
	public String getEndDate() {
		return endDate;
	}
	
	public void setEndDate(String endDate) {
		if(StringUtil.isNullOrEmpty(endDate)){
			endDate=Constant.MAXINVALIDDATE;
    	}else{
    		endDate=ToolUtil.formatDateStr(endDate, Constant.YYYY_MM_DD);
    	}
		this.endDate = endDate+Constant.ENDTIME;
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

    public String getRootEdmcNameEn() {
        return rootEdmcNameEn;
    }

    public void setRootEdmcNameEn(String rootEdmcNameEn) {
        this.rootEdmcNameEn = rootEdmcNameEn;
    }
	
}
