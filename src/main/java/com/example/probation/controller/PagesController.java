package com.example.probation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PagesController {
    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/registration")
    public String createUser() {
        return "registration";
    }

    @GetMapping("/confirmation")
    public String confirm(@RequestParam("token") String token) {
        return "confirmation";
    }
}
