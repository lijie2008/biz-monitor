/**
 * Project Name:biz-monitor-provider
 * File Name:EdmPropertyGroupDataMapper.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.orm
 * Date:2017年8月9日下午6:01:10
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.orm.dao;

import java.util.List;
import java.util.Map;

/**
 * ClassName:EdmPropertyGroupDataMapper
 * Function: TODO ADD FUNCTION
 * Date:     2017年8月9日 下午6:01:10
 * @author   caozhenx
 * @version  
 * @see 	 
 */
public interface EdmPropertyGroupDataMapper {

    /**
     * 查询
     * @param map
     * @return
     */
    List<Map<String, Object>> select(Map<String, Object> map);
}

