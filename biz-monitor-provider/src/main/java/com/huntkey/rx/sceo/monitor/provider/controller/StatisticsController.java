/**
 * Project Name:biz-monitor-provider
 * File Name:StatisticsReportController.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.controller
 * Date:2017年8月7日上午10:07:18
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.edm.entity.StatisticsEntity;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.provider.service.StatisticsDService;

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
    StatisticsDService statisticsService;

    @GetMapping("/curamt")
    public Result getStatistics(@RequestParam String edmId, @RequestParam String objId, @RequestParam String periodId,
                                @RequestParam String attributeId) throws Exception{
        
        Result result = new Result();
        
        result.setRetCode(Result.RECODE_SUCCESS);
        
        List<StatisticsEntity> queryJson = statisticsService.queryStatistics(edmId, objId, periodId, attributeId);
        
        if (queryJson == null || queryJson.size() <= 0) 
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),ErrorMessage._60005.getMsg());
        
        StatisticsEntity statisticObj = queryJson.get(0);

        BigDecimal curamt = statisticObj.getStat_curamt();
        
        result.setData(curamt);
        
        return result;
    }

    @RequestMapping("/statistics/curamts")
    public Result getStatistic(@RequestParam(value = "moniIds") String moniIds,
                               @RequestParam(value = "periodId") String periodId,
                               @RequestParam(value = "attributeIds") String attributeIds) throws Exception{
        
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        
        List<StatisticsEntity> datas = statisticsService.queryStatistics(moniIds,periodId, attributeIds);
        
        if (datas == null || datas.size() <= 0) 
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),ErrorMessage._60005.getMsg());
        
        result.setData(datas);
        
        return result;
        
    };

}
