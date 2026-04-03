package com.example.admin.portal.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "home-service")
public interface UserFeign {

    @GetMapping("/user/login")
    Map<String, Object> login(@RequestParam("username") String username, @RequestParam("password") String password);
}
