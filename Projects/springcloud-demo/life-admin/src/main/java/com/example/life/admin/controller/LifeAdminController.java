package com.example.life.admin.controller;

import com.example.life.admin.feign.LifeServiceFeign;
import com.example.life.model.Life;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/life")
public class LifeAdminController {
    @Autowired
    private LifeServiceFeign lifeServiceFeign;

    @GetMapping
    public String workAdminPage(Model model) {
        model.addAttribute("works", lifeServiceFeign.getAllWorks());
        model.addAttribute("newWork", new Life());
        return "life-admin";
    }

    @PostMapping("/add")
    public String addWork(Life work) {
        lifeServiceFeign.addWork(work);
        return "redirect:/admin/life";
    }
}
