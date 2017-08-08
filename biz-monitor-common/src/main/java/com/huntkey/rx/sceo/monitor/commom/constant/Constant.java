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
   
    // 指标可见类
    String VISIBLEINDEX = "visibleindex";
    
    // 指标可见集类
    String VIIN_VIIN_001A = "viin_viin_001a";
    
    // 可见PPI集
    String VIIN_VIIN_002B = "viin_viin_002b";
    
    // 可见KPI集
    String VIIN_VIIN_012B = "viin_viin_012b";
    
    //可见任务目标集
    String VIIN_VIIN_022B = "viin_viin_022b";
    
    //可见靶向集
    String VIIN_VIIN_032B = "viin_viin_032b";
    
    // 绩效指标类
    String PROCESSPERFORMANCEINDEX = "processperformanceindex";
    
    // 岗位授权单
    String POSITIONEMPOWERORDER = "positionempowerorder";
    
    // 岗位授权单中的授权岗位集 peor_peor_006a
    String PEOR_PEOR_006A = "peor_peor_006a";
    
    // 岗位授权单中的可见表单集 peor_peor_008a
    String PEOR_PEOR_008A = "peor_peor_008a";
    
    // 岗位授权单中的 可见属性集合 peor_peor_013B
    String PEOR_PEOR_013B = "peor_peor_013b";
    
    // 岗位授权单中的 可见数据性集合 peor_peor_017a
    String PEOR_PEOR_017A = "peor_peor_017a";
    
    // 岗位授权单中的 可阅性控制集合 peor_peor_023B
    String PEOR_PEOR_023B = "peor_peor_023b";
    
    // 岗位授权单中的可见指标属性集 peor_peor_027a
    String PEOR_PEOR_027A = "peor_peor_027a";
    
    // 可用表单类
    String ASSIGNEDORDER = "assignedorder";
    
    // 可见数据类
    String ACCESSIBLEINFORMATION = "accessibleinformation";
    
    // 表单集
    String ASOR_ASOR_001A = "asor_asor_001a";
    
    // 可见数据集
    String ACIN_ACIN_001A = "acin_acin_001a";
    
    // 属性集
    String ACIN_ACIN_010B = "acin_acin_010b";
    

    
    
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
    
    /**
     * 资源配置类
     */
    String OWNER_OF_RESOURCE = "ownerofresource";

    /**
     * 资源配置类
     */
    String OWRE_OWRE_001A =  "owre_owre_001a";

    /**
     * 资源配置单
     */
    String RESOURCES_DEPLOY_ORDER =  "resourcesdeployorder";

    /**
     * 授权资源配置集
     */
    String RDOR_RDOR_006A =  "rdor_rdor_006a";
    
    /**
     * 授权资源配置集
     */
    String LETFTJOIN =  "left";
    
    /**
     * 授权资源配置集
     */
    String RIGHTJOIN =  "right";
    
    // 权限申请单
    String EMPOWERAPPLYORDER = "empowerapplyorder";
    
    // 可用表单集
    String EAOR_EAOR_007A = "eaor_eaor_007a";
    
    // 可用表单集 下的属性集
    String EAOR_EAOR_012B = "eaor_eaor_012b";
    
    // 可见数据类
    String EAOR_EAOR_016A = "eaor_eaor_016a";
    
    // 可见数据类下的属性集
    String EAOR_EAOR_022B = "eaor_eaor_022b";
    
    // 可见指标集
    String EAOR_EAOR_026A = "eaor_eaor_026a";
    
    // 日期类型的特殊转换
    String YYYYMMDD = "yyyyMMdd";
    
    String YYYY_MM_DD = "yyyy-MM-dd";
    
    /**
     * 状态位 是
     */
    public static final String STATUSTRUE =  "1";
    
    /**
     * 状态位 否
     */
    public static final String STATUSFALSE =  "0";


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
     * 数据源的字段
     */
    public static final String SOURCEKEY =  "sourceKey";
    /**
     * 被合并数据源的字段
     */
    public static final String MERGEKEY =  "mergeKey";
    /**
     * 添加到数据源的字段
     */
    public static final String COLUMN =  "column";
    /**
     * 表名
     */
    public static final String TABLENAME =  "tableName";
    /**
     * 条件
     */
    public static final String CONDITION =  "condition";
    /**
     * 数据连接方式
     */
    public static final String JOINMETHOD =  "joinMethod";


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
    
    String MODE003="monitortreeorder.mode003";//监管树临时单_节点集合表
    String MODE003_="monitortreeorder.mode003_";//监管树临时单_节点集合表
    
    String MODE018="monitortreeorder.mode018";//监管树临时单_资源集合表
    String MODE018_="monitortreeorder.mode018_";//监管树临时单_资源集合表
    
    String MODE010="mode010";//监管树临时单_资源集合表_失效时间字段
    
    
    
    
    
}

