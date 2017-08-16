/**
 * Project Name:biz-monitor-provider
 * File Name:PeriodServiceImpl.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.service.impl
 * Date:2017年8月7日上午10:41:05
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
import com.huntkey.rx.sceo.monitor.commom.constant.ServiceCenterConstant;
import com.huntkey.rx.sceo.monitor.commom.constant.StatisticsConstant;
import com.huntkey.rx.sceo.monitor.commom.exception.ServiceException;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ServiceCenterClient;
import com.huntkey.rx.sceo.monitor.provider.service.PeriodService;

/**
 * ClassName:PeriodServiceImpl
 * Function: 周期类方法实现
 * Date:     2017年8月7日 上午10:41:05
 * @author   caozhenx
 * @version  
 * @see 	 
 */
@Service("periodService")
public class PeriodServiceImpl implements PeriodService {
    
    private static final Logger LOG = LoggerFactory.getLogger(PeriodServiceImpl.class);

    @Autowired
    ServiceCenterClient serviceCenterClient;

    /**
     * queryPeriod:查询统计周期信息
     * 
     * @param year 财年
     * @param type 周期分类 天:D  周：W   月:M   季:Q  年:Y
     * 
     * @author caozhenx
     * @return Result查询结果
     */
    @Override
    public JSONObject queryPeriod(String id, String year, String type, String beginTime,
                                  String endTime) {
        LOG.info("查询周期类开始,id:{},year:{},type:{},beginTime:{},endTime:{}",new Object [] {id,year,type,beginTime,endTime});
        long time = System.currentTimeMillis();

        Result result = new Result();

        String queryString = getQueryString(id, year, type, beginTime, endTime);

        result = serviceCenterClient.queryServiceCenter(queryString);

        //处理查询结果 拼接财年显示内容    peid001+"F"+peid005 的格式拼接
        if (result.getData() != null && result.getRetCode() == Result.RECODE_SUCCESS) {
            JSONObject jsonObj =  JsonUtil.getJson(result.getData());
            JSONArray jsonArray = jsonObj.getJSONArray(ServiceCenterConstant.DATA_SET);
            if (jsonArray != null && !jsonArray.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Object obj : jsonArray) {
                    JSONObject jo = JsonUtil.getJson(obj);
                    sb.append(jo.getString(StatisticsConstant.PEID001)).append(StatisticsConstant.SYMBOL_F);
                    String peid005 = jo.getString(StatisticsConstant.PEID005);
                    if (StringUtils.isNotBlank(peid005)) {
                        if (peid005.length() == 1) {
                            sb.append(StatisticsConstant.SYMBOL_0).append(peid005);
                        } else {
                            sb.append(peid005);
                        }
                    }
                    jo.put(StatisticsConstant.PEID006, sb.toString());
                    sb.delete(0, sb.length());
                }
            }
            LOG.info("查询周期类信息结束,结果:{},用时:{}",JsonUtil.getJsonString(jsonObj),System.currentTimeMillis()-time);

            return jsonObj;
        }else{
            throw new ServiceException(result.getErrMsg());
        }
    }

    private String getQueryString(String id, String year, String type, String beginTime,
                                  String endTime) {

        //查询条件
        JSONObject json = new JSONObject();
        JSONObject search = new JSONObject();
        JSONArray conditions = new JSONArray();
        JSONArray orderBy = new JSONArray();

        //查询条件1
        if (StringUtils.isNotBlank(id)) {
            JSONObject condition0 = new JSONObject();
            condition0.put(ServiceCenterConstant.ATTR, StatisticsConstant.ID);
            condition0.put(ServiceCenterConstant.OPERATOR, ServiceCenterConstant.SYMBOL_EQUAL);
            condition0.put(ServiceCenterConstant.VALUE, id);
        }

        //查询条件2
        if (StringUtils.isNotBlank(year)) {
            JSONObject condition1 = new JSONObject();
            condition1.put(ServiceCenterConstant.ATTR, StatisticsConstant.PEID001);
            condition1.put(ServiceCenterConstant.OPERATOR, ServiceCenterConstant.SYMBOL_EQUAL);
            condition1.put(ServiceCenterConstant.VALUE, year);
            conditions.add(condition1);
        }
        //查询条件3
        if (StringUtils.isNotBlank(type)) {
            JSONObject condition2 = new JSONObject();
            condition2.put(ServiceCenterConstant.ATTR, StatisticsConstant.PEID002);
            condition2.put(ServiceCenterConstant.OPERATOR, ServiceCenterConstant.SYMBOL_EQUAL);
            condition2.put(ServiceCenterConstant.VALUE, type);
            conditions.add(condition2);
        }

        //查询条件4
        if (StringUtils.isNotBlank(beginTime)) {
            JSONObject condition3 = new JSONObject();
            condition3.put(ServiceCenterConstant.ATTR, StatisticsConstant.PEID003);
            condition3.put(ServiceCenterConstant.OPERATOR, ServiceCenterConstant.SYMBOL_EQUAL);
            condition3.put(ServiceCenterConstant.VALUE, beginTime);
            conditions.add(condition3);
        }

        //查询条件5
        if (StringUtils.isNotBlank(endTime)) {
            JSONObject condition4 = new JSONObject();
            condition4.put(ServiceCenterConstant.ATTR, StatisticsConstant.PEID004);
            condition4.put(ServiceCenterConstant.OPERATOR, ServiceCenterConstant.SYMBOL_EQUAL);
            condition4.put(ServiceCenterConstant.VALUE, endTime);
            conditions.add(condition4);
        }

        JSONObject order = new JSONObject();
        order.put(ServiceCenterConstant.ATTR, StatisticsConstant.PEID005);
        order.put(ServiceCenterConstant.SORT, ServiceCenterConstant.SORT_ASC);
        orderBy.add(order);

        search.put(ServiceCenterConstant.CONDITIONS, conditions);
        search.put(ServiceCenterConstant.ORDER_BY, orderBy);

        //edm类名称
        json.put(ServiceCenterConstant.EDM_NAME, StatisticsConstant.EDM_NAME);
        json.put(ServiceCenterConstant.SEARCH, search);

        return json.toJSONString();

    }

}
