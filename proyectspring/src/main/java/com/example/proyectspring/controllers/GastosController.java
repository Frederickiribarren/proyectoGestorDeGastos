package com.example.proyectspring.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class GastosController {
    
    @GetMapping("/gastos")
    public String getGastos() {
        return "gastos/gastos";
    }
    
}
