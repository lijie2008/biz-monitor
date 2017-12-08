package com.huntkey.rx.sceo.monitor.provider.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Retryer;

/**
 * Created by xuyf on 2017/7/27.
 */
@Configuration
public class FeignConfiguration {

    @Bean
    Retryer retryer(){
        return Retryer.NEVER_RETRY;
    }
}
