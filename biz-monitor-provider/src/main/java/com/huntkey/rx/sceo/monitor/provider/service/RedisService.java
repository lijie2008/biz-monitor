package com.huntkey.rx.sceo.monitor.provider.service;

/**
 * 
 * ClassName: RedisService redis操作
 * Date: 2017年8月3日 上午9:44:33
 * @author lijie
 * @version
 */
public interface RedisService {
    
    Long size(String key);
    
    Boolean isEmpity(String key);
    
    void lPush(String key, Object value);
    
    Object lPop(String key);
    
    void delete(String key);
    
    void set(String key, long index, Object value);
    
    Object index(String key,long index);
}
