package com.example.gatekeeper.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class HomeController {
    @GetMapping("/home")
public String home(Model model) {
    model.addAttribute("title", "Inicio");
    return "home";
    }
}
