package com.example.home.admin.feign.work;

import com.example.work.model.Work;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "work-service",path = "/work")
public interface WorkServiceFeign {
    @GetMapping("/work")
    List<Work> getAllWorks();

    @PostMapping("/work")
    void addWork(@RequestBody Work work);

    @PostMapping("/getTest")
    String getTest(@RequestParam("param") String param);

    @PostMapping("/postTest")
    String postTest(@RequestBody String param);

}