package com.huntkey.rx.sceo.monitor.provider.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ServiceCenterClient;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zhaomj on 2017/8/9.
 */
@Service
public class MonitorTreeServiceImpl implements MonitorTreeService {

    @Autowired
    ServiceCenterClient serviceCenterClient;

    @Override
    public JSONArray getMonitorTreeNodes(String edmcNameEn, String searchDate,String rootNodeId) {

        if(StringUtil.isNullOrEmpty(rootNodeId)){
            //根据时间查询根节点
        }else{
            //根据ID查询跟节点
        }

        //根据根节点ID，查询所有子节点
        Result result = serviceCenterClient.getMonitorTreeNodes(edmcNameEn,searchDate,rootNodeId);

        result.getData();
        return null;
    }
}
