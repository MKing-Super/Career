package com.example.work.admin.feign;

import com.example.work.model.Work;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@FeignClient(name = "work-service")
public interface WorkServiceFeign {
    @GetMapping("/work")
    List<Work> getAllWorks();

    @PostMapping("/work")
    void addWork(@RequestBody Work work);
}