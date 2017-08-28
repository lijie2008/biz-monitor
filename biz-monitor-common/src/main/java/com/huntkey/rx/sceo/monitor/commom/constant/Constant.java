/**
 * Project Name:common-service-common
 * File Name:Constant.java
 * Package Name:com.huntkey.rx.sceo.commonService.common.util
 * Date:2017年7月3日上午9:51:29
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.constant;
/**
 * ClassName:Constant
 * Function: 常量
 * Date:     2017年7月3日 上午9:51:29
 * @author   lijie
 * @version  
 * @see 	 
 */
public interface Constant {
   
    // 总账树类
    String BOOKING = "booking";
    
    // 部门树类
    String DEPTTREE = "depttree";
    
    // 部门树类
    String REPORTINGLINE = "reportingline";
    
    // 设施系统类
    String FACILITY = "facility";
    
    // 授权ID
    String EMPO_001 = "empo_001";
    
    // equal operate
    String EQUAL = "=";
    
    // like operate
    String LIKE = "like";
    
    // > GT
    String GT = ">";
    
    // < LT
    String LT = "<";
    
    // >=
    String GTE = ">=";
    
    // >=
    String LTE = "<=";
    
    // 查询结果集名称
    String DATASET = "dataset";
    
    // id EDM类的ID
    String ID = "id";
    
    // 变更对象
    String OID = "oid";
    
    // pid 属性集的外键
    String PID = "pid";
    
    // 日期类型的特殊转换
    String YYYYMMDD = "yyyyMMdd";
    
    String YYYY_MM_DD = "yyyy-MM-dd";
    

    /**
     * 有权限/可用/可见 - 1
     */
    int AUTHORIZATION_YES = 1;

    /**
     * 无权限/不可用/不可见 - 0
     */
    int AUTHORIZATION_NO = 0;

    /**
     * 模糊查询 0
     */
    int FUZZY_QUERY_YES = 1;

    /**
     *  非模糊查询（即通过ID精确查询） 1
     */
    int FUZZY_QUERY_NO = 0;

    /**
     * 资源不存在 0
     */
    int RESOURCE_EXIST_NO = 0;

    /**
     * 资源存在 1
     */
    int RESOURCE_EXIST_YES = 1;


    /**
     *EDM模板id
     */
    String MODELER_ID = "9314121da04b4642a02e2c2f0e3920bd";
    
    /**
     *分页的总页数
     */
    String TOTALSIZE = "totalSize";
    
    /**
     *标记位
     */
    String FLAG = "flag";
    
    String MONITORTREEORDER="monitortreeorder";//监管树临时单表
    
    String MTOR001="mtor001";//单据编号
    
    String MTOR002="mtor002";//变更类型
    
    String MTOR003="mtor003";//变更监管树类
    
    String MTOR004="mtor004";//变更树对象根节点
    
    String MTOR005="monitortreeorder.mtor005";//监管树临时单_节点集合表
    
    String MTOR019="monitortreeorder.mtor005.mtor019";//监管树临时单_资源集合表
    
    String MTOR009="mtor009";//主管人
    
    String MTOR010="mtor010";//协管人
    
    String MTOR011="mtor011";//监管树临时单_资源集合表_生效时间字段
    
    String MTOR012="mtor012";//监管树临时单_资源集合表_失效时间字段
    
    String MTOR020="mtor020";//资源对象ID
    
    String MTOR013="mtor013";//父节点
    
    String MTOR014="mtor014";//子节点
    
    String MTOR015="mtor015";//左节点
    
    String MTOR016="mtor016";//右节点
    
    String MTOR021="mtor021";//监管更新标记
    
    String NULL="";//空标记
    
    String STAFF="staff";//员工表
    
    String STAF002="staf002";//员工姓名字段
    
    String INITNODENAME="未命名节点";//初始化节点名称
    
    String MAXINVALIDDATE="9999-12-31";//最大失效日期
    
    String STAFFCLASSID="6f913ed266e211e7b2e4005056bc4879";
}

