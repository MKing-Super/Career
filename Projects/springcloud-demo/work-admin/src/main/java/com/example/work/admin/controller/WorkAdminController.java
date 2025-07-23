package com.example.work.admin.controller;

import com.example.work.admin.feign.WorkServiceFeign;
import com.example.work.model.Work;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Slf4j
@Controller
@RequestMapping("/admin/work")
public class WorkAdminController {
    @Autowired
    private WorkServiceFeign workServiceFeign;

    @GetMapping
    public String workAdminPage(Model model) {
        model.addAttribute("works", workServiceFeign.getAllWorks());
        model.addAttribute("newWork", new Work());
        return "work-admin";
    }

    @PostMapping("/add")
    public String addWork(Work work) {
        workServiceFeign.addWork(work);
        return "redirect:/admin/work";
    }

    @GetMapping("/getTest")
    @ResponseBody
    public String getTest(){
        Date date = new Date();
        return workServiceFeign.getTest("getTest");
    }

    @GetMapping("/postTest")
    @ResponseBody
    public String postTest(){
        Date date = new Date();
        return workServiceFeign.postTest("postTest");
    }
}