package com.example.home.admin.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.json.JSONUtil;
import com.example.home.admin.feign.home.HomeServiceFeign;
import com.example.home.admin.feign.work.WorkServiceFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@Slf4j
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private HomeServiceFeign homeServiceFeign;
    @Autowired
    private WorkServiceFeign workServiceFeign;

    @GetMapping("/index")
    @ResponseBody
    public String index(){
        try {
            log.info("TestController index start >>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            String localhostStr = NetUtil.getLocalhostStr();
            ArrayList<String> list = new ArrayList<>();
            try {
                String h = homeServiceFeign.getTest("home-admin["+localhostStr+"]");
                list.add(h);
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
            try {
                String w = workServiceFeign.getTest("home-admin["+localhostStr+"]");
                list.add(w);
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
            log.info("TestController index end <<<<<<<<<<<<<<<<<<<<<<<<<<");
            return CollectionUtil.isEmpty(list) ? "无法调用 home、work 服务" : JSONUtil.toJsonStr(list);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return "test 后台异常！";
        }

    }
}
