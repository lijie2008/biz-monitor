/**
 * Project Name:biz-monitor-provider
 * File Name:StatisticsService.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.service
 * Date:2017年8月7日上午10:17:43
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.service;

import com.alibaba.fastjson.JSONObject;

/**
 * ClassName:StatisticsService
 * Function: 统计报表数据查询 (hbase)
 * Date:     2017年8月7日 上午10:17:43
 * @author   caozhenx
 */
public interface StatisticsService {

    /**
     * queryStatistics:查询统计报表指定卷积属性数据
     * @param monitorClass  所属监管类  例：部门树类、岗位树类
     * @param monitorId     节点id
     * @param periodId      周期类id
     * @param attributeId   卷积属性id
     * @author caozhenx
     * @return Result 查询结果
     */
    public JSONObject queryStatistics(String monitorClass,String monitorId,String periodId,String attributeId);
}

