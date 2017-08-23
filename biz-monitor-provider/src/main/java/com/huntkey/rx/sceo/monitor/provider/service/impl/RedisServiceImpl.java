package com.huntkey.rx.sceo.monitor.provider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.huntkey.rx.sceo.monitor.provider.service.RedisService;

/**
 * 
 * ClassName: RedisServiceImpl redis的操作
 * Date: 2017年8月3日 上午9:46:20
 * @author lijie
 * @version
 */
@Service
public class RedisServiceImpl implements RedisService{

    @Autowired
    private RedisTemplate<Object, Object> redis;
    
    @Override
    public Long size(String key) {
        return redis.opsForList().size(key);
    }

    @Override
    public Boolean isEmpity(String key) {
        return size(key) == 0 ? true : false;
    }

    @Override
    public void lPush(String key, Object value) {
        redis.opsForList().leftPush(key, value);
    }

    @Override
    public Object lPop(String key) {
        return redis.opsForList().leftPop(key);
    }

    @Override
    public void delete(String key) {
        redis.delete(key);
    }
    
    @Override
    public void set(String key, long index, Object value){
        redis.opsForList().set(key, index, value);
    }
    
    @Override
    public Object index(String key,long index){
        return redis.opsForList().index(key, index);
    }
    
    

}
