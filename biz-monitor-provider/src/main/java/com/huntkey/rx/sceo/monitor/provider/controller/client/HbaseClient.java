/**
 * Project Name:security-center-provider
 * File Name:HbaseClient.java
 * Package Name:com.huntkey.rx.sceo.security.center.provider.controller.client
 * Date:2017年6月30日下午5:38:01
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 */

package com.huntkey.rx.sceo.monitor.provider.controller.client;

import org.springframework.cloud.netflix.feign.FeignClient;
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
@FeignClient(value = "modelerCommon-provider", fallback = HbaseClientFallback.class)
public interface HbaseClient {

    @RequestMapping(value = "/esAndhbase", method = RequestMethod.GET)
    Result queryFromEsAndHbase(@RequestParam(value = "datas") String datas);


    @RequestMapping(value = "/esAndhbase/add", method = RequestMethod.POST)
    Result addDatasToEsAndHbase(@RequestParam(value = "datas") String datas);
    /**
     * 
     * deleteEsAndHbase: 删除数据
     * @author lijie
     * @param datas
     * @return
     */
    @RequestMapping(value= "/esAndhbase", method = RequestMethod.DELETE)
    Result deleteEsAndHbase(@RequestParam(value = "datas") String datas);
}

