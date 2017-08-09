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
     * queryStatistics:查询传入监管树对够用节点的统计数据
     * {
     *      monitorId:所属监管类id,
     *      periodId:周期类id,
     *      attributeIds:[属性1，属性2]
     *      treeNode:{
     *              id:节点id,
     *              childNodes:[{id:id1},{id:id2}]
     *          }
     *      }
     * @author caozhenx
     * @param data
     * @return
     */
    Result queryStatistics(JSONObject data);

}

