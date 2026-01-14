package com.example.home.admin.controller;

import com.example.home.admin.feign.home.HomeServiceFeign;
import com.example.home.model.Home;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/home")
public class HomeAdminController {
    @Autowired
    private HomeServiceFeign homeServiceFeign;

    @GetMapping
    public String workAdminPage(Model model) {
        model.addAttribute("works", homeServiceFeign.getAllWorks());
        model.addAttribute("newWork", new Home());
        return "home-admin";
    }

    @PostMapping("/add")
    public String addWork(Home work) {
        homeServiceFeign.addWork(work);
        return "redirect:/admin/work";
    }
}