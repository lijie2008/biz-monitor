/**
 * Project Name:biz-monitor-provider
 * File Name:StatisticsServiceImpl.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.service.impl
 * Date:2017年8月7日上午10:41:19
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huntkey.rx.edm.entity.StatisticsEntity;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ServiceCenterClient;
import com.huntkey.rx.sceo.monitor.provider.service.StatisticsDService;
import com.huntkey.rx.sceo.orm.common.model.OrmParam;
import com.huntkey.rx.sceo.orm.common.type.SQLSymbolEnum;
import com.huntkey.rx.sceo.orm.service.OrmService;

/**
 * ClassName:StatisticsServiceImpl
 * Function: 查询统计类数据
 * Date:     2017年8月7日 上午10:41:19
 * @author   caozhenx
 * @version  
 * @see 	 
 */
@Service("statisticsDService")
public class StatisticsDServiceImpl implements StatisticsDService {
    private static final Logger LOG = LoggerFactory.getLogger(StatisticsDServiceImpl.class);

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
    
    @Autowired 
    OrmService ormService;

    /**
     * queryStatistics:查询统计报表指定卷积属性数据
     * @param monitorClass  所属监管类  例：部门树类、岗位树类
     * @param monitorId     节点id
     * @param periodId      周期类id
     * @param attributeId   卷积属性id
     * @author caozhenx
     * @return Result 查询结果
     * @throws Exception 
     */
    @Override
    public List<StatisticsEntity> queryStatistics(String monitorClass, String monitorId, String periodId,
                                      String attributeId) throws Exception {
        LOG.info("查询统计类信息开始,monitorClass:{},monitorId:{},periodId:{},attributeId:{}",new Object [] {monitorClass,monitorId,periodId,attributeId});
        long time = System.currentTimeMillis();
        
        OrmParam param = getQueryString(monitorClass, monitorId, periodId, attributeId);
        
        List<StatisticsEntity> statistics = ormService.selectBeanList(StatisticsEntity.class, param);
        
        return statistics;
    }

    private OrmParam getQueryString(String monitorClass, String monitorId, String periodId,
                                  String attributeId) throws Exception {
        
        OrmParam param = new OrmParam();
        
        param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
        
        //查询条件1 所性监管类
        if (StringUtils.isNotBlank(monitorClass)) {
            if(StringUtils.isNotBlank(param.getWhereExp()))
                param.setWhereExp(OrmParam.and(param.getWhereExp(), param.getEqualXML("stat_moniclass", monitorClass)));
            else
                param.setWhereExp(param.getEqualXML("stat_moniclass", monitorClass));
        }
        
        //查询条件2 节点id
        if (StringUtils.isNotBlank(monitorId)) {
            if(StringUtils.isNotBlank(param.getWhereExp()))
                param.setWhereExp(OrmParam.and(param.getWhereExp(), param.getEqualXML("stat_moniobj", monitorId)));
            else
                param.setWhereExp(param.getEqualXML("stat_moniobj", monitorId));
        }
        
        //查询条件3 周期点id
        if (StringUtils.isNotBlank(periodId)) {
            if(StringUtils.isNotBlank(param.getWhereExp()))
                param.setWhereExp(OrmParam.and(param.getWhereExp(), param.getEqualXML("stat_pridobj", periodId)));
            else
                param.setWhereExp(param.getEqualXML("stat_pridobj", periodId));
        }
        //查询条件4 属性id
        if (StringUtils.isNotBlank(attributeId)) {
            if(StringUtils.isNotBlank(param.getWhereExp()))
                param.setWhereExp(OrmParam.and(param.getWhereExp(), param.getEqualXML("stat_moniattr", attributeId)));
            else
                param.setWhereExp(param.getEqualXML("stat_moniattr", attributeId));
        }
        return param;
    }

    @Override
    public List<StatisticsEntity> queryStatistics(String moniIds, String periodId, String attributeIds) throws Exception{
        long time = System.currentTimeMillis();
        
        OrmParam queryString = getQueryString(moniIds, periodId, attributeIds);
        
        List<StatisticsEntity> staticE = ormService.selectBeanList(StatisticsEntity.class, queryString);
        
       return staticE;
    }

    private OrmParam getQueryString(String moniIds, String periodId, String attributeIds) throws Exception{
        
        OrmParam param = new OrmParam();
        param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
        
        //查询条件2 监管
        if (StringUtils.isNotBlank(moniIds)) {
            if(StringUtils.isNotBlank(param.getWhereExp()))
                param.setWhereExp(OrmParam.and(param.getWhereExp(), param.getConditionForInXML("stat_moniobj", moniIds.split("\\,"))));
            else
                param.setWhereExp(param.getConditionForInXML("stat_moniobj", moniIds.split("\\,")));
        }
        
        //查询条件3 周期点id
        if (StringUtils.isNotBlank(periodId)) {
            if(StringUtils.isNotBlank(param.getWhereExp()))
                param.setWhereExp(OrmParam.and(param.getWhereExp(), param.getEqualXML("stat_pridobj", periodId)));
            else
                param.setWhereExp(param.getEqualXML("stat_pridobj", periodId));
        }
        
        //查询条件4 属性id
        if (StringUtils.isNotBlank(attributeIds)) {
            if(StringUtils.isNotBlank(param.getWhereExp()))
                param.setWhereExp(OrmParam.and(param.getWhereExp(), param.getConditionForInXML("stat_moniattr", attributeIds.split("\\,"))));
            else
                param.setWhereExp(param.getConditionForInXML("stat_moniattr", attributeIds.split("\\,")));
        }
        return param;
    }
    
    
    

}
