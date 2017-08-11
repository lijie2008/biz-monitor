/**
 * Project Name:biz-monitor-provider
 * File Name:StatisticsBizImpl.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.biz.impl
 * Date:2017年8月8日下午3:08:01
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.biz.impl;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.datetime.DateUtil;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.DateConstant;
import com.huntkey.rx.sceo.monitor.commom.ServiceCenterConstant;
import com.huntkey.rx.sceo.monitor.commom.StatisticsConstant;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
import com.huntkey.rx.sceo.monitor.provider.biz.StatisticsBiz;
import com.huntkey.rx.sceo.monitor.provider.service.PeriodService;
import com.huntkey.rx.sceo.monitor.provider.service.StatisticsService;

/**
 * ClassName:StatisticsBizImpl
 * Function: 统计数据查询业务逻辑
 * Date:     2017年8月8日 下午3:08:01
 * @author   caozhenx
 * @version  
 * @see 	 
 */
@Service("statisticsBiz")
public class StatisticsBizImpl implements StatisticsBiz {
    
    private static final Logger LOG = LoggerFactory.getLogger(StatisticsBizImpl.class);

    @Autowired
    PeriodService periodService;

    @Autowired
    StatisticsService statisticsService;

    /**
     * 
     * {
     *      id:周期类id,
     *      year:财年,
     *      type:周期类类型,   (天:D  周：W   月:M   季:Q  年:Y)
     *      beginTime:开始时间,
     *      endTime:结束时间
     * }
     * 
     */
    @Override
    public Result queryPeriod(JSONObject json) {

        Result result = new Result();
        JSONObject jsonObj = new JSONObject();

        if (json == null || json.isEmpty()) {
            Integer year = Calendar.getInstance().get(Calendar.YEAR);
            jsonObj = periodService.queryPeriod(null, year.toString(), "m", null, null);
        } else {
            String id = json.getString(StatisticsConstant.ID);
            String year = json.getString(StatisticsConstant.YEAR);
            String type = json.getString(StatisticsConstant.TYPE);
            String beginTime = json.getString(StatisticsConstant.BEGIN_TIME);
            String endTime = json.getString(StatisticsConstant.END_TIME);
            jsonObj = periodService.queryPeriod(id, year.toString(), type, beginTime, endTime);
        }

        result.setData(jsonObj);
        result.setRetCode(Result.RECODE_SUCCESS);

        return result;
    }

    /**
     * {
     *      monitorId:所属监管类id,
     *      periodId:周期类id,
     *      attributeIds:[属性1，属性2]
     *      treeNode:{
     *              id:节点id,
     *              childNodes:[{id:id1},{id:id2}]
     *          }
     *      }
     *      
     */
    @Override
    public Result queryStatistics(JSONObject json) {

        Result result = new Result();

        if (json == null || json.isEmpty()) {
            result.setRetCode(Result.RECODE_ERROR);
            result.setErrMsg("查询参数为空");
            return result;
        }

        String monitorId = json.getString(StatisticsConstant.MONITOR_ID);
        if (StringUtils.isBlank(monitorId)) {
            result.setRetCode(Result.RECODE_ERROR);
            result.setErrMsg("所属监管类不可为空..");
            return result;
        }
        String periodId = json.getString(StatisticsConstant.PERIOD_ID);
        if (StringUtils.isBlank(periodId)) {
            result.setRetCode(Result.RECODE_ERROR);
            result.setErrMsg("周期类id不可为空..");
            return result;
        }
        JSONArray attributeIds = json.getJSONArray(StatisticsConstant.ATTRIBUTE_IDS);

        if (attributeIds == null || attributeIds.isEmpty()) {
            result.setRetCode(Result.RECODE_ERROR);
            result.setErrMsg("查询属性集不可为空..");
            return result;
        }
        JSONObject treeNode = json.getJSONObject(StatisticsConstant.TREE_NODE);
        if (treeNode == null) {
            result.setRetCode(Result.RECODE_ERROR);
            result.setErrMsg("查询节点不可为空..");
            return result;
        }
        String treeNodeId = treeNode.getString(StatisticsConstant.ID);
        if (StringUtils.isBlank(treeNodeId)) {
            result.setRetCode(Result.RECODE_ERROR);
            result.setErrMsg("查询节点id不可为空..");
            return result;
        }

        JSONObject js = queryStatistics(monitorId, treeNodeId, periodId, attributeIds);
        treeNode.put(StatisticsConstant.STATISTICS, js);

        //k果有子节点  查询子节点统计数据
        JSONArray chileNodes = treeNode.getJSONArray(StatisticsConstant.CHILD_NODES);
        if (chileNodes != null && !chileNodes.isEmpty()) {
            for (Object o : chileNodes) {
                JSONObject jsonObj = JsonUtil.getJson(o);
                String id = jsonObj.getString(StatisticsConstant.ID);
                jsonObj.put(StatisticsConstant.STATISTICS,
                        queryStatistics(monitorId, id, periodId, attributeIds));
            }
        }

        json.put(StatisticsConstant.TREE_NODE, treeNode);
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(json);

        return result;
    }

