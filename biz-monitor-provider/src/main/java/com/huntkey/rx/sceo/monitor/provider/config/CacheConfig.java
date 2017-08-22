package com.huntkey.rx.sceo.monitor.provider.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
/**
 * Created by lijie on 2017/8/3 0017.
 */
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {
    private static Logger log = LoggerFactory.getLogger(CacheConfig.class);
    @Value("${redis.port}")
    private int port;
    @Value("${redis.host}")
    private String host;
    
    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        log.info("JedisCluster创建！！");
        log.info("redis地址：" + host + ":" + port);
        
        JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
        redisConnectionFactory.setHostName(this.host);
        redisConnectionFactory.setPort(this.port);
        
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(5);
        jedisPoolConfig.setMaxIdle(2);
        jedisPoolConfig.setMaxWaitMillis(60000);
        
        redisConnectionFactory.setPoolConfig(jedisPoolConfig);
        redisConnectionFactory.afterPropertiesSet();
        return redisConnectionFactory;
    }
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
        redisTemplate.setConnectionFactory(cf);
        return redisTemplate;
    }
    @Bean
    public RedisTemplate<String, Object> expressTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, Object> expressTemplate = new RedisTemplate<String, Object>();
        expressTemplate.setConnectionFactory(cf);
        return expressTemplate;
    }
    @SuppressWarnings("rawtypes")
    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        cacheManager.setDefaultExpiration(300);
        return cacheManager;
    }
    @Bean
    public JedisPool redisPoolFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(8);
        jedisPoolConfig.setMaxWaitMillis(-1);
        log.info("------------"+host+":"+port+"------------");
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port);
        return jedisPool;
    }
}