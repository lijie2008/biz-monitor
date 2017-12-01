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

import com.huntkey.rx.commons.utils.rest.Result;

/**
 * ServiceCenterClient
 * Function: 调用service center接口
 * Date:     2017年6月30日 下午5:38:01
 * @author caozhenx
 */
//@FeignClient(value = "SERVICECENTER-PROVIDER", url = "http://10.3.99.47:2008", fallback = ServiceCenterClientFallback.class) //单机调试使用(注意不要提交此行)
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
}
