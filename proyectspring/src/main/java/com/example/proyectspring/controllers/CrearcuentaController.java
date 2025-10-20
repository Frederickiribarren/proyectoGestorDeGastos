package com.example.proyectspring.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class CrearcuentaController {
    @GetMapping("/crearcuenta")
    public String getCrearCuenta() {
        return "crearcuenta";
    }
    
}
