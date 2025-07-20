package com.example.home.admin.feign;

import com.example.home.model.Home;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "work-service")
public interface HomeServiceFeign {
    @GetMapping("/work")
    List<Home> getAllWorks();

    @PostMapping("/work")
    void addWork(@RequestBody Home work);
}