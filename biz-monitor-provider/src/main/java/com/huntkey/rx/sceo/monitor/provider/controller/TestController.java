/**
 * Project Name:biz-monitor-provider
 * File Name:TestController.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.controller
 * Date:2017年8月16日上午11:16:55
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.enums.OperateType;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.provider.config.Revoked;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorService;
import com.huntkey.rx.sceo.monitor.provider.service.RedisService;

/**
 * ClassName:TestController
 * Function: TODO ADD FUNCTION
 * Reason:	 TODO ADD REASON
 * Date:     2017年8月16日 上午11:16:55
 * @author   lijie
 * @version  
 * @see 	 
 */
@RestController
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    private MonitorService mService;
    @Autowired
    private RedisService redis;
    
    @Revoked(type=OperateType.INITIALIZE)
    @RequestMapping(value="/init", method = RequestMethod.GET)
    public Result init(@RequestParam(required=true) String orderId){
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        JSONObject obj = new JSONObject();
        obj.put("id", orderId);
        result.setData(obj);
        return result;
    }
    
    
    @Revoked(type=OperateType.NODE)
    @RequestMapping(value="/add", method = RequestMethod.GET)
    public Result add(@RequestParam(required=true) @Revoked String nodeId){
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        mService.addNode(nodeId, 0);
        return result;
    }
    
    @Revoked(type=OperateType.DETAIL)
    @RequestMapping(value="/update", method = RequestMethod.POST)
    public Result update(@RequestBody @Revoked(key="id") NodeTo node){
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        mService.saveNodeDetail(node);
        return result;
    }
    
    
    @Revoked(type=OperateType.DETAIL)
    @RequestMapping(value="/resource", method = RequestMethod.GET)
    public Result resource(@RequestParam(required=true) @Revoked String nodeId, List<String> resources){
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        mService.addResource(nodeId, resources.toArray(new String[resources.size()]));
        return result;
    }
    
    @RequestMapping(value="/data", method = RequestMethod.GET)
    public Result data(@RequestParam(required=true)String orderId){
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        Long size = redis.size(orderId);
        List<Object> obj = new ArrayList<Object>();
        for(int i = 0;i< size; i++){
            obj.add(redis.index(orderId, i));
        }
        result.setData(obj);    
        return result;
    }
    
}

