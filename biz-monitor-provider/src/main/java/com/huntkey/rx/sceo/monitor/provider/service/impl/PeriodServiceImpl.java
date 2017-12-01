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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.constant.ServiceCenterConstant;
import com.huntkey.rx.sceo.monitor.commom.constant.StatisticsConstant;
import com.huntkey.rx.sceo.monitor.commom.exception.ServiceException;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ServiceCenterClient;
import com.huntkey.rx.sceo.monitor.provider.service.PeriodService;
import com.huntkey.rx.sceo.serviceCenter.common.emun.OperatorType;
import com.huntkey.rx.sceo.serviceCenter.common.emun.SortType;
import com.huntkey.rx.sceo.serviceCenter.common.model.ConditionNode;
import com.huntkey.rx.sceo.serviceCenter.common.model.SearchParam;
import com.huntkey.rx.sceo.serviceCenter.common.model.SortNode;

/**
 * ClassName:PeriodServiceImpl
 * Function: 周期类方法实现
 * Date:     2017年8月7日 上午10:41:05
 * @author   caozhenx
 * @version  
 * @see 	 
 */
//@Service("periodService")
public class PeriodServiceImpl implements PeriodService {

    private static final Logger LOG = LoggerFactory.getLogger(PeriodServiceImpl.class);
    
    /**
     * EDM_NAME:周期类 edm 名称
     */
    private String EDM_NAME = "period";

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
        LOG.info("查询周期类开始,id:{},year:{},type:{},beginTime:{},endTime:{}",
                new Object[] { id, year, type, beginTime, endTime });
        long time = System.currentTimeMillis();

        Result result = new Result();

        String queryString = getQueryString(id, year, type, beginTime, endTime);

        result = serviceCenterClient.queryServiceCenter(queryString);

        //处理查询结果 拼接财年显示内容    peid001+"F"+peid005 的格式拼接
        if (result.getData() != null && result.getRetCode() == Result.RECODE_SUCCESS) {
            JSONObject jsonObj = JsonUtil.getJson(result.getData());
            JSONArray jsonArray = jsonObj.getJSONArray(ServiceCenterConstant.DATA_SET);
            if (jsonArray != null && !jsonArray.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Object obj : jsonArray) {
                    JSONObject jo = JsonUtil.getJson(obj);
                    sb.append(jo.getString(StatisticsConstant.PEID001))
                            .append(StatisticsConstant.SYMBOL_F);
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
            LOG.info("查询周期类信息结束,结果:{},用时:{}", JsonUtil.getJsonString(jsonObj),
                    System.currentTimeMillis() - time);

            return jsonObj;
        } else {
            LOG.error("orm查询出错，参数：{},errMsg:{}", queryString,result.getErrMsg());
            throw new ServiceException(result.getErrMsg());
        }
    }

    private String getQueryString(String id, String year, String type, String beginTime,
                                  String endTime) {

        //查询条件
        SearchParam requestParams = new SearchParam(EDM_NAME);

        //查询条件1
        if (StringUtils.isNotBlank(id)) {
            requestParams.addCondition(new ConditionNode(StatisticsConstant.ID, OperatorType.Equals, id));
        }

        //查询条件2
        if (StringUtils.isNotBlank(year)) {
            requestParams.addCondition(new ConditionNode(StatisticsConstant.PEID001, OperatorType.Equals, year));
        }
        //查询条件3
        if (StringUtils.isNotBlank(type)) {
            requestParams.addCondition(new ConditionNode(StatisticsConstant.PEID002, OperatorType.Equals, type));
        }

        //查询条件4
        if (StringUtils.isNotBlank(beginTime)) {
            requestParams.addCondition(new ConditionNode(StatisticsConstant.PEID003, OperatorType.Equals, beginTime));
        }

        //查询条件5
        if (StringUtils.isNotBlank(endTime)) {
            requestParams.addCondition(new ConditionNode(StatisticsConstant.PEID004, OperatorType.Equals, endTime));
        }
        requestParams.addSortParam(new SortNode(StatisticsConstant.PEID005, SortType.ASC));

        return requestParams.toJSONString();

    }

}
