package com.example.admin.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "redirect:/portal";
    }

    @GetMapping("/portal")
    public String portal() {
        return "layout";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/portal/work")
    public String work() {
        return "redirect:http://localhost:9002/admin/work";
    }

    @GetMapping("/portal/life")
    public String life() {
        return "redirect:http://localhost:9004/admin/life";
    }

    @GetMapping("/portal/home")
    public String home() {
        return "redirect:http://localhost:9006/admin/home";
    }
}
