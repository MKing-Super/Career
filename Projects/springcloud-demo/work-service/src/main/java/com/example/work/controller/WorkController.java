package com.example.work.controller;

import cn.hutool.core.net.NetUtil;
import com.example.work.model.Work;
import com.example.work.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
        String localhostStr = NetUtil.getLocalhostStr();
        String res = param + " -> " + "work-service["+localhostStr+"]>>>>>>>>>>";
        return res;
    }

    @PostMapping("/postTest")
    public String postTest(@RequestBody String param){
        return "It is work-service~\nparam=" + param;
    }
}