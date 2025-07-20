package com.example.life.service;

import com.example.life.mapper.LifeMapper;
import com.example.life.model.Life;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LifeService {
    @Autowired
    private LifeMapper lifeMapper;

    public List<Life> getAllWorks() {
        return lifeMapper.findAll();
    }

    public void addWork(Life work) {
        lifeMapper.insert(work);
    }
}