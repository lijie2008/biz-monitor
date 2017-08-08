/**
 * Project Name:service-center-provider
 * File Name:MonitorTreeOrderServiceImpl.java
 * Package Name:com.huntkey.rx.sceo.serviceCenter.provider.business.service.impl
 * Date:2017年8月8日上午11:06:46
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.constant.Constant;
import com.huntkey.rx.sceo.monitor.commom.constant.PersistanceConstant;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.commom.model.ConditionParam;
import com.huntkey.rx.sceo.monitor.commom.model.FullInputArgument;
import com.huntkey.rx.sceo.monitor.commom.model.PagenationParam;
import com.huntkey.rx.sceo.monitor.commom.model.SortParam;
import com.huntkey.rx.sceo.monitor.provider.controller.client.HbaseClient;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeOrderService;

/**
 * ClassName:MonitorTreeOrderServiceImpl 临时单Impl
 * Date:     2017年8月8日 上午11:06:46
 * @author   lijie
 * @version  
 * @see 	 
 */
@Component
public class MonitorTreeOrderServiceImpl implements MonitorTreeOrderService{
    
    @Autowired
    private HbaseClient client;
    
    @Override
    public JSONObject queryNode(String nodeId) {
        ConditionParam cnd = new ConditionParam();
        cnd.setAttr(Constant.ID);
        cnd.setOperator("=");
        cnd.setValue(nodeId);
        List<ConditionParam> cnds = new ArrayList<ConditionParam>();
        cnds.add(cnd);
        FullInputArgument input = new FullInputArgument(queryParam("monitortreeorder.mtor005", cnds, null, null));
        
        Result result = client.find(input.getJson().toString());
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        return (JSONObject)result.getData();
    }

    @Override
    public JSONObject queryResource(String nodeId) {
        ConditionParam cnd = new ConditionParam();
        cnd.setAttr(Constant.PID);
        cnd.setOperator("=");
        cnd.setValue(nodeId);
        List<ConditionParam> cnds = new ArrayList<ConditionParam>();
        cnds.add(cnd);
        FullInputArgument input = new FullInputArgument(queryParam("monitortreeorder.mtor005.mtor019", cnds, null, null));
        
        Result result = client.find(input.getJson().toString());
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        return (JSONObject)result.getData();
        
    }
    
    @Override
    public JSONObject queryOrder(String orderId){
        ConditionParam cnd = new ConditionParam();
        cnd.setAttr(Constant.ID);
        cnd.setOperator("=");
        cnd.setValue(orderId);
        List<ConditionParam> cnds = new ArrayList<ConditionParam>();
        cnds.add(cnd);
        FullInputArgument input = new FullInputArgument(queryParam("monitortreeorder", cnds, null, null));
        
        Result result = client.find(input.getJson().toString());
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        return (JSONObject)result.getData();
        
    }
    
    @Override
    public JSONObject queryTreeNode(String orderId, String startDate, String endDate) {
        
        Result result = client.queryTreeNodeResource(orderId, startDate, endDate);
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        return (JSONObject)result.getData();
        
    }

    /**
     * 
     * queryParam: 查询参数
     * @author lijie
     * @param edmName edm名称
     * @param cnds 查询条件
     * @param pagenation 分页信息
     * @param sort 排序信息
     * @return
     */
    public String queryParam(String edmName, List<ConditionParam> cnds, 
                             PagenationParam pagenation, List<SortParam> sort){
        JSONObject json = new JSONObject();
        JSONObject search = new JSONObject();
        search.put(PersistanceConstant.CONDITIONS, cnds);
        search.put(PersistanceConstant.PAGENATION, pagenation);
        search.put("orderBy", sort);
        json.put("search", search);
        json.put(PersistanceConstant.EDMNAME, edmName);
        return json.toJSONString();
    }
    
}

