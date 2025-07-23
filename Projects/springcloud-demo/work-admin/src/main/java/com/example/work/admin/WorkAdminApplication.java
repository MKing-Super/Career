package com.example.work.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class WorkAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkAdminApplication.class, args);
        log.info("work-admin has started >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }
}