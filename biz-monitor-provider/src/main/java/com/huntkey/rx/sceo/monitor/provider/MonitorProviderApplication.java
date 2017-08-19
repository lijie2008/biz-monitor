package com.huntkey.rx.sceo.monitor.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
/**
 * Created by zhaomj on 2017/4/24.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.huntkey.rx.sceo.monitor.provider.controller.client")
public class MonitorProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(MonitorProviderApplication.class,args);
    }
}
