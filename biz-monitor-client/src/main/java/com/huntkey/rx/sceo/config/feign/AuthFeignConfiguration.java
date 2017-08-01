package com.huntkey.rx.sceo.config.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.huntkey.rx.sceo.monitor.client.config.AuthErrorDecoder;



/**
 * Created by zhaomj on 2017/4/25.
 */
@Configuration
public class AuthFeignConfiguration {

    @Bean
    public AuthErrorDecoder errorDecoder() {
        return new AuthErrorDecoder();
    }
}
