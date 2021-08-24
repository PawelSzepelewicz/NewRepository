package com.example.probation.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PagesController {
    @Value("${server.host}")
    private String host;

    @GetMapping("/home")
    public String home(final Model model) {
        model.addAttribute("host", host);
        return "home";
    }

    @GetMapping("/registration")
    public String createUser(final Model model) {
        model.addAttribute("host", host);
        return "registration";
    }

    @GetMapping("/confirmation")
    public String confirm(@RequestParam("token") String token) {
        return "confirmation";
    }
}
