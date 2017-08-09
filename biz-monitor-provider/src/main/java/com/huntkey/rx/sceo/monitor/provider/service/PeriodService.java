/**
 * Project Name:biz-monitor-provider
 * File Name:PeriodService.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.service
 * Date:2017年8月7日上午10:18:12
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.service;

import com.alibaba.fastjson.JSONObject;

/**
 * ClassName:PeriodService
 * Function: 统计周期信息查询(hbase)
 * Date:     2017年8月7日 上午10:18:12
 * @author   caozhenx
 */
public interface PeriodService {

    /**
     * queryPeriod:查询统计周期信息
     * 
     * @param year 财年
     * @param type 周期分类 天:D  周：W   月:M   季:Q  年:Y
     * 
     * @author caozhenx
     * @return Result查询结果
     */
    public JSONObject queryPeriod(String id, String year, String type, String beginTime, String endTime);
}
