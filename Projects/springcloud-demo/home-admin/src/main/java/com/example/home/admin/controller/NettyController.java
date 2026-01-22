package com.example.home.admin.controller;

import com.example.home.utils.websocket.client.NettyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/netty")
public class NettyController {

    @GetMapping("/test")
    public String test(){
        return "/netty/test";
    }

    @GetMapping("/receiveMsg")
    @ResponseBody
    public String receiveMsg(String msg){
        NettyClient.getInstance().writeAndFlush(msg);
        return "success~";
    }

}
