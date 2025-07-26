package com.example.work.admin.controller;

import com.example.work.admin.feign.WorkServiceFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Slf4j
@Controller
public class IndexController {

    @Autowired
    private WorkServiceFeign workServiceFeign;

    @GetMapping
    @ResponseBody
    public String index(){
        return workServiceFeign.getTest("I am work-admin~");
    }

}
