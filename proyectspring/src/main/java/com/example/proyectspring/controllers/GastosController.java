package com.example.proyectspring.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.service.UsuarioService;

@Controller
public class GastosController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping("/gastos")
    public String getGastos(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        String email = principal.getName();
        Usuario usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        model.addAttribute("usuario", usuario);
        return "gastos/gastos";
    }
    
}
