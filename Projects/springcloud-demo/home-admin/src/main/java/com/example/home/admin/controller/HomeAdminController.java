package com.example.home.admin.controller;

import com.example.home.admin.feign.home.HomeServiceFeign;
import com.example.home.model.Home;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/admin/home")
public class HomeAdminController {
    @Autowired
    private HomeServiceFeign homeServiceFeign;

    @GetMapping
    public String workAdminPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("roles", auth.getAuthorities());
        }
        model.addAttribute("works", homeServiceFeign.getAllWorks());
        model.addAttribute("newWork", new Home());
        return "home-admin";
    }

    @PostMapping("/add")
    public String addWork(Home work) {
        homeServiceFeign.addWork(work);
        return "redirect:/admin/home";
    }
}