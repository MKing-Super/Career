package com.example.home.admin.feign.home;

import com.example.home.model.Home;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "home-service",path = "/home")
public interface HomeServiceFeign {
    @GetMapping("/getAllWorks")
    List<Home> getAllWorks();

    @PostMapping("/addWork")
    void addWork(@RequestBody Home work);

    @GetMapping("/getTest")
    String getTest(@RequestParam("param") String param);

}