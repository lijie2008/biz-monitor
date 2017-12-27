/**
 * Project Name:common-service-common
 * File Name:Constant.java
 * Package Name:com.huntkey.rx.sceo.commonService.common.util
 * Date:2017年7月3日上午9:51:29
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.constant;

import java.math.BigDecimal;

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
    Integer ROOT_LVL = 1;
    
    // 根节点层级编码
    String ROOT_LVL_CODE = "1,";
    
    // 根节点序号
    BigDecimal ROOT_SEQ = BigDecimal.valueOf(1L);
    
    // 实体类所在路径
    String ENTITY_PATH = "com.huntkey.rx.edm.entity.";
    
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
    
    // 员工表的EDMID
    String STAFFCLASSID="2d6b8cd2abcb11e78bba005056bc4879";
    
    //初始化节点名称
    String INITNODENAME="未命名节点";
    
    // 回退节点的关键字
    String REVOKE_KEY = "REVOKE";
    
    // 临时单节点集合
    String MTOR_NODES_EDM = "monitortreeorder.mtor_node_set";
    
    // 缓存关键字分隔符
    String KEY_SEP = "-";
    
    // 层级编码分隔符
    String LVSPLIT = ",";
    
    String VALUE = "value";
    
    // 监管类下 从属资源类 常量名称
    String EDMPCODE = "moni_resource_class";
    
    // 特殊类 岗位类EDM ID
    String JOBPOSITIONCLASSID = "6fa512bf66e211e7b2e4005056bc4879";
    
    // 版本的前缀
    String PRE_VERSION = "V";
    
    // 变更对象
    String OID = "oid";
    
    //最大失效日期
    String MAXINVALIDDATE="9999-12-31";
    
    // 关键字key
    String KEY = "key";
    
    // 关键字层级编码的key
    String LVLCODE = "lvlCode";
    
    // EDM 临时单类
    String EDM_MONITORTREEORDER = "monitortreeorder";
    
    // EDM 监管树类
    String EDM_MONITOR = "monitor";
    
    // EDM 员工类
    String EDM_EMPLOYEE = "employee";
    
    // EDM 统计类
    String EDM_STATISTICS = "statistics";
    
    // 单据状态-临时
    String ORDER_STATUS_TEMP = "1";
    
    // 单据状态-完成
    String ORDER_STATUS_COMMIT = "5";
}

