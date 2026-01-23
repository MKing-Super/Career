package com.example.home.admin.controller;

import com.example.home.utils.websocket.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/netty")
public class NettyController {
    private static final Logger log = LoggerFactory.getLogger(NettyController.class);

    // netty 发送信息测试页面
    @GetMapping("/test")
    public String test(){
        log.info("enter sendMsgTest page~");
        return "netty/ClientTest";
    }

    // 发送信息测试页面 ajax调用接口发送到WebSocket
    @GetMapping("/receiveMsg")
    @ResponseBody
    public String receiveMsg(String msg){
        NettyClient.getInstance().writeAndFlush(msg);
        return "success~";
    }

    // Websocket页面测试
    @GetMapping("/webSocketTest")
    public String webSocketTest(){
        log.info("enter webSocketTest page~");
        return "netty/WebsocketTest";
    }

}
