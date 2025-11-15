package com.example.proyectspring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyectspring.dto.RegistroDTO;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.service.UsuarioService;

import jakarta.validation.Valid;

@Controller
public class CrearcuentaController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/crearcuenta")
    public String mostrarFormulario(Model model) {
        model.addAttribute("registroDTO", new RegistroDTO());
        return "crearcuenta";
    }

    @PostMapping("/registro")
    public String registrar(
            @Valid @ModelAttribute("registroDTO") RegistroDTO registroDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Si hay errores de validación de anotaciones
        if (result.hasErrors()) {
            return "crearcuenta";
        }

        try {
            // Registrar usuario
            Usuario usuario = usuarioService.registrarUsuario(registroDTO);
            
            // Redirigir con mensaje de éxito
            redirectAttributes.addFlashAttribute("mensajeExito", 
                "¡Registro exitoso! Bienvenido " + usuario.getNombre());
            
            return "redirect:/login";
            
        } catch (RuntimeException e) {
            // Mostrar error
            model.addAttribute("error", e.getMessage());
            return "crearcuenta";
        }
    }
}
