package com.huntkey.rx.sceo.monitor.client.service.hystrix;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.service.MonitorTreeClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zhaomj on 2017/8/11.
 */
@Component
public class MonitorTreeClientFallback implements MonitorTreeClient {
    @Override
    public Result getMonitorTreeNodes(String edmcNameEn, String searchDate, String rootNodeId) {
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
    public Result getMonitorTrees(String treeName, String edmcNameEn, String beginTime, String endTime) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorTreeClient getMonitorTrees fallback");
        return result;
    }

    @Override
    public Result getNodeResources(String name, List<String> nodes, String edmcId) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorTreeClient getNodeResources fallback");
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
    public Result getNewMonitorTreeStartDate(String edmcNameEn,String classId) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorTreeClient getNewMonitorTreeStartDate fallback");
        return result;
    }
}
