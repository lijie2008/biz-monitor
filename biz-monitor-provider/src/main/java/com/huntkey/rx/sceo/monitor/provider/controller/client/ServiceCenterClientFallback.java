/**
 * Project Name:security-center-provider
 * File Name:HbaseClientFallback.java
 * Package Name:com.huntkey.rx.sceo.security.center.provider.controller.client
 * Date:2017年6月30日下午5:39:00
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.controller.client;

import java.util.List;

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
    public Result countByConditions(String data) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("countByConditions监管服务无法连接共享服务中心树节点查询服务！");
        return result;
    }

    @Override
    public Result queryServiceCenter(String data) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("ServiceCenter client queryServiceCenter fallback");
        return result;
    }

    @Override
    public Result getMonitorTreeNodes(String edmcNameEn, String searchDate, String rootNodeId,int type) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("监管服务无法连接共享服务中心树节点查询服务！");
        return result;
    }

    @Override
    public Result getMonitorClasses(String treeName, String beginTime, String endTime, String edmdVer, String edmcNameEn) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("监管服务无法连接共享服务中心 getMonitorClasses 服务！");
        return result;
    }

    @Override
    public Result getNodeResources(String name, List<String> nodes, String edmId,String edmName,int type) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("监管服务无法连接共享服务中心 getNodeResources 服务！");
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
    public Result queryTreeNodeResource(String orderId, String startDate, String endDate, String excNodeId, Boolean invalid) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("hbase client update fallback");
        return result;
    }

    @Override
    public Result getOrderMonitorTreeNodes(String edmcNameEn, String searchDate, String rootNodeId,int type) {
        // TODO Auto-generated method stub
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("hbase client getOrderMonitorTreeNodes fallback");
        return result;
    }

    @Override
    public Result load(String datas) {
        // TODO Auto-generated method stub
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("hbase client load fallback");
        return result;
    }

	@Override
	public Result searchResourceObj(String resourceClassId, String resourceValue) {
		// TODO Auto-generated method stub
		Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("监管服务无法连接共享服务中心 searchResourceObj 服务！");
        return result;
	}
}

