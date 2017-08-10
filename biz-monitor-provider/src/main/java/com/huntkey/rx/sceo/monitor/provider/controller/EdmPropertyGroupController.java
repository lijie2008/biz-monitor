/**
 * Project Name:biz-monitor-provider
 * File Name:EdmPropertyGroupController.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.controller
 * Date:2017年8月10日上午8:48:05
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.provider.biz.EdmPropertyGroupBiz;

/**
 * ClassName:EdmPropertyGroupController
 * Function: TODO ADD FUNCTION
 * Reason:	 TODO ADD REASON
 * Date:     2017年8月10日 上午8:48:05
 * @author   caozhenx
 * @version  
 * @see 	 
 */
@RestController
@RequestMapping("/edmPropertyGroup")
public class EdmPropertyGroupController {
    
    @Autowired
    EdmPropertyGroupBiz edmPropertyGroupBiz;
    
    /**
     * getMonitorIds:根据传入查询条件  查询同分组下 所有的monitor类的相关信息
     * @author caozhenx
     * @return
     */
    @RequestMapping("/getMonitorId")
    public Result getMonitorIds(@RequestBody JSONObject jsonObject){
        Result result = edmPropertyGroupBiz.getMonitorIds(jsonObject);
        return result;
    }

}

