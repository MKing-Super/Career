package com.example.home.controller;

import com.example.home.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private HomeService homeService;

    @GetMapping("/keys")
    public Map<String, Object> getKeys(@RequestParam(required = false, defaultValue = "*") String pattern) {
        Set<String> keys = homeService.getAllKeys(pattern);
        Map<String, Object> result = new HashMap<>();
        result.put("keys", keys);
        result.put("count", keys.size());
        return result;
    }

    @GetMapping("/value/{key}")
    public Map<String, Object> getValue(@PathVariable String key) {
        String value = homeService.getValue(key);
        Long ttl = homeService.getKeyTtl(key);
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("value", value);
        result.put("ttl", ttl);
        result.put("ttlText", formatTtl(ttl));
        return result;
    }

    @DeleteMapping("/key/{key}")
    public Map<String, Object> deleteKey(@PathVariable String key) {
        Boolean deleted = homeService.deleteKey(key);
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("deleted", deleted);
        return result;
    }

    @DeleteMapping("/keys")
    public Map<String, Object> deleteKeysByPattern(@RequestParam String pattern) {
        Long deleted = homeService.deleteKeysByPattern(pattern);
        Map<String, Object> result = new HashMap<>();
        result.put("pattern", pattern);
        result.put("deleted", deleted);
        return result;
    }

    @PostMapping("/value")
    public Map<String, Object> setValue(@RequestParam String key, @RequestParam String value) {
        homeService.setValue(key, value);
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("value", value);
        result.put("success", true);
        return result;
    }

    private String formatTtl(Long ttl) {
        if (ttl == null || ttl < 0) {
            return "No expiration";
        } else if (ttl == 0) {
            return "Expired";
        }
        long days = ttl / 86400;
        long hours = (ttl % 86400) / 3600;
        long minutes = (ttl % 3600) / 60;
        long seconds = ttl % 60;
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d-");
        if (hours > 0) sb.append(hours).append("h-");
        if (minutes > 0) sb.append(minutes).append("m-");
        sb.append(seconds).append("s");
        return sb.toString();
    }
}
