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

/**
 * ServiceCenterClient
 * Function: 调用service center接口
 * Date:     2017年6月30日 下午5:38:01
 * @author caozhenx
 */
@FeignClient(value = "serviceCenter-provider", fallback = ServiceCenterClientFallback.class)
public interface ServiceCenterClient {

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

}
