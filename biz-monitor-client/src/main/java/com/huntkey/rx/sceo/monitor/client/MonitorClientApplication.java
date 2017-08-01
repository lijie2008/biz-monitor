package com.huntkey.rx.sceo.monitor.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by zhaomj on 2017/4/24.
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages ="com.huntkey.rx.sceo.monitor.client" )
@EnableFeignClients
@EnableDiscoveryClient
@EnableSwagger2
@EnableHystrix
public class MonitorClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(MonitorClientApplication.class,args);
    }
}
