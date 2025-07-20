package com.example.home.service;

import com.example.home.mapper.HomeMapper;
import com.example.home.model.Home;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeService {
    @Autowired
    private HomeMapper workMapper;

    public List<Home> getAllWorks() {
        return workMapper.findAll();
    }

    public void addWork(Home work) {
        workMapper.insert(work);
    }
}