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
import com.huntkey.rx.sceo.monitor.commom.constant.DateConstant;
import com.huntkey.rx.sceo.monitor.commom.constant.ServiceCenterConstant;
import com.huntkey.rx.sceo.monitor.commom.constant.StatisticsConstant;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
import com.huntkey.rx.sceo.monitor.provider.biz.StatisticsBiz;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeService;
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

    @Autowired
    MonitorTreeService monitorTreeService;

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
            jsonObj = periodService.queryPeriod(null, year.toString(), "M", null, null);
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
     * 查询条件
     *     {
     *     "monitorId":"所属监管类id",
     *     "edmcNameEn":"所属监管类英文名",
     *     "periodId":"周期类id",
     *     "attributeIds":[{"attrId":"attrid1","attrName":"属性1"},{"attrId":"attrid2","attrName":"属性2"}],
     *     "type":"查询类型，0查询本节点，1查询子节点",
     *     "treeNode":{
     *             "nodeId":"节点id",
     *             "nodeName":"节点名"
     *         }
     *     }
     *     
     * 返回结果
     *       {
     *      "monitorId":"所属监管类id",
     *     "periodId":"周期类id",
     *     attributeIds:[{"attrId":"attrid1","attrName":"属性1"},{"attrId":"attrid2","attrName":"属性2"}],
     *     "treeNode":{
     *             "nodeId":"节点id",
     *     "nodeName":"节点名",
     *     "statistics":[
     *          {"attrId":"attrid1","attrName":"属性1","dayValue":"日值","monthValue":"月值","monthLinkRelativeRatio":"环比值","monthLearOnYear":"同比值","queryMonthValue":"财年累计值","queryMonthLinkRelativeRatio":"财年累计同比值"},
     *          {"attrId":"attrid2","attrName":"属性2","dayValue":"日值","monthValue":"月值","monthLinkRelativeRatio":"环比值","monthLearOnYear":"同比值","queryMonthValue":"财年累计值","queryMonthLinkRelativeRatio":"财年累计同比值"}
     *      ],
     *     "childNodes":[
     *          {
     *          "nodeId":"节点id",
     *          "nodeName":"节点名",
     *          "statistics":[
     *                  {"attrId":"attrid1","attrName":"属性1","dayValue":"日值","monthValue":"月值","monthLinkRelativeRatio":"环比值","monthLearOnYear":"同比值","queryMonthValue":"财年累计值","queryMonthLinkRelativeRatio":"财年累计同比值"},
     *                  {"attrId":"attrid2","attrName":"属性2","dayValue":"日值","monthValue":"月值","monthLinkRelativeRatio":"环比值","monthLearOnYear":"同比值","queryMonthValue":"财年累计值","queryMonthLinkRelativeRatio":"财年累计同比值"}
     *              ]
     *          },
     *          
     *          {
     *          "nodeId":"节点id",
     *          "nodeName":"节点名",
     *          "statistics":[
     *                  {"attrId":"attrid1","attrName":"属性1","dayValue":"日值","monthValue":"月值","monthLinkRelativeRatio":"环比值","monthLearOnYear":"同比值","queryMonthValue":"财年累计值","queryMonthLinkRelativeRatio":"财年累计同比值"},
     *                  {"attrId":"attrid2","attrName":"属性2","dayValue":"日值","monthValue":"月值","monthLinkRelativeRatio":"环比值","monthLearOnYear":"同比值","queryMonthValue":"财年累计值","queryMonthLinkRelativeRatio":"财年累计同比值"}
     *              ]
     *          }
     *      ]
     *         }
     *     }
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
            result.setErrMsg("所属监管类id不可为空..");
            return result;
        }

        String edmcNameEn = json.getString(StatisticsConstant.EDMC_NAME_EN);
        if (StringUtils.isBlank(monitorId)) {
            result.setRetCode(Result.RECODE_ERROR);
            result.setErrMsg("所属监管类英文名不可为空..");
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
        String treeNodeId = treeNode.getString(StatisticsConstant.TREE_NODE_ID);
        if (StringUtils.isBlank(treeNodeId)) {
            result.setRetCode(Result.RECODE_ERROR);
            result.setErrMsg("查询节点id不可为空..");
            return result;
        }

        String type = json.getString(StatisticsConstant.QUERY_TYPE);

        //查询type为1时表示查询子节点数据  否则为查询本节点数据
        if (StatisticsConstant.QUERY_TYPE_1.equals(type)) {
            //根据节点id，查询其子节点
            JSONArray chileNodes = getChileNodes(treeNodeId, edmcNameEn);
            if (chileNodes != null && !chileNodes.isEmpty()) {
                for (Object o : chileNodes) {
                    JSONObject jsonObj = JsonUtil.getJson(o);
                    String id = jsonObj.getString(StatisticsConstant.ID);
                    String name = jsonObj.getString("moni002");

                    jsonObj.put(StatisticsConstant.TREE_NODE_ID, id);
                    jsonObj.put(StatisticsConstant.TREE_NODE_NAME, name);
                    jsonObj.put(StatisticsConstant.STATISTICS,
                            queryStatistics(monitorId, id, periodId, attributeIds));
                }
            }

            treeNode.put(StatisticsConstant.CHILD_NODES, chileNodes);
        } else {
            JSONArray js = queryStatistics(monitorId, treeNodeId, periodId, attributeIds);
            treeNode.put(StatisticsConstant.STATISTICS, js);
        }

        json.put(StatisticsConstant.TREE_NODE, treeNode);
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(json);

        return result;
    }

    private JSONArray getChileNodes(String treeNodeId, String edmcNameEn) {

        if (StringUtils.isNotBlank(treeNodeId) && StringUtils.isNotBlank(edmcNameEn)) {
            return monitorTreeService.getChileNodes(treeNodeId, edmcNameEn);
        }

        return null;
    }

    /**
     * queryStatistics:查询节点统计数据
     * @author caozhenx
     * @param monitorId 所属监管类id
     * @param nodeId 节点id
     * @param periodId 周期类id
     * @param attributeIds 属性集
     * @return [
                {"attrId":"attrid1","attrName":"属性1","dayValue":"日值","monthValue":"月值","monthLinkRelativeRatio":"环比值","monthLearOnYear":"同比值","queryMonthValue":"财年累计值","queryMonthLinkRelativeRatio":"财年累计同比值"},
                {"attrId":"attrid2","attrName":"属性2","dayValue":"日值","monthValue":"月值","monthLinkRelativeRatio":"环比值","monthLearOnYear":"同比值","queryMonthValue":"财年累计值","queryMonthLinkRelativeRatio":"财年累计同比值"}
            ]
     */
    public JSONArray queryStatistics(String monitorId, String nodeId, String periodId,
                                     JSONArray attributeIds) {

        LOG.info("查询节点统计数据开始,monitorId:{},nodeId:{},periodId:{},attributeIds:{}",
                new Object[] { monitorId, nodeId, periodId, JsonUtil.getJsonString(attributeIds) });
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
        JSONArray obj = processResult(attributeIds, currentDayJson, currentMonthJson, lastMonthJson,
                lastYearCurrentMonthJson, queryMonthJson, lastYearQueryMonthJson);
        LOG.info("查询节点统计数据结束,结果:{},用时:{}", JsonUtil.getJsonString(obj),
                System.currentTimeMillis() - time);
        return obj;
    }

    /**
     * processResult:处理查询到的结果
     * @author caozhenx
     * @param attributeIds  卷积属性集
     * @param currentDayJson 日数据
     * @param currentMonthJson 月数据
     * @param lastMonthJson 上月数据
     * @param lastYearCurrentMonthJson 去年同月数据
     * @param queryMonthJson 传入的财月数据
     * @param lastYearQueryMonthJson 传入财月  去年同月数据
     * @return  [
                {"attrId":"attrid1","attrName":"属性1","dayValue":"日值","monthValue":"月值","monthLinkRelativeRatio":"环比值","monthLearOnYear":"同比值","queryMonthValue":"财年累计值","queryMonthLinkRelativeRatio":"财年累计同比值"},
                {"attrId":"attrid2","attrName":"属性2","dayValue":"日值","monthValue":"月值","monthLinkRelativeRatio":"环比值","monthLearOnYear":"同比值","queryMonthValue":"财年累计值","queryMonthLinkRelativeRatio":"财年累计同比值"}
            ]
     */
    private JSONArray processResult(JSONArray attributeIds, JSONObject currentDayJson,
                                    JSONObject currentMonthJson, JSONObject lastMonthJson,
                                    JSONObject lastYearCurrentMonthJson, JSONObject queryMonthJson,
                                    JSONObject lastYearQueryMonthJson) {
        if (attributeIds == null || attributeIds.isEmpty()) {
            return null;
        }

        JSONArray json = new JSONArray();

        for (Object obj : attributeIds) {
            JSONObject attrJson = JsonUtil.getJson(obj);
            String attrId = attrJson.getString(StatisticsConstant.ATTRIBUTE_ID);
            String attrName = attrJson.getString(StatisticsConstant.ATTRIBUTE_NAME);

            JSONObject attrValJson = new JSONObject();

            attrValJson.put(StatisticsConstant.ATTRIBUTE_ID, attrId);
            attrValJson.put(StatisticsConstant.ATTRIBUTE_NAME, attrName);
            //日值
            attrValJson.put("dayValue", getAttrValue(currentDayJson, attrId));
            //月值
            Double monthValue = getAttrValue(currentMonthJson, attrId);
            attrValJson.put("monthValue", monthValue);
            //上月值
            Double lastMonthValue = getAttrValue(lastMonthJson, attrId);
            //去年同月值
            Double lastYearCurrentMonthValue = getAttrValue(lastYearCurrentMonthJson, attrId);
            //环比值
            if (lastMonthValue == 0) {
                attrValJson.put("monthLinkRelativeRatio", 0);
            } else {
                Double monthLinkRelativeRatio = monthValue.doubleValue()
                        / lastMonthValue.doubleValue();
                attrValJson.put("monthLinkRelativeRatio", monthLinkRelativeRatio * 100);
            }

            //同比值
            if (lastYearCurrentMonthValue == 0) {
                attrValJson.put("monthLearOnYear", 0);
            } else {
                Double monthLearOnYear = monthValue.doubleValue()
                        / lastYearCurrentMonthValue.doubleValue();
                attrValJson.put("monthLearOnYear", monthLearOnYear * 100);
            }

            //财月累计值
            Double queryMonthValue = getAttrCumulativeValue(queryMonthJson, attrId);
            attrValJson.put("queryMonthValue", queryMonthValue);
            //去年同财月累计值
            Double lastYearQueryMonthValue = getAttrCumulativeValue(lastYearQueryMonthJson,
                    attrId);

            //财月累计值环比
            if (lastYearQueryMonthValue == 0) {
                attrValJson.put("queryMonthLinkRelativeRatio", 0);
            } else {
                Double queryMonthLinkRelativeRatio = queryMonthValue.doubleValue()
                        / lastYearQueryMonthValue.doubleValue();
                attrValJson.put("queryMonthLinkRelativeRatio", queryMonthLinkRelativeRatio * 100);
            }

            json.add(attrValJson);
        }

        return json;
    }

    private Double getAttrCumulativeValue(JSONObject jsonObj, String attrId) {

        if (jsonObj != null && StringUtils.isNotBlank(attrId)) {
            JSONArray dataSet = jsonObj.getJSONArray(ServiceCenterConstant.DATA_SET);
            if (dataSet != null && !dataSet.isEmpty()) {
                for (Object o : dataSet) {
                    JSONObject json = JsonUtil.getJson(o);
                    Double value = json.getDouble("stat012");
                    String dataAttrId = json.getString("stat003");
                    if (attrId.equals(dataAttrId)) {
                        return value;
                    }
                }
            }
        }
        return 0d;
    }

    /**
     * getAttrValue:根据属性id，获取累计值
     * @author caozhenx
     * @param currentDayJson
     * @param attrId
     * @return
     */
    private Double getAttrValue(JSONObject jsonObj, String attrId) {

        if (jsonObj != null && StringUtils.isNotBlank(attrId)) {
            JSONArray dataSet = jsonObj.getJSONArray(ServiceCenterConstant.DATA_SET);
            if (dataSet != null && !dataSet.isEmpty()) {
                for (Object o : dataSet) {
                    JSONObject json = JsonUtil.getJson(o);
                    Double value = json.getDouble("stat011");
                    String dataAttrId = json.getString("stat003");
                    if (attrId.equals(dataAttrId)) {
                        return value;
                    }
                }
            }
        }
        return 0d;
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
        JSONObject dayJson = periodService.queryPeriod(null, null, null, beginTime, endTime);

        if (dayJson == null) {
            return null;
        }

        //周期类结果集
        JSONArray dayArray = dayJson.getJSONArray(ServiceCenterConstant.DATA_SET);

        if (dayArray.isEmpty()) {
            return null;
        }

        JSONObject period = JsonUtil.getJson(dayArray.get(0));
        return period.getString(StatisticsConstant.ID);

    }

}
