package com.huntkey.rx.sceo.monitor.provider.service;

import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;

/**
 * 
 * ClassName: RedisService redis操作
 * Date: 2017年8月3日 上午9:44:33
 * @author lijie
 * @version
 */
public interface RedisService<T extends NodeTo> {
    
    Long size(String key);
    
    Boolean isEmpity(String key);
    
    void lPush(String key, T value);
    
    Object lPop(String key);
    
    void delete(String key);
    
    void set(String key, long index, T value);
    
    Object index(String key,long index);
}
