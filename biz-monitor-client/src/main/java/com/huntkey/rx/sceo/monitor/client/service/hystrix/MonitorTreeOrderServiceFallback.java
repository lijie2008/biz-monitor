/**
 * Project Name:biz-monitor-client
 * File Name:MonitorTreeOrderServiceFallback.java
 * Package Name:com.huntkey.rx.sceo.monitor.client.service.hystrix
 * Date:2017年8月11日下午3:58:10
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.client.service.hystrix;

import org.springframework.stereotype.Component;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.service.MonitorTreeOrderService;

/**
 * ClassName:MonitorTreeOrderServiceFallback
 * Function: TODO ADD FUNCTION
 * Reason:	 TODO ADD REASON
 * Date:     2017年8月11日 下午3:58:10
 * @author   lijie
 * @version  
 * @see 	 
 */
@Component
public class MonitorTreeOrderServiceFallback implements MonitorTreeOrderService{

    @Override
    public Result queryNotUsingResource(String orderId, String nodeId, int currentPage,
                                        int pageSize) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("fallback");
        return result;
    }

    @Override
    public Result checkNodeResource(String nodeId, String startDate, String endDate) {
        
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("fallback");
        return result;
    }

    @Override
    public Result addOtherNode(String orderId) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("fallback");
        return result;
    }

    @Override
    public Result store(String orderId) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("fallback");
        return result;
    }

    @Override
    public Result revoked(String orderId) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("fallback");
        return result;
    }

    @Override
    public Result checkAvailableResource(String orderId) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("fallback");
        return result;
    }
    
}

