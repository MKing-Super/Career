package com.example.home.admin.feign.home;

import com.example.home.model.Home;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "home-service")
public interface HomeServiceFeign {
    @GetMapping("/home/getAllWorks")
    List<Home> getAllWorks();

    @PostMapping("/home/addWork")
    void addWork(@RequestBody Home work);

    @GetMapping("/home/getTest")
    String getTest(@RequestParam("param") String param);

    @GetMapping("/redis/keys")
    Map<String, Object> getRedisKeys(@RequestParam(required = false, defaultValue = "*") String pattern);

    @GetMapping("/redis/value/{key}")
    Map<String, Object> getRedisValue(@PathVariable("key") String key);

    @DeleteMapping("/redis/key/{key}")
    Map<String, Object> deleteRedisKey(@PathVariable("key") String key);

    @DeleteMapping("/redis/keys")
    Map<String, Object> deleteRedisKeysByPattern(@RequestParam("pattern") String pattern);

    @PostMapping("/redis/value")
    Map<String, Object> setRedisValue(@RequestParam("key") String key, @RequestParam("value") String value);
}