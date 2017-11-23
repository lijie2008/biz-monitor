/**
 * Project Name:biz-monitor-provider
 * File Name:StatisticsServiceImpl.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.service.impl
 * Date:2017年8月7日上午10:41:19
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.exception.ServiceException;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ServiceCenterClient;
import com.huntkey.rx.sceo.monitor.provider.service.StatisticsService;
import com.huntkey.rx.sceo.serviceCenter.common.emun.OperatorType;
import com.huntkey.rx.sceo.serviceCenter.common.model.ConditionNode;
import com.huntkey.rx.sceo.serviceCenter.common.model.SearchParam;

/**
 * ClassName:StatisticsServiceImpl
 * Function: 查询统计类数据
 * Date:     2017年8月7日 上午10:41:19
 * @author   caozhenx
 * @version  
 * @see 	 
 */
@Service("statisticsService")
public class StatisticsServiceImpl implements StatisticsService {
    private static final Logger LOG = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    /**edm类名*/
    private String EDM_NAME = "statistics";
    /**所属监管类*/
    private String STAT001 = "stat_moniclass";
    /**所属监管类对象*/
    private String STAT002 = "stat_moniobj";
    /**属性id*/
    private String STAT003 = "stat_moniattr";
    /**周期类id*/
    private String STAT013 = "stat_pridobj";

    @Autowired
    ServiceCenterClient serviceCenterClient;

    /**
     * queryStatistics:查询统计报表指定卷积属性数据
     * @param monitorClass  所属监管类  例：部门树类、岗位树类
     * @param monitorId     节点id
     * @param periodId      周期类id
     * @param attributeId   卷积属性id
     * @author caozhenx
     * @return Result 查询结果
     */
    @Override
    public JSONObject queryStatistics(String monitorClass, String monitorId, String periodId,
                                      String attributeId) {
        LOG.info("查询统计类信息开始,monitorClass:{},monitorId:{},periodId:{},attributeId:{}",new Object [] {monitorClass,monitorId,periodId,attributeId});
        long time = System.currentTimeMillis();
        String queryString = getQueryString(monitorClass, monitorId, periodId, attributeId);
        Result result = serviceCenterClient.queryServiceCenter(queryString);
        if (result.getRetCode() == Result.RECODE_SUCCESS) {
            JSONObject obj = JsonUtil.getJson(result.getData());
            LOG.info("查询统计类信息结束,结果:{},用时:{}",JsonUtil.getJsonString(obj),System.currentTimeMillis()-time);
            return obj;
        } else {
            LOG.error("查询统计类信息错误,errMsg:{}",result.getErrMsg());
            throw new ServiceException(result.getErrMsg());
        }
    }

    private String getQueryString(String monitorClass, String monitorId, String periodId,
                                  String attributeId) {
        //查询条件
        SearchParam requestParams = new SearchParam(EDM_NAME);
        //查询条件1 所性监管类
        if (StringUtils.isNotBlank(monitorClass)) {
            requestParams.addCondition(new ConditionNode(STAT001, OperatorType.Equals, monitorClass));
        }
        //查询条件2 节点id
        if (StringUtils.isNotBlank(monitorId)) {
            requestParams.addCondition(new ConditionNode(STAT002, OperatorType.Equals, monitorId));
        }
        //查询条件3 周期点id
        if (StringUtils.isNotBlank(periodId)) {
            requestParams.addCondition(new ConditionNode(STAT013, OperatorType.Equals, periodId));
        }
        //查询条件4 属性id
        if (StringUtils.isNotBlank(attributeId)) {
            requestParams.addCondition(new ConditionNode(STAT003, OperatorType.Equals, attributeId));
        }
        return requestParams.toJSONString();

    }

    @Override
    public JSONObject queryStatistics(String moniIds, String periodId, String attributeIds) {
        long time = System.currentTimeMillis();
        String queryString = getQueryString(moniIds, periodId, attributeIds);
        Result result = serviceCenterClient.queryServiceCenter(queryString);
        if (result.getRetCode() == Result.RECODE_SUCCESS) {
            JSONObject obj = JsonUtil.getJson(result.getData());
            LOG.info("查询统计类信息结束,结果:{},用时:{}",JsonUtil.getJsonString(obj),System.currentTimeMillis()-time);
            return obj;
        } else {
            LOG.error("查询统计类信息错误,errMsg:{}",result.getErrMsg());
            throw new ServiceException(result.getErrMsg());
        }
    }

    private String getQueryString(String moniIds, String periodId, String attributeIds) {
        //查询条件
        SearchParam requestParams = new SearchParam(EDM_NAME);
        //查询条件2 监管
        if (StringUtils.isNotBlank(moniIds)) {
            requestParams.addCondition(new ConditionNode(STAT002, OperatorType.In, moniIds));
        }
        //查询条件3 周期点id
        if (StringUtils.isNotBlank(periodId)) {
            requestParams.addCondition(new ConditionNode(STAT013, OperatorType.Equals, periodId));
        }
        //查询条件4 属性id
        if (StringUtils.isNotBlank(attributeIds)) {
            requestParams.addCondition(new ConditionNode(STAT003, OperatorType.In, attributeIds));
        }
        return requestParams.toJSONString();

    }
    
    
    

}
