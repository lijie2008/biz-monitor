/**
 * Project Name:security-center-provider
 * File Name:HbaseClientFallback.java
 * Package Name:com.huntkey.rx.sceo.security.center.provider.controller.client
 * Date:2017年6月30日下午5:39:00
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.client.service.hystrix;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.service.EdmPropertyGroupClient;

/**
 * ClassName:StatisticsClientFallback
 * Function: 
 * Date:     2017年6月30日 下午5:39:00
 * @author   lijie
 * @version  
 * @see 	 
 */
@Component
public class EdmPropertyGroupClientFallback implements EdmPropertyGroupClient{

    @Override
    public Result getMonitorIds(JSONObject data) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("biz monitor provider client getMonitorIds fallback");
        return result;
    }




}

