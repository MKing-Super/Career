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

    public String createSession(String username, String role) {
        String sessionId = UUID.randomUUID().toString();
        String key = SESSION_PREFIX + sessionId;
        redisTemplate.opsForValue().set(key, username + ":" + role, SESSION_TIMEOUT, TimeUnit.SECONDS);
        return sessionId;
    }

    public String getUsername(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            redisTemplate.expire(key, SESSION_TIMEOUT, TimeUnit.SECONDS);
            return value.split(":")[0];
        }
        return null;
    }

    public void invalidateSession(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        redisTemplate.delete(key);
    }
}
