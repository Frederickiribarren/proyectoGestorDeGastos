package com.example.proyectspring.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.service.UsuarioService;

@Controller
public class GestorUsuariosController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/gestor-usuarios")
    public String getGestorUsuarios(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        // Obtener usuario actual para navbar
        String email = principal.getName();
        Usuario usuarioActual = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Obtener todos los usuarios
        List<Usuario> usuarios = usuarioService.findAll();
        
        model.addAttribute("usuario", usuarioActual);
        model.addAttribute("usuarios", usuarios);
        
        return "gestorUsuarios/gestorUsuarios";
    }
    
}
