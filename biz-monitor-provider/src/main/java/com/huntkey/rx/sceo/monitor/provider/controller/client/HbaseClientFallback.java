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
 * ClassName:HbaseClientFallback
 * Function: TODO ADD FUNCTION
 * Reason:	 TODO ADD REASON
 * Date:     2017年6月30日 下午5:39:00
 * @author   lijie
 * @version  
 * @see 	 
 */
@Component
public class HbaseClientFallback implements HbaseClient{

    @Override
    public Result find(String datas) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("hbase client find fallback");
        return result;
    }

    @Override
    public Result add(String datas) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("hbase client add fallback");
        return result;
    }

    @Override
    public Result delete(String datas) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("hbase client delete fallback");
        return result;
    }

	@Override
	public Result update(String datas) {
		// TODO Auto-generated method stub
		Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("hbase client update fallback");
        return result;
	}

    @Override
    public Result queryTreeNodeResource(String orderId, String startDate, String endDate, String excNodeId) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("hbase client update fallback");
        return result;
    }

}

