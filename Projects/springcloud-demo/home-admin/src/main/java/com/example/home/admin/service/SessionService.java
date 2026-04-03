package com.example.home.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SessionService {

    private static final String SESSION_PREFIX = "mk:session:";
    private static final long SESSION_TIMEOUT = 2 * 60 * 60;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String getUsername(String sessionId, String userAgent, String ip) {
        if (sessionId == null || sessionId.isEmpty()) return null;
        String key = SESSION_PREFIX + sessionId;
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            String[] parts = value.split(":");
            if (parts.length < 4) {
                return null;
            }
            String storedUserAgent = parts[2];
            String storedIp = parts[3];
            boolean userAgentMatch = (userAgent == null && storedUserAgent.isEmpty()) || (userAgent != null && userAgent.equals(storedUserAgent));
            boolean ipMatch = (ip == null && storedIp.isEmpty()) || (ip != null && ip.equals(storedIp));
            if (userAgentMatch && ipMatch) {
                redisTemplate.expire(key, SESSION_TIMEOUT, TimeUnit.SECONDS);
                return parts[0];
            }
            redisTemplate.delete(key);
        }
        return null;
    }

    public String getRole(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) return null;
        String key = SESSION_PREFIX + sessionId;
        String value = redisTemplate.opsForValue().get(key);
        if (value != null && value.contains(":")) {
            return value.split(":")[1];
        }
        return null;
    }
}
