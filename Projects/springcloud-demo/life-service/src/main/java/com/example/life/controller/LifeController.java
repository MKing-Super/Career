package com.example.life.controller;

import com.example.life.model.Life;
import com.example.life.service.LifeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/life")
public class LifeController {
    @Autowired
    private LifeService lifeService;

    @GetMapping
    public List<Life> getAllWorks() {
        return lifeService.getAllWorks();
    }

    @PostMapping
    public void addWork(@RequestBody Life work) {
        lifeService.addWork(work);
    }
}