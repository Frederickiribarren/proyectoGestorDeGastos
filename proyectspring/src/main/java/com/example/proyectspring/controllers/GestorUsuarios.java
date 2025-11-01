package com.example.proyectspring.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GestorUsuarios {

    @GetMapping("/gestor-usuarios")
    public String mostrarGestorUsuarios() {
        return "gestorUsuarios/gestorUsuarios";
    }
}
