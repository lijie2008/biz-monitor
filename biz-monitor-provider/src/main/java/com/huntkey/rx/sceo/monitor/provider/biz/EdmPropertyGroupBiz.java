/**
 * Project Name:biz-monitor-provider
 * File Name:EdmPropertyGroupBiz.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.biz
 * Date:2017年8月10日上午8:49:45
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.biz;

import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;

/**
 * ClassName:EdmPropertyGroupBiz
 * Function: edm 类 属性分组 biz方法定义
 * Date:     2017年8月10日 上午8:49:45
 * @author   caozhenx
 * @version  
 * @see 	 
 */
public interface EdmPropertyGroupBiz {

    /**
     * getMonitorIds:根据传入查询条件  查询同分组下 所有的monitor类的相关信息
     * 
     * {
     * edpg_edmc_id:xx
     * edpg_edmp_id:xx
     * 
     * }
     * @author caozhenx
     * @param jsonObject
     * @return
     */
    public Result getMonitorInfo(JSONObject jsonObject);

}

