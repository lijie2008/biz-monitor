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
    
    //监管类的从属资源类字段名
    String MONITOR_CLASS_PROP_RESOURCES = "moni_resource_class";
    
    // 根节点层级
    String ROOT_LVL = "1";
    
    // 根节点层级编码
    String ROOT_LVL_CODE = "1,";
    
    // 实体类所在路径
    String ENTITY_PATH = "com.huntkey.rx.edm.entity";
    
    // 年-月-日 
    String YYYY_MM_DD = "yyyy-MM-dd";
    
    // 年-月-日 时:分:秒
    String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    
    // 监管树历史集
    String MONITOR_HISTORY_SET="moni_his_set";
    
    // 主键ID
    String ID = "id";
    
    // pid 属性集的外键
    String PID = "pid";
    
    // 开始时分秒
    String STARTTIME = " 00:00:00";
    
    // 结束时分秒
    String ENDTIME = " 23:59:59";
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // 授权ID
    String EMPO_001 = "empo_001";
    
    // 查询结果集名称
    String DATASET = "dataset";
    
    
    // 变更对象
    String OID = "oid";

    // 日期类型的特殊转换
    String YYYYMMDD = "yyyyMMdd";
    
    

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
   
    String STAFF="employee";//员工表
    
    String STAF002="staf002";//员工姓名字段
    
    String INITNODENAME="未命名节点";//初始化节点名称
    
    String MAXINVALIDDATE="9999-12-31";//最大失效日期
    
    String STAFFCLASSID="2d6b8cd2abcb11e78bba005056bc4879";
    
    String EDMPCODE = "moni_resource_class";
    
    String VALUE = "value";

    String JOBPOSITIONCLASSID = "6fa512bf66e211e7b2e4005056bc4879";
    
    String DEPTTREECLASSID = "6fa512bf66e211e7b2e4005056bc4879";
}

