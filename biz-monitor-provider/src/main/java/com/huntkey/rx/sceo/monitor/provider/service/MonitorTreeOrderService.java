/**
 * Project Name:service-center-provider
 * File Name:MonitorTreeOrderService.java
 * Package Name:com.huntkey.rx.sceo.serviceCenter.provider.service
 * Date:2017年8月8日上午10:22:56
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.sceo.monitor.commom.model.RevokedTo;

/**
 * ClassName:MonitorTreeOrderService 临时单服务
 * Date:     2017年8月8日 上午10:22:56
 * @author   lijie
 * @version  
 * @see 	 
 */
public interface MonitorTreeOrderService {
    
    JSONObject queryNotUsingResource(String key,String lvlCode,int currentPage, int pageSize);
    
    boolean checkDate(String key, String lvlCode,String startDate, String endDate);
    
    JSONArray queryAvailableResource(String key);
    
    String addOtherNode(String key);

    String save(String key);
    
    RevokedTo revoke(String key);

    String store(String orderId) throws Exception;
}

