package com.example.home.admin.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.example.home.utils.IpUtil;
import com.example.home.utils.websocket.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.NetworkInterface;
import java.util.List;

@Controller
@RequestMapping("/netty")
public class NettyController {
    private static final Logger log = LoggerFactory.getLogger(NettyController.class);

    // 指定 本地服务器网卡名称
    @Value("${netty.websocket.network-card-name:eth5}")
    private String NETWORK_CARD_NAME;


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
    public String webSocketTest(Model model){
        log.info("enter webSocketTest page~");
        String lanIp = "127.0.0.1";
        List<String> lanIpArr = IpUtil.getLanIp(NETWORK_CARD_NAME);
        if (CollectionUtil.isNotEmpty(lanIpArr)){
            lanIp = lanIpArr.get(0);
        }
        log.info("本地服务器内网IP -> {}", JSON.toJSONString(lanIpArr));
        model.addAttribute("lanIp",lanIp);
        model.addAttribute("port",8860);
        return "netty/WebsocketTest";
    }



}
