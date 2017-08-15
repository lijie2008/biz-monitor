package com.huntkey.rx.sceo.monitor.provider.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huntkey.rx.sceo.monitor.commom.model.RedisLock;
import com.huntkey.rx.sceo.monitor.commom.utils.SequenceNumberFormatUtil;
import com.huntkey.rx.sceo.monitor.provider.service.OrderNumberService;

/**
 * Created by xuyf on 2017/6/30 0030.
 */
@Service
@Transactional(readOnly = true)
public class OrderNumberServiceImpl implements OrderNumberService {

    private static Logger log = LoggerFactory.getLogger(OrderNumberServiceImpl.class);

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public String generateOrderNumber(String orderType) {
        String redisOrderKey = "order_type:"+orderType;
        RedisLock lock = new RedisLock(stringRedisTemplate, redisOrderKey, 10000, 20000);
        String orderNumber = null;
        try {
            if (lock.lock()){
                int seqNum = 1;
                if (stringRedisTemplate.hasKey(redisOrderKey)){
                    seqNum = Integer.parseInt(stringRedisTemplate.opsForValue().get(redisOrderKey)) + 1;
                }
                stringRedisTemplate.opsForValue().set(redisOrderKey, seqNum + "");
                orderNumber = SequenceNumberFormatUtil.prefixSeqNum(orderType, seqNum, 10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lock.isTimeout()){
                lock.unlock();
            }
        }
        return orderNumber;
    }
}
