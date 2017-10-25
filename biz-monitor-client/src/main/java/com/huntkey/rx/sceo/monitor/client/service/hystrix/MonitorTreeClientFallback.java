package com.huntkey.rx.sceo.monitor.client.service.hystrix;

import org.springframework.stereotype.Component;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.service.MonitorTreeClient;

/**
 * Created by zhaomj on 2017/8/11.
 */
@Component
public class MonitorTreeClientFallback implements MonitorTreeClient {
    @Override
    public Result getMonitorTreeNodes(String rootEdmcNameEn, String searchDate, String rootNodeId,String edmId,boolean flag) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorTreeClient getMonitorTreeNodes fallback");
        return result;
    }

    @Override
    public Result getMonitors(String treeName, String beginTime, String endTime) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorTreeClient getMonitors fallback");
        return result;
    }

    @Override
    public Result getMonitorTrees(String treeName, String edmcNameEn,String edmId, String beginTime, String endTime) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorTreeClient getMonitorTrees fallback");
        return result;
    }

    @Override
    public Result getConProperties(String edmcNameEn, boolean enable) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorTreeClient getConProperties fallback");
        return result;
    }

    @Override
    public Result getNewMonitorTreeStartDate(String edmcNameEn) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorTreeClient getNewMonitorTreeStartDate fallback");
        return result;
    }

	@Override
	public Result searchResourceObj(String resourceClassId, String resourceValue) {
		// TODO Auto-generated method stub
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorTreeClient searchResourceObj fallback");
        return result;
	}
}
