/**
 * Project Name:biz-monitor-provider
 * File Name:EdmPropertyGroupController.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.controller
 * Date:2017年8月10日上午8:48:05
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.service.EdmPropertyGroupClient;

/**
 * ClassName:EdmPropertyGroupController
 * Function: edm属性关联分组表查询
 * Date:     2017年8月10日 上午8:48:05
 * @author   caozhenx
 * @version  
 * @see 	 
 */
@RestController
@RequestMapping("/v1/edmPropertyGroup")
public class EdmPropertyGroupController {
    
    @Autowired
    EdmPropertyGroupClient edmPropertyGroupClient;
    
    /**
     * getMonitorIds:根据传入查询条件  查询同分组下 所有的monitor类的相关信息
     * @author caozhenx
     * @return
     */
    @RequestMapping("/monitor/info")
    public Result getMonitorIds(@RequestBody JSONObject jsonObject){
        Result result = edmPropertyGroupClient.getMonitorInfo(jsonObject);
        return result;
    }

}

