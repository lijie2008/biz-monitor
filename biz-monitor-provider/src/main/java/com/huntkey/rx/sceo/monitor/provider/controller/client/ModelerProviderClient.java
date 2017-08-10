/**
 * Project Name:Name:biz-monitor-provider
 * File Name:ServiceCenterClient.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.controller.client
 * Date:2017年8月07日下午5:38:01
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 */

package com.huntkey.rx.sceo.monitor.provider.controller.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.huntkey.rx.commons.utils.rest.Result;

/**
 * ServiceCenterClient
 * Function: 调用service center接口
 * Date:     2017年6月30日 下午5:38:01
 * @author caozhenx
 */
@FeignClient(value = "modeler-provider", fallback = ModelerProviderClientFallback.class)
public interface ModelerProviderClient {

    @RequestMapping(value = "/classes/child/{id}/{sid}")
    Result checkIsChileNode(@PathVariable(value = "id") String id,@PathVariable(value="sid") String sid);

}
