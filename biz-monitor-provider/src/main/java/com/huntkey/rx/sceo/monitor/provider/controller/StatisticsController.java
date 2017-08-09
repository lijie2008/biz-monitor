/**
 * Project Name:biz-monitor-provider
 * File Name:StatisticsReportController.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.controller
 * Date:2017年8月7日上午10:07:18
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.provider.biz.StatisticsBiz;

/**
 * ClassName:StatisticsReportController
 * Function: 统计报表信息查询类
 * Date:     2017年8月7日 上午10:07:18
 * @author   caozhenx
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    
    
    @Autowired
    StatisticsBiz statisticsBiz;
    
    @RequestMapping("/query/period")
    public Result getPeriod(@RequestBody(required = false) JSONObject data) {
        return statisticsBiz.queryPeriod(data);
    }
    
    @RequestMapping("/query/statistics")
    public Result getStatistics(@RequestBody JSONObject data) {
        System.out.println(data.toJSONString());
        return statisticsBiz.queryStatistics(data);
    }

}

