package com.example.home.admin.controller;

import com.example.home.admin.feign.home.HomeServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/admin/redis")
public class RedisController {

    @Autowired
    private HomeServiceFeign homeServiceFeign;

    @GetMapping
    public String redisPage() {
        return "redis-admin";
    }

    @GetMapping("/keys")
    @ResponseBody
    public Map<String, Object> getRedisKeys(@RequestParam(required = false, defaultValue = "*") String pattern) {
        return homeServiceFeign.getRedisKeys(pattern);
    }

    @GetMapping("/value/{key}")
    @ResponseBody
    public Map<String, Object> getRedisValue(@PathVariable String key) {
        return homeServiceFeign.getRedisValue(key);
    }

    @DeleteMapping("/key/{key}")
    @ResponseBody
    public Map<String, Object> deleteKey(@PathVariable String key) {
        return homeServiceFeign.deleteRedisKey(key);
    }

    @PostMapping("/value")
    @ResponseBody
    public Map<String, Object> setValue(@RequestParam String key, @RequestParam String value) {
        return homeServiceFeign.setRedisValue(key, value);
    }
}