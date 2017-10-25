package com.huntkey.rx.sceo.monitor.commom.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.sceo.monitor.commom.constant.Constant;
import com.huntkey.rx.sceo.monitor.commom.exception.ServiceException;

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
		beginDate=formatDateStr(beginDate, Constant.YYYY_MM_DD);
		this.beginDate = (StringUtil.isNullOrEmpty(beginDate)?getNowDateStr(Constant.YYYY_MM_DD):beginDate)+Constant.STARTTIME;
	}
	
	public String getEndDate() {
		return endDate;
	}
	
	public void setEndDate(String endDate) {
		if(StringUtil.isNullOrEmpty(endDate)){
			endDate=Constant.MAXINVALIDDATE;
    	}else{
    		endDate=formatDateStr(endDate, Constant.YYYY_MM_DD);
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
    
    /**
     * 日期格式转化
     * @param dateStr 日期字符串 
     * @param formatStr 日期格式
     * @return
     */
    public static  String formatDateStr(String dateStr,String formatStr) {
        String formatDateStr=null;
        if(!StringUtil.isNullOrEmpty(dateStr)){
            SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
            Date datetime=null;
            try {
                datetime=(Date) sdf.parse(dateStr);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                throw new ServiceException("传入日期格式错误！");
            }
            formatDateStr= sdf.format(datetime);
        }
        return formatDateStr;
    }
    
    /**
     * 日期格式转化
     * @param dateStr 日期字符串 
     * @param formatStr 日期格式
     * @return
     */
    public static  String getNowDateStr(String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        String formatDateStr= sdf.format(new Date());
        return formatDateStr;
    } 
	
}
