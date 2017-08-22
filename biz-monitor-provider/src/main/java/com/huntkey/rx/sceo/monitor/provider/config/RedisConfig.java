/**
 * Project Name:biz-monitor-provider
 * File Name:RedisConfig.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.config
 * Date:2017年8月21日下午1:34:21
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import redis.clients.jedis.JedisPoolConfig;

/**
 * ClassName:RedisConfig
 * Function: TODO ADD FUNCTION
 * Reason:	 TODO ADD REASON
 * Date:     2017年8月21日 下午1:34:21
 * @author   lijie
 * @version  
 * @see 	 
 */
public class RedisConfig extends CachingConfigurerSupport{
    
    private final static int MAX_TOTAL = 20;

    private final static int MAX_IDLE = 20;

    private final static int MIN_IDLE = 5;

    private final static long MAX_WAIT_MILLIS = 6000;
    
    @Value("${redis.cluster.nodes}")
    private List<String> node;
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory cf) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(cf);
        return redisTemplate;
    }
    
    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisClusterConfiguration config = new RedisClusterConfiguration(node);
        JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory(config,getJedisPoolConfig());
        redisConnectionFactory.afterPropertiesSet();
        return redisConnectionFactory;
    }
    
    private JedisPoolConfig getJedisPoolConfig(){
        JedisPoolConfig config = new JedisPoolConfig();
        config = new JedisPoolConfig();
        config.setMaxTotal(MAX_TOTAL);
        config.setMaxIdle(MAX_IDLE);
        config.setMinIdle(MIN_IDLE);
        config.setMaxWaitMillis(MAX_WAIT_MILLIS);
        config.setTestOnBorrow(true);
        return config;
    }
    
    @Bean
    public CacheManager cacheManager(RedisTemplate<?, ?> redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        cacheManager.setDefaultExpiration(300);
        return cacheManager;
    }
    
}


