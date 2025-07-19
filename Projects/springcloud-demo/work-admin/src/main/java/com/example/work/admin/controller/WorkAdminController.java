package com.example.work.admin.controller;

import com.example.work.admin.feign.WorkServiceFeign;
import com.example.work.model.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}