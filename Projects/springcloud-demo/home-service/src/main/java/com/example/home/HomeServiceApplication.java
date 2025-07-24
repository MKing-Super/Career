package com.example.home;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient  // 启用服务注册与发现
public class HomeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(HomeServiceApplication.class, args);
        log.info("home-service has started >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }
}