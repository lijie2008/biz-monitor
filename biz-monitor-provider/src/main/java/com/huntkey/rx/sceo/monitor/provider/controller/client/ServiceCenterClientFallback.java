/**
 * Project Name:security-center-provider
 * File Name:HbaseClientFallback.java
 * Package Name:com.huntkey.rx.sceo.security.center.provider.controller.client
 * Date:2017年6月30日下午5:39:00
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.controller.client;

import org.springframework.stereotype.Component;

import com.huntkey.rx.commons.utils.rest.Result;

/**
 * ClassName:ServiceCenterClientFallback
 * Function: TODO ADD FUNCTION
 * Reason:	 TODO ADD REASON
 * Date:     2017年6月30日 下午5:39:00
 * @author   lijie
 * @version  
 * @see 	 
 */
@Component
public class ServiceCenterClientFallback implements ServiceCenterClient{


    @Override
    public Result queryServiceCenter(String data) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("ServiceCenter client queryServiceCenter fallback");
        return result;
    }

    @Override
    public Result getMonitorTreeNodes(String edmcNameEn, String searchDate, String rootNodeId) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("监管服务无法连接共享服务中心树节点查询服务！");
        return result;
    }

}

