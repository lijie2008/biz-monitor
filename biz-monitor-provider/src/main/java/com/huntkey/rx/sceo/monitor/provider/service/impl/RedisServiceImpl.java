package com.huntkey.rx.sceo.monitor.provider.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huntkey.rx.sceo.monitor.provider.service.RedisService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 
 * ClassName: RedisServiceImpl redis的操作
 * Date: 2017年8月3日 上午9:46:20
 * @author lijie
 * @version
 */
@Service
public class RedisServiceImpl implements RedisService{

    private static Logger log = LoggerFactory.getLogger(RedisServiceImpl.class);

    @Autowired
    private JedisPool jedisPool;

    @Override
    public Jedis getResource() {
        return jedisPool.getResource();
    }

    @Override
    public void returnResource(Jedis jedis) {
        if(null != jedis){
            jedis.close();
        }
    }

    @Override
    public void set(String key, String value) {
        Jedis jedis=null;
        try{
            jedis = getResource();
            jedis.set(key, value);
            log.info("Redis set success - " + key + ", value:" + value);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis set error: "+ e.getMessage() +" - " + key + ", value:" + value);
        }finally{
            returnResource(jedis);
        }
    }

    @Override
    public void set(byte[] keyByte, byte[] valueByte) {
        Jedis jedis=null;
        try{
            jedis = getResource();
            jedis.set(keyByte, valueByte);
            log.info("Redis set[] success - " + keyByte + ", value:" + valueByte);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis set[] error: "+ e.getMessage() +" - " + keyByte + ", value:" + valueByte);
        }finally{
            returnResource(jedis);
        }
    }

    @Override
    public String get(String key) {
        String result = null;
        Jedis jedis=null;
        try{
            jedis = getResource();
            result = jedis.get(key);
            log.info("Redis get success - " + key + ", value:" + result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis get error: "+ e.getMessage() +" - " + key + ", value:" + result);
        }finally{
            returnResource(jedis);
        }
        return result;
    }

    @Override
    public byte[] get(byte[] key) {
        byte[] result = null;
        Jedis jedis=null;
        try{
            jedis = getResource();
            result = jedis.get(key);
            log.info("Redis get[] success - " + key + ", value:" + result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis get[] error: "+ e.getMessage() +" - " + key + ", value:" + result);
        }finally{
            returnResource(jedis);
        }
        return result;
    }

    @Override
    public Long delete(String key) {
        Long result = null;
        Jedis jedis=null;
        try{
            jedis = getResource();
            result = jedis.del(key);
            log.info("Redis delete success - " + key + ", value:" + result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis delete error: "+ e.getMessage() +" - " + key + ", value:" + result);
        }finally{
            returnResource(jedis);
        }
        return result;
    }

    @Override
    public Long delete(byte[] key) {
        Long result = null;
        Jedis jedis=null;
        try{
            jedis = getResource();
            result = jedis.del(key);
            log.info("Redis delete[] success - " + key + ", value:" + result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis delete[] error: "+ e.getMessage() +" - " + key + ", value:" + result);
        }finally{
            returnResource(jedis);
        }
        return result;
    }
    
}