    public JSONObject queryStatistics(String monitorId, String nodeId, String periodId,
                                      JSONArray attributeIds) {
        
        LOG.info("查询节点统计数据开始,monitorId:{},nodeId:{},periodId:{},attributeIds:{}",new Object []{monitorId,nodeId,periodId,JsonUtil.getJsonString(attributeIds)});
        long time = System.currentTimeMillis();

        //当天 统计数据
        JSONObject currentDayJson = getDayStatistics(Calendar.getInstance(), monitorId, nodeId);

        //当月统计数据
        JSONObject currentMonthJson = getMonthStatistics(Calendar.getInstance(), monitorId, nodeId);
        //上个月统计数据
        JSONObject lastMonthJson = getMonthStatistics(getLastMonth(), monitorId, nodeId);
        //去年同一时期统计数据（去年的这个月）
        JSONObject lastYearCurrentMonthJson = getMonthStatistics(getLastYearCurrentMonth(),
                monitorId, nodeId);

        //传入的财月 统计数据查询
        JSONObject queryMonthJson = statisticsService.queryStatistics(monitorId, nodeId, periodId,
                null);

        JSONObject lastYearQueryMonthJson = getMonthStatistics(getLastYearQueryMonth(periodId),
                monitorId, nodeId);

        //最终统计结果
        JSONObject obj = processResult(attributeIds, currentDayJson, currentMonthJson,
                lastMonthJson, lastYearCurrentMonthJson, queryMonthJson, lastYearQueryMonthJson);
        LOG.info("查询节点统计数据结束,结果:{},用时:{}",JsonUtil.getJsonString(obj),System.currentTimeMillis()-time);
        return obj;
    }

    private JSONObject processResult(JSONArray attributeIds, JSONObject currentDayJson,
                                     JSONObject currentMonthJson, JSONObject lastMonthJson,
                                     JSONObject lastYearCurrentMonthJson, JSONObject queryMonthJson,
                                     JSONObject lastYearQueryMonthJson) {
        if (attributeIds == null || attributeIds.isEmpty()) {
            return null;
        }

        JSONObject json = new JSONObject();

        for (Object obj : attributeIds) {
            String attrId = (String) obj;
            JSONObject attrJson = new JSONObject();
            //日值
            attrJson.put("dayValue", getAttrValue(currentDayJson, attrId));
            //月值
            Integer monthValue = getAttrValue(currentMonthJson, attrId);
            attrJson.put("monthValue", monthValue);
            //上月值
            Integer lastMonthValue = getAttrValue(lastMonthJson, attrId);
            //去年同月值
            Integer lastYearCurrentMonthValue = getAttrValue(lastYearCurrentMonthJson, attrId);
            //环比值
            if (lastMonthValue == 0) {
                attrJson.put("monthLinkRelativeRatio", 0);
            } else {
                Double monthLinkRelativeRatio = monthValue.doubleValue()
                        / lastMonthValue.doubleValue();
                attrJson.put("monthLinkRelativeRatio", monthLinkRelativeRatio);
            }

            //同比值
            if (lastYearCurrentMonthValue == 0) {
                attrJson.put("monthLearOnYear", 0);
            } else {
                Double monthLearOnYear = monthValue.doubleValue()
                        / lastYearCurrentMonthValue.doubleValue();
                attrJson.put("monthLearOnYear", monthLearOnYear);
            }

            //财月累计值
            Integer queryMonthValue = getAttrCumulativeValue(queryMonthJson, attrId);
            attrJson.put("queryMonthValue", queryMonthValue);
            //去年同财月累计值
            Integer lastYearQueryMonthValue = getAttrCumulativeValue(lastYearQueryMonthJson,
                    attrId);

            //财月累计值环比
            if (lastYearQueryMonthValue == 0) {
                attrJson.put("queryMonthLinkRelativeRatio", 0);
            } else {
                Double queryMonthLinkRelativeRatio = queryMonthValue.doubleValue()
                        / lastYearQueryMonthValue.doubleValue();
                attrJson.put("queryMonthLinkRelativeRatio", queryMonthLinkRelativeRatio);
            }

            json.put(attrId, attrJson);
        }

        return json;
    }

