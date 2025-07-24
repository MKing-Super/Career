package com.example.life;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient  // 启用服务注册与发现
public class LifeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LifeServiceApplication.class, args);
        log.info("life-service has started >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }
}
