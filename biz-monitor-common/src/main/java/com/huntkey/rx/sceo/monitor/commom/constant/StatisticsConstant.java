/**
 * Project Name:biz-monitor-common
 * File Name:StatisticsConstant.java
 * Package Name:com.huntkey.rx.sceo.monitor.commom
 * Date:2017年8月9日下午2:10:56
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.constant;
/**
 * ClassName:StatisticsConstant
 * Function: 统计数据查询相关常
 * Date:     2017年8月9日 下午2:10:56
 * @author   caozhenx
 * @version  
 * @see 	 
 */
public interface StatisticsConstant {
    /**字段id*/
    public String ID = "id";
    /**字段 year 财年*/
    public String YEAR = "year";
    /**字段type  类型*/
    public String TYPE = "type";
    /**字段beginTime  开始时间*/
    public String BEGIN_TIME = "beginTime";
    
    /**
     * END_TIME:结束时间
     */
    public String END_TIME = "endTime";
    
    /**
     * MONITOR_ID:所属监管类id
     */
    public String MONITOR_ID = "monitorId";
    
    /**
     * PERIOD_ID:周期类id
     */
    public String PERIOD_ID = "periodId";
    
    /**
     * ATTRIBUTE_IDS:查询卷积属性集
     */
    public String ATTRIBUTE_IDS = "attributeIds";
    
    /**
     * TREE_NODE:查询节点信息
     */
    public String TREE_NODE = "treeNode";
    
    /**
     * STATISTICS:统计结果
     */
    public String STATISTICS = "statistics";
    
    /**
     * CHILD_NODES:子节点信息
     */
    public String CHILD_NODES = "childNodes";
    
    /**
     * EDM_NAME:周期类 edm 名称
     */
    public String EDM_NAME = "period";
    
    /**
     * PEID_id:周期类id
     */
    public String PEID_id = "id";
    
    /**
     * PEID001:周期类字段 财年
     */
    public String PEID001 = "peid001";
    
    /**
     * PEID002:周期类字段  周期类型  天:D  周：W   月:M   季:Q  年:Y
     */
    public String PEID002 = "peid002";
    
    /**
     * PEID003:周期类字段 开始时间
     */
    public String PEID003 = "peid003";
    
    /**
     * PEID004:周期类字段 结束时间
     */
    public String PEID004 = "peid004";
    
    /**
     * PEID005:周期类字段 期次
     */
    public String PEID005 = "peid005";
    
    /**
     * PEID006:查询返回添加字段    展示名称   PEID001 + SYMBOL_F + PEID005
     */
    public String PEID006 = "peid006";
    
    /**
     * SYMBOL_F:符号F
     */
    public String SYMBOL_F = "F";
    
    /**
     * SYMBOL_0:符号 0
     */
    public String SYMBOL_0 = "0";
    

}