    private Integer getAttrCumulativeValue(JSONObject jsonObj, String attrId) {

        if (jsonObj != null && StringUtils.isNotBlank(attrId)) {
            JSONArray dataSet = jsonObj.getJSONArray(ServiceCenterConstant.DATA_SET);
            if (dataSet != null && !dataSet.isEmpty()) {
                for (Object o : dataSet) {
                    JSONObject json = JsonUtil.getJson(o);
                    Integer value = json.getInteger("stat012");
                    if (attrId.equals(value)) {
                        return value;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * getAttrValue:根据属性id，获取累计值
     * @author caozhenx
     * @param currentDayJson
     * @param attrId
     * @return
     */
    private Integer getAttrValue(JSONObject jsonObj, String attrId) {

        if (jsonObj != null && StringUtils.isNotBlank(attrId)) {
            JSONArray dataSet = jsonObj.getJSONArray(ServiceCenterConstant.DATA_SET);
            if (dataSet != null && !dataSet.isEmpty()) {
                for (Object o : dataSet) {
                    JSONObject json = JsonUtil.getJson(o);
                    Integer value = json.getInteger("stat011");
                    if (attrId.equals(value)) {
                        return value;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * getLastYearQueryMonth:根据传入周期id 获取去年同一时间的时间类
     * @author caozhenx
     * @param periodId
     * @return
     */
    private Calendar getLastYearQueryMonth(String periodId) {

        if (StringUtils.isBlank(periodId)) {
            return null;
        }

        //查询传入财月的周期类信息   
        JSONObject periodIdJson = periodService.queryPeriod(periodId, null, null, null, null);

        if (periodIdJson == null) {
            return null;
        }
        //结果集
        JSONArray dataset = periodIdJson.getJSONArray(ServiceCenterConstant.DATA_SET);

        if (dataset == null || dataset.isEmpty()) {
            return null;
        }

        JSONObject json = dataset.getJSONObject(0);
        Integer year = json.getInteger("peid001");
        //去年的时间
        Integer lastYear = year - 1;
        //yyyy-MM-dd
        String beginTime = json.getString("peid003");

        Calendar cl = Calendar.getInstance();
        if (StringUtils.isNotBlank(beginTime)) {
            String[] times = beginTime.split("-");
            if (times.length == 3) {
                //月
                String month = times[1];
                //日
                String day = times[2];

                cl.set(lastYear, Integer.parseInt(month), Integer.parseInt(day));
            }
        }

        return cl;

    }

    /**
     * getLastYearCurrentMonth:获取去年当前月  日期类
     * @author caozhenx
     * @return
     */
    private Calendar getLastYearCurrentMonth() {

        Calendar cl = Calendar.getInstance();
        cl.add(Calendar.YEAR, -1);
        return cl;
    }

    /**
     * getLastMonth:获取上个月   日期类
     * @author caozhenx
     * @return
     */
    private Calendar getLastMonth() {

        Calendar cl = Calendar.getInstance();
        cl.add(Calendar.MONTH, -1);
        return cl;
    }

    private JSONObject getMonthStatistics(Calendar cl, String monitorClass, String monitorId) {

        //查询时间为空时  会获取不到周期类，这样不准许查询（若准许结果集会很大）
        if (cl == null) {
            return null;
        }

        cl.set(Calendar.DAY_OF_MONTH, 1);
        String firstDay = DateUtil.parseFormatDate(cl.getTime(), DateConstant.FORMATE_YYYY_MM_DD);

        cl.add(Calendar.MONTH, 1);
        cl.set(Calendar.DAY_OF_MONTH, 0);
        String lastDay = DateUtil.parseFormatDate(cl.getTime(), DateConstant.FORMATE_YYYY_MM_DD);
        String period = getPeriodId(firstDay, lastDay);

        JSONObject json = statisticsService.queryStatistics(monitorClass, monitorId, period, null);

        return json;
    }

    /**
     * getDayStatistics:根据传入时间，所属监管类，节点id查询  节点当前时间的所有卷积属性的日累计数据
     * @author caozhenx
     * @param cl 日期类
     * @param monitorClass 所属监管类
     * @param monitorId 节点id
     * @return
     */
    private JSONObject getDayStatistics(Calendar cl, String monitorClass, String monitorId) {

        String time = DateUtil.parseFormatDate(cl.getTime(), DateConstant.FORMATE_YYYY_MM_DD);
        String period = getPeriodId(time, time);
        return statisticsService.queryStatistics(monitorClass, monitorId, period, null);
    }

    /**
     * getPeriodId:根据开始时间 结束时间查询 周期类id
     * @author caozhenx
     * @param beginTime 开始时间 yyyy-MM-dd
     * @param endTime 结束时间 yyyy-MM-dd
     * @return
     */
    private String getPeriodId(String beginTime, String endTime) {

        //查询返回结果
        JSONObject todayJson = periodService.queryPeriod(null, null, null, beginTime, endTime);

        if (todayJson == null) {
            return null;
        }

        //周期类结果集
        JSONArray todayArray = todayJson.getJSONArray(ServiceCenterConstant.DATA_SET);

        if (todayArray.isEmpty()) {
            return null;
        }

        JSONObject period = JsonUtil.getJson(todayArray.get(0));
        return period.getString(StatisticsConstant.ID);

    }

    public static void main(String[] args) {
        Calendar cl = Calendar.getInstance();
        cl.set(Calendar.DAY_OF_MONTH, 1);
        String firstDay = DateUtil.parseFormatDate(cl.getTime(), DateConstant.FORMATE_YYYY_MM_DD);
        System.out.println(firstDay);
    }

}
