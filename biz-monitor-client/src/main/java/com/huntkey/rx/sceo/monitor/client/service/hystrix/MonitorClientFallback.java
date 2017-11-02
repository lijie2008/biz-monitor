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
    public Result tempTree(String tempId, String validDate,int type, boolean flag) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorClient tempTree fallback");
        return result;
    }
    
    @Override
    public Result checkOrder(String classId, String rootId, int type) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorClient treeMaintaince fallback");
        return result;
    }

    @Override
    public Result editBefore(String key, boolean flag) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorClient treeMaintaince fallback");
        return result;
    }
    
    @Override
    public Result addMonitorTree(AddMonitorTreeTo addMonitorTreeTo) {
        // TODO Auto-generated method stub
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorClient addMonitorTree fallback");
        return result;
    }

    @Override
    public Result treeMaintaince(String classId, String rootId, String edmcNameEn) {
        // TODO Auto-generated method stub
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorClient treeMaintaince fallback");
        return result;
    }
    
    @Override
    public Result nodeDetail(String key,String lvlCode) {
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
    public Result deleteNodeResource(String key,String lvlCode, String resourceId) {
        // TODO Auto-generated method stub
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorClient deleteNodeResource fallback");
        return result;
    }

    @Override
    public Result addResource(String key,String lvlCode, String resourceId,String resourceText) {
        // TODO Auto-generated method stub
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorClient addResource fallback");
        return result;
    }

    @Override
    public Result addNode(String key,String lvlCode, int type) {
        // TODO Auto-generated method stub
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorClient addNode fallback");
        return result;
    }

    @Override
    public Result deleteNode(String key,String lvlCode,int type) {
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

	@Override
	public Result formula(NodeTo node) {
		// TODO Auto-generated method stub
		Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("MonitorClient formula fallback");
        return result;
	}
}
