package com.huntkey.rx.sceo.monitor.provider.service;

import redis.clients.jedis.Jedis;

/**
 * 
 * ClassName: RedisService redis操作
 * Date: 2017年8月3日 上午9:44:33
 * @author lijie
 * @version
 */
public interface RedisService {
    Jedis getResource();

    void returnResource(Jedis jedis);

    void set(String key, String value);

    void set(byte[] key, byte[] value);

    String get(String key);

    byte[] get(byte[] key);

    Long delete(String key);

    Long delete(byte[] key);
}
