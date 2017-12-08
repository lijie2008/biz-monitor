package com.huntkey.rx.sceo.monitor.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Retryer;

/**
 * Created by zhaomj on 2017/4/25.
 */
@Configuration
public class FeignConfiguration {

    @Bean
    Retryer retryer(){
        return Retryer.NEVER_RETRY;
    }
}
