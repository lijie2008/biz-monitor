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
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.provider.biz.StatisticsDBiz;

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
    StatisticsDBiz statisticsBiz;

    @RequestMapping("/query/period")
    public Result getPeriod(@RequestBody(required = false) JSONObject data) {
        return statisticsBiz.queryPeriod(data);
    }

    @RequestMapping("/query/statistics")
    public Result getStatistics(@RequestBody JSONObject data) {
        return statisticsBiz.queryStatistics(data);
    }

    @GetMapping("/curamt")
    public Result getStatistics(@RequestParam String edmId, @RequestParam String objId, @RequestParam String periodId,
                                @RequestParam String attributeId) throws Exception{
        return statisticsBiz.queryStatistics(edmId, objId, periodId, attributeId);
    }

    @RequestMapping("/statistics/curamts")
    public Result getStatistic(@RequestParam(value = "moniIds") String moniIds,
                               @RequestParam(value = "periodId") String periodId,
                               @RequestParam(value = "attributeIds") String attributeIds) throws Exception{
        return statisticsBiz.queryStatistics(moniIds,periodId,attributeIds);
        
    };

}
