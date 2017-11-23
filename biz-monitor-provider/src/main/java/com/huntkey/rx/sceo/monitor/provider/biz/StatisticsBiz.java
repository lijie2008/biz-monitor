/**
 * Project Name:biz-monitor-provider
 * File Name:StatisticsBiz.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.biz
 * Date:2017年8月8日下午3:07:34
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.biz;

import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;

/**
 * ClassName:StatisticsBiz
 * Function: 统计数据查询业务逻辑
 * Date:     2017年8月8日 下午3:07:34
 * @author   caozhenx
 * @version  
 * @see 	 
 */
public interface StatisticsBiz {

    /**
     * queryPeriod:查询周期类
     * {
     *      id:周期类id,
     *      year:财年,
     *      type:周期类类型,   (天:D  周：W   月:M   季:Q  年:Y)
     *      beginTime:开始时间,
     *      endTime:结束时间
     * }
     * @author caozhenx
     * @param data
     * @return
     */
    Result queryPeriod(JSONObject data);

    /**
     * 查询条件
     *     {
     *      "monitorId":"所属监管类id",
     *      "edmcNameEn":"所属监管类英文名",
     *     "periodId":"周期类id",
     *     "type":"查询类型",
     *     "attributeIds":[{"attrId":"attrid1","attrName":"属性1"},{"attrId":"attrid2","attrName":"属性2"}],
     *     "treeNode":{
     *             "nodeId":"节点id",
     *     "nodeName":"节点名"
     *         }
     *     }
     *     
     * 返回结果
     *       {
     *      "monitorId":"所属监管类id",
     *     "periodId":"周期类id",
     *     attributeIds:[{"attrId":"attrid1","attrName":"属性1"},{"attrId":"attrid2","attrName":"属性2"}],
     *     "treeNode":{
     *             "nodeId":"节点id",
     *     "nodeName":"节点名",
     *     "statistics":[
     *          {"attrId":"attrid1","attrName":"属性1","dayValue":"日值","monthValue":"月值","monthLinkRelativeRatio":"环比值","monthLearOnYear":"同比值","queryMonthValue":"财年累计值","queryMonthLinkRelativeRatio":"财年累计同比值"},
     *          {"attrId":"attrid2","attrName":"属性2","dayValue":"日值","monthValue":"月值","monthLinkRelativeRatio":"环比值","monthLearOnYear":"同比值","queryMonthValue":"财年累计值","queryMonthLinkRelativeRatio":"财年累计同比值"}
     *      ],
     *             "childNodes":[
     *          {
     *          "nodeId":"节点id",
     *              "nodeName":"节点名",
     *          "statistics":[
     *                  {"attrId":"attrid1","attrName":"属性1","dayValue":"日值","monthValue":"月值","monthLinkRelativeRatio":"环比值","monthLearOnYear":"同比值","queryMonthValue":"财年累计值","queryMonthLinkRelativeRatio":"财年累计同比值"},
     *                  {"attrId":"attrid2","attrName":"属性2","dayValue":"日值","monthValue":"月值","monthLinkRelativeRatio":"环比值","monthLearOnYear":"同比值","queryMonthValue":"财年累计值","queryMonthLinkRelativeRatio":"财年累计同比值"}
     *              ]
     *          },
     *          
     *          {
     *          "nodeId":"节点id",
     *          "nodeName":"节点名",
     *          "statistics":[
     *                  {"attrId":"attrid1","attrName":"属性1","dayValue":"日值","monthValue":"月值","monthLinkRelativeRatio":"环比值","monthLearOnYear":"同比值","queryMonthValue":"财年累计值","queryMonthLinkRelativeRatio":"财年累计同比值"},
     *                  {"attrId":"attrid2","attrName":"属性2","dayValue":"日值","monthValue":"月值","monthLinkRelativeRatio":"环比值","monthLearOnYear":"同比值","queryMonthValue":"财年累计值","queryMonthLinkRelativeRatio":"财年累计同比值"}
     *              ]
     *          }
     *      ]
     *         }
     *     }
     */
    Result queryStatistics(JSONObject data);

    /**
     * 查询指定周期的类的对象的属性统计值
     * @param edmId edm类id
     * @param objId 对象id
     * @param periodId 周期对象id
     * @param attributeId 属性ID
     * @return
     */
    Result queryStatistics(String edmId,String objId,String periodId,String attributeId);

    /**
     * queryStatistics:批量查询统计类
     * @author caozhenx
     * @param moniIds
     * @param periodId
     * @param attributeIds
     * @return
     */
    Result queryStatistics(String moniIds, String periodId, String attributeIds);

}

