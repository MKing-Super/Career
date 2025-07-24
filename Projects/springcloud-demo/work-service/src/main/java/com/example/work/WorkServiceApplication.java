package com.example.work;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

//@Slf4j
@SpringBootApplication
@EnableDiscoveryClient  // 启用服务注册与发现
public class WorkServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkServiceApplication.class, args);
//        log.info("work-service has started >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }
}