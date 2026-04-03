package com.example.admin.portal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SessionService {

    private static final String SESSION_PREFIX = "mk:session:";
    private static final long SESSION_TIMEOUT = 2 * 60 * 60;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String createSession(String username, String role, String userAgent, String ip) {
        String sessionId = UUID.randomUUID().toString();
        String key = SESSION_PREFIX + sessionId;
        String value = username + ":" + role + ":" + (userAgent != null ? userAgent : "") + ":" + (ip != null ? ip : "");
        redisTemplate.opsForValue().set(key, value, SESSION_TIMEOUT, TimeUnit.SECONDS);
        return sessionId;
    }

    public String getUsername(String sessionId, String userAgent, String ip) {
        String key = SESSION_PREFIX + sessionId;
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            String[] parts = value.split(":");
            if (parts.length >= 2) {
                redisTemplate.expire(key, SESSION_TIMEOUT, TimeUnit.SECONDS);
                return parts[0];
            }
        }
        return null;
    }

    public void invalidateSession(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        redisTemplate.delete(key);
    }

    public String getRole(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            String[] parts = value.split(":");
            if (parts.length >= 2) {
                return parts[1];
            }
        }
        return null;
    }
}
