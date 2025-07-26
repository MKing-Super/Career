package com.example.home.admin.controller;

import com.example.home.admin.feign.HomeServiceFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class IndexController {
    @Autowired
    private HomeServiceFeign homeServiceFeign;

    @GetMapping
    @ResponseBody
    public String index(){
        return homeServiceFeign.getTest("I am home-admin~");
    }
}
