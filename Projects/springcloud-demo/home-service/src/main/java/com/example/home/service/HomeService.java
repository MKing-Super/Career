package com.example.home.service;

import com.example.home.mapper.HomeMapper;
import com.example.home.mapper.UserMapper;
import com.example.home.model.Home;
import com.example.home.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class HomeService {
    @Autowired
    private HomeMapper workMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public List<Home> getAllWorks() {
        return workMapper.findAll();
    }

    public void addWork(Home work) {
        workMapper.insert(work);
    }

    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public Set<String> getAllKeys(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            pattern = "*";
        }
        return stringRedisTemplate.keys(pattern);
    }

    public String getValue(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void setValue(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public Boolean deleteKey(String key) {
        return stringRedisTemplate.delete(key);
    }
}