package com.example.proyectspring.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class LoginController {

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam("username") String email,
            @RequestParam("password") String password) {
        
        if ("usuario@email.com".equals(email) && "123456".equals(password)) {
            return "redirect:/dashboard";
        } else {
            return "redirect:/login?error=true";
        }
    }
    
}
