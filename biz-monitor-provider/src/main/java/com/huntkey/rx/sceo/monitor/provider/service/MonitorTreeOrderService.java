/**
 * Project Name:service-center-provider
 * File Name:MonitorTreeOrderService.java
 * Package Name:com.huntkey.rx.sceo.serviceCenter.provider.service
 * Date:2017年8月8日上午10:22:56
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.edm.entity.ResourceEntity;
import com.huntkey.rx.sceo.monitor.commom.model.RevokedTo;

/**
 * ClassName:MonitorTreeOrderService 临时单服务
 * Date:     2017年8月8日 上午10:22:56
 * @author   lijie
 * @version  
 * @see 	 
 */
public interface MonitorTreeOrderService {
    
    JSONObject queryNotUsingResource (String key,String lvlCode,int currentPage, int pageSize) throws Exception;
    
    boolean checkDate(String key, String lvlCode,String startDate, String endDate);
    
    List<?> queryAvailableResource(String key) throws Exception;
    
    String addOtherNode(String key) throws Exception;

    String save(String key) throws Exception;
    
    RevokedTo revoke(String key);

    String store(String orderId) throws Exception;
}

