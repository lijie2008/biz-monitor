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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.ServiceCenterConstant;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ServiceCenterClient;
import com.huntkey.rx.sceo.monitor.provider.service.StatisticsService;

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
    private String STAT001 = "stat001";
    /**所属监管类对象*/
    private String STAT002 = "stat002";
    /**属性id*/
    private String STAT003 = "stat003";
    /**周期类id*/
    private String STAT006 = "stat006";

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
            LOG.error("查询统计类信息错误.errMsg:{}", result.getErrMsg());
        }
        
        return null;
    }

    private String getQueryString(String monitorClass, String monitorId, String periodId,
                                  String attributeId) {

        //查询条件
        JSONObject json = new JSONObject();
        JSONObject search = new JSONObject();
        JSONArray conditions = new JSONArray();

        //查询条件1 所性监管类
        if (StringUtils.isNotBlank(monitorClass)) {
            JSONObject condition1 = new JSONObject();
            condition1.put(ServiceCenterConstant.ATTR, STAT001);
            condition1.put(ServiceCenterConstant.OPERATOR, ServiceCenterConstant.SYMBOL_EQUAL);
            condition1.put(ServiceCenterConstant.VALUE, monitorClass);
            conditions.add(condition1);
        }
        //查询条件2 节点id
        if (StringUtils.isNotBlank(monitorId)) {
            JSONObject condition2 = new JSONObject();
            condition2.put(ServiceCenterConstant.ATTR, STAT002);
            condition2.put(ServiceCenterConstant.OPERATOR, ServiceCenterConstant.SYMBOL_EQUAL);
            condition2.put(ServiceCenterConstant.VALUE, monitorId);
            conditions.add(condition2);
        }
        //查询条件3 周期点id
        if (StringUtils.isNotBlank(periodId)) {
            JSONObject condition3 = new JSONObject();
            condition3.put(ServiceCenterConstant.ATTR, STAT006);
            condition3.put(ServiceCenterConstant.OPERATOR, ServiceCenterConstant.SYMBOL_EQUAL);
            condition3.put(ServiceCenterConstant.VALUE, periodId);
            conditions.add(condition3);
        }
        //查询条件4 属性id
        if (StringUtils.isNotBlank(attributeId)) {
            JSONObject condition4 = new JSONObject();
            condition4.put(ServiceCenterConstant.ATTR, STAT003);
            condition4.put(ServiceCenterConstant.OPERATOR, ServiceCenterConstant.SYMBOL_EQUAL);
            condition4.put(ServiceCenterConstant.VALUE, attributeId);
            conditions.add(condition4);
        }

        search.put(ServiceCenterConstant.CONDITIONS, conditions);

        //edm类名称
        json.put(ServiceCenterConstant.EDM_NAME, EDM_NAME);
        json.put(ServiceCenterConstant.SEARCH, search);

        return json.toJSONString();

    }

}
