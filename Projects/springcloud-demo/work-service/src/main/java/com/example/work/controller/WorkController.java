package com.example.work.controller;

import com.example.work.model.Work;
import com.example.work.service.WorkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/work")
public class WorkController {
    @Autowired
    private WorkService workService;

    @GetMapping
    public List<Work> getAllWorks() {
        return workService.getAllWorks();
    }

    @PostMapping
    public void addWork(@RequestBody Work work) {
        workService.addWork(work);
    }

    @PostMapping("/getTest")
    public String getTest(@RequestParam("param") String param){
        return "It is work-service~\nparam=" + param;
    }

    @PostMapping("/postTest")
    public String postTest(@RequestBody String param){
        return "It is work-service~\nparam=" + param;
    }
}