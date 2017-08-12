package com.huntkey.rx.sceo.monitor.client.service.hystrix;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.service.MonitorTreeClient;
import org.springframework.stereotype.Component;

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
}
