package com.huntkey.rx.sceo.monitor.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

import com.huntkey.rx.sceo.method.register.plugin.annotation.EnableDriverMethod;
import com.huntkey.rx.sceo.method.register.plugin.annotation.EnableMethodRegisterScanner;
/**
 * Created by zhaomj on 2017/4/24.
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.huntkey.rx")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.huntkey.rx.sceo.monitor.provider.controller.client")
@EnableMethodRegisterScanner(startApplicationClass = MonitorProviderApplication.class,
        edmServiceName = "${edmServiceName:modeler-provider}",
        serviceApplicationName = "${spring.application.name}")
@EnableDriverMethod
public class MonitorProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(MonitorProviderApplication.class,args);
    }
}
