package com.example.probation.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class PagesController() {
    @GetMapping("/home")
    fun home(): String {
        return "home"
    }

    @GetMapping("/registration")
    fun createUser(): String {
        return "registration"
    }

    @GetMapping("/confirmation")
    fun confirm(@RequestParam("token") token: String): String {
        return "confirmation"
    }

    @GetMapping("/personal")
    fun page(): String {
        return "personal"
    }
}
