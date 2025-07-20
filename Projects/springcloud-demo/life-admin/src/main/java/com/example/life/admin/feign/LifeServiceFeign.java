package com.example.life.admin.feign;

import com.example.life.model.Life;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "life-service")
public interface LifeServiceFeign {

    @GetMapping("/work")
    List<Life> getAllWorks();

    @PostMapping("/work")
    void addWork(@RequestBody Life work);
}
