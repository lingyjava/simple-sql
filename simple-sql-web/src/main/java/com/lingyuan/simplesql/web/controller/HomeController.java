package com.lingyuan.simplesql.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home(Model model) {
        // 对应 templates/index.html
        model.addAttribute("title", "Simple SQL 控制台");
        return "index"; 
    }
}
