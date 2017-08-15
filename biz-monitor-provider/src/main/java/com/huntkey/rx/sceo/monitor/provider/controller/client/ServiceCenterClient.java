/**
 * Project Name:Name:biz-monitor-provider
 * File Name:ServiceCenterClient.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.controller.client
 * Date:2017年8月07日下午5:38:01
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 */

package com.huntkey.rx.sceo.monitor.provider.controller.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.huntkey.rx.commons.utils.rest.Result;

import java.util.List;

/**
 * ServiceCenterClient
 * Function: 调用service center接口
 * Date:     2017年6月30日 下午5:38:01
 * @author caozhenx
 */
@FeignClient(value = "serviceCenter-provider", fallback = ServiceCenterClientFallback.class)
public interface ServiceCenterClient {

    /**
     * queryServiceCenter:根据条件查询servicecenter信息
     * @author caozhenx
     * @param data orm查询条件
     * @return
     */
    @RequestMapping(value = "/servicecenter/find", method = RequestMethod.POST)
    Result queryServiceCenter(@RequestBody String data);

    /**
     * 根据根节点ID 和时间查询出监管树所有节点
     * @param edmcNameEn
     * @param searchDate
     * @return
     */
    @RequestMapping(value = "/servicecenter/business/monitors/trees/nodes", method = RequestMethod.GET)
    Result getMonitorTreeNodes(@RequestParam(value = "edmcNameEn") String edmcNameEn,
                               @RequestParam(value = "searchDate") String searchDate,
                               @RequestParam(value = "rootNodeId") String rootNodeId);

    /**
     * 查询监管树类列表，并根据查询条件统计监管类下监管树的数量
     * @param treeName
     * @param beginTime
     * @param endTime
     * @param edmdVer
     * @param edmcNameEn
     * @return
     */
    @RequestMapping(value = "/servicecenter/business/monitors", method = RequestMethod.GET)
    Result getMonitorClasses(@RequestParam(value = "treeName") String treeName,
                                    @RequestParam(value = "beginTime") String beginTime,
                                    @RequestParam(value = "endTime") String endTime,
                                    @RequestParam(value = "edmdVer") String edmdVer,
                                    @RequestParam(value = "edmcNameEn") String edmcNameEn);

    /**
     * 统计函数，支持查询条件
     * @param data
     * @return
     */
    @RequestMapping(value = "/servicecenter/count",method = RequestMethod.POST)
    Result countByConditions(@RequestBody String data);

    @RequestMapping(value = "/servicecenter/business/monitors/trees/resources", method = RequestMethod.GET)
    Result getNodeResources(@RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "nodes") List<String> nodes,
                            @RequestParam(value = "edmcId") String edmcId);

}
