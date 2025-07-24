package com.example.life.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class LifeAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(LifeAdminApplication.class, args);
        log.info("life-admin has started >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }
}
