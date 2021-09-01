package com.example.probation.controller

import com.example.probation.core.enums.Actions
import com.example.probation.core.enums.Current
import com.example.probation.service.UsersService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class PagesController(
    @Value("\${server.host}") private val host: String,
    private val usersService: UsersService
) {

    @GetMapping("/home")
    fun home(model: Model): String {
        usersService.sendLog(Actions.HOME.action, Current.CURRENT.value, null)
        model["host"] = host
        return "home"
    }

    @GetMapping("/registration")
    fun createUser(model: Model): String {
        model["host"] = host
        return "registration"
    }

    @GetMapping("/confirmation")
    fun confirm(@RequestParam("token") token: String, model: Model): String {
        model["host"] = host
        return "confirmation"
    }

    @GetMapping("/personal")
    fun page(model: Model): String {
        model["host"] = host
        return "personal"
    }
}
