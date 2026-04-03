package com.example.home.controller;

import com.example.home.model.User;
import com.example.home.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private HomeService homeService;

    @GetMapping("/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        User user = homeService.getUserByUsername(username);
        
        if (user == null) {
            result.put("success", false);
            result.put("message", "User not found");
            return result;
        }
        
        if (!user.getPassword().equals(password)) {
            result.put("success", false);
            result.put("message", "Wrong password");
            return result;
        }
        
        result.put("success", true);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        return result;
    }
}
