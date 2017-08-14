/**
 * Project Name:biz-monitor-client
 * File Name:StatisticsService.java
 * Package Name:com.huntkey.rx.sceo.monitor.client.service
 * Date:2017年8月9日下午2:31:45
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.client.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.service.hystrix.EdmPropertyGroupClientFallback;

/**
 * ClassName:StatisticsService
 * Function: 调用provider端服务
 * Date:     2017年8月9日 下午2:31:45
 * @author   caozhenx
 * @version  
 * @see 	 
 */
@FeignClient(value = "biz-monitor-provider", fallback = EdmPropertyGroupClientFallback.class)
public interface EdmPropertyGroupClient {

    @RequestMapping(value = "/edmPropertyGroup/monitor/info", method = RequestMethod.GET)
    Result getMonitorInfo(@RequestBody JSONObject data);

}
