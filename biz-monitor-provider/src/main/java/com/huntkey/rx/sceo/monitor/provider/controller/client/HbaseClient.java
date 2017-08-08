/**
 * Project Name:security-center-provider
 * File Name:HbaseClient.java
 * Package Name:com.huntkey.rx.sceo.security.center.provider.controller.client
 * Date:2017年6月30日下午5:38:01
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
 * ClassName:HbaseClient
 * Function: 调用Hbase接口
 * Reason:	 TODO ADD REASON
 * Date:     2017年6月30日 下午5:38:01
 * @author lijie
 * @version
 * @see
 */
@FeignClient(value = "serviceCenter-provider", fallback = HbaseClientFallback.class,url="http://192.168.13.34:2008")
public interface HbaseClient {

    @RequestMapping(value = "/servicecenter/find", method = RequestMethod.POST)
    Result find(@RequestBody String datas);


    @RequestMapping(value = "/servicecenter/add", method = RequestMethod.POST)
    Result add(@RequestParam(value = "datas") String datas);
    /**
     * 
     * deleteEsAndHbase: 删除数据
     * @author lijie
     * @param datas
     * @return
     */
    @RequestMapping(value= "/servicecenter/delete", method = RequestMethod.DELETE)
    Result delete(@RequestParam(value = "datas") String datas);
    
    @RequestMapping(value= "/servicecenter/update", method = RequestMethod.POST)
    Result update(@RequestParam(value = "datas") String datas);
}

