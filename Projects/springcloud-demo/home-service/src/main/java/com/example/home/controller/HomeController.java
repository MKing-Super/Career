package com.example.home.controller;

import com.example.home.model.Home;
import com.example.home.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private HomeService workService;

    @GetMapping
    public List<Home> getAllWorks() {
        return workService.getAllWorks();
    }

    @PostMapping
    public void addWork(@RequestBody Home work) {
        workService.addWork(work);
    }
}