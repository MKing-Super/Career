package com.example.work.service;

import com.example.work.mapper.WorkMapper;
import com.example.work.model.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WorkService {
    @Autowired
    private WorkMapper workMapper;

    public List<Work> getAllWorks() {
        return workMapper.findAll();
    }

    public void addWork(Work work) {
        workMapper.insert(work);
    }
}