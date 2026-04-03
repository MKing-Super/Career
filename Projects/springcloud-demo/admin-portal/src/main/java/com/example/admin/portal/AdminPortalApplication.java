package com.example.admin.portal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AdminPortalApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminPortalApplication.class, args);
        log.info("eureka-server has started >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }
}
