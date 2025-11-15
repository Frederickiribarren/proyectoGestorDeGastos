package com.example.proyectspring.controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyectspring.dto.PerfilDTO;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.service.UsuarioService;

import jakarta.validation.Valid;

@Controller
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDate.class, new java.beans.PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.isEmpty()) {
                    setValue(null);
                } else {
                    setValue(LocalDate.parse(text, DateTimeFormatter.ISO_DATE));
                }
            }
        });
    }

    @GetMapping("/perfil")
    public String getMostrarPerfil(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        // Obtener el usuario autenticado
        String email = principal.getName();
        Usuario usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Crear DTO con los datos del usuario
        PerfilDTO perfilDTO = new PerfilDTO();
        perfilDTO.setNombre(usuario.getNombre());
        perfilDTO.setApellido(usuario.getApellido());
        perfilDTO.setEmail(usuario.getEmail());
        perfilDTO.setTelefono(usuario.getTelefono());
        perfilDTO.setPais(usuario.getPais());
        perfilDTO.setBiografia(usuario.getBiografia());
        perfilDTO.setFechaNacimiento(usuario.getFechaNacimiento());
        
        model.addAttribute("perfilDTO", perfilDTO);
        model.addAttribute("usuario", usuario);
        
        return "perfil/perfil";
    }
    
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@Valid @ModelAttribute PerfilDTO perfilDTO,
                                   BindingResult result,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        if (result.hasErrors()) {
            String email = principal.getName();
            Usuario usuario = usuarioService.findByEmail(email).orElse(null);
            model.addAttribute("usuario", usuario);
            model.addAttribute("error", "Por favor, corrige los errores en el formulario");
            return "perfil/perfil";
        }
        
        try {
            String email = principal.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            usuarioService.actualizarPerfil(usuario.getId(), perfilDTO);
            
            redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente");
            return "redirect:/perfil";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
            return "perfil/perfil";
        }
    }
}
