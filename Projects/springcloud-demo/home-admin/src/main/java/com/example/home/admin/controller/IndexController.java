package com.example.home.admin.controller;

import com.example.home.admin.feign.home.HomeServiceFeign;
import com.example.home.admin.feign.work.WorkServiceFeign;
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
    @Autowired
    private WorkServiceFeign workServiceFeign;

    @GetMapping
    @ResponseBody
    public String index(){
        String res = "";
        try {
            String h = homeServiceFeign.getTest("I am home-admin~");
            res += h;
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        try {
            String w = workServiceFeign.getTest("I am home-admin~");
            res += w;
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }

        return res.length() == 0 ? "无法调用 home、work 服务" : res;
    }
}
