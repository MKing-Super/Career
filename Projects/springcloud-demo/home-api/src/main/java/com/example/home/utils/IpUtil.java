package com.example.home.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpUtil {
    private static final Logger log = LoggerFactory.getLogger(IpUtil.class);

    // 网卡名称查ip
    public static List<String> getLanIp(String name){
        List<String> ipList = new ArrayList<>();
        try {
            log.info("IpUtil getLanIp param -> {}",name);
            NetworkInterface eth5 = NetworkInterface.getByName(name);
            // 基础IPv4正则
            Pattern ipv4Pattern = Pattern.compile(
                    "\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b"
            );

            // 获取所有IP地址
            Enumeration<InetAddress> addresses = eth5.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                String ip = address.getHostAddress();

                // 使用正则验证
                Matcher matcher = ipv4Pattern.matcher(ip);
                if (matcher.find()) {
                    ipList.add(ip);
                }
            }

        }catch (Exception e){
            log.error(e.getMessage(),e);
        }finally {
            return ipList;
        }
    }

}
