package com.huntkey.rx.sceo.monitor.client.service.hystrix;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.service.MonitorClient;
import com.huntkey.rx.sceo.monitor.commom.model.AddMonitorTreeTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import org.springframework.stereotype.Component;

/**
 * Created by zhaomj on 2017/8/11.
 */
@Component
public class MonitorClientFallback implements MonitorClient {

    @Override
    public Result nodeDetail(String key,String levelCode) {
        // TODO Auto-generated method stub
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorClient nodeDetail fallback");
        return result;
    }

    @Override
    public Result saveNodeDetail(NodeTo nodeDetail) {
        // TODO Auto-generated method stub
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorClient saveNodeDetail fallback");
        return result;
    }

    @Override
    public Result deleteNodeResource(String key,String levelCode, String resourceId) {
        // TODO Auto-generated method stub
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorClient deleteNodeResource fallback");
        return result;
    }

    @Override
    public Result addResource(String tempId,String levelCode, String resourceId,String resourceText) {
        // TODO Auto-generated method stub
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorClient addResource fallback");
        return result;
    }

    @Override
    public Result addNode(String key,String levelCode, int type) {
        // TODO Auto-generated method stub
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorClient addNode fallback");
        return result;
    }

    @Override
    public Result deleteNode(String key,String levelCode,int type) {
        // TODO Auto-generated method stub
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorClient deleteNode fallback");
        return result;
    }

    @Override
    public Result moveNode(String key, String moveLvlCode, String desLvlCode, int type) {
        // TODO Auto-generated method stub
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorClient moveNode fallback");
        return result;
    }
}
