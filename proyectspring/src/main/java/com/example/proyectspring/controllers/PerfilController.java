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
import com.example.proyectspring.dto.SeguridadDTO;
import com.example.proyectspring.dto.PalabraSeguridadDTO;
import com.example.proyectspring.dto.EliminarCuentaDTO;
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
        model.addAttribute("seguridadDTO", new SeguridadDTO());
        model.addAttribute("palabraSeguridadDTO", new PalabraSeguridadDTO());
        model.addAttribute("eliminarCuentaDTO", new EliminarCuentaDTO());
        model.addAttribute("usuario", usuario);
        model.addAttribute("tienePalabraSeguridad", usuario.getPalabraSeguridad() != null);
        
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
    
    @PostMapping("/perfil/cambiar-password")
    public String cambiarPassword(@Valid @ModelAttribute SeguridadDTO seguridadDTO,
                                  BindingResult result,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorSeguridad", "Por favor, corrige los errores");
            return "redirect:/perfil";
        }
        
        try {
            String email = principal.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            boolean cambioExitoso = usuarioService.cambiarPassword(usuario.getId(), seguridadDTO);
            
            if (cambioExitoso) {
                redirectAttributes.addFlashAttribute("mensajeSeguridad", "Contraseña actualizada correctamente");
            } else {
                redirectAttributes.addFlashAttribute("errorSeguridad", "La contraseña actual es incorrecta o las nuevas contraseñas no coinciden");
            }
            
            return "redirect:/perfil";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorSeguridad", "Error al cambiar la contraseña: " + e.getMessage());
            return "redirect:/perfil";
        }
    }
    
    @PostMapping("/perfil/configurar-palabra-seguridad")
    public String configurarPalabraSeguridad(@Valid @ModelAttribute PalabraSeguridadDTO palabraSeguridadDTO,
                                             BindingResult result,
                                             Principal principal,
                                             RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorPalabraSeguridad", "Por favor, corrige los errores");
            return "redirect:/perfil";
        }
        
        try {
            String email = principal.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            usuarioService.configurarPalabraSeguridad(usuario.getId(), palabraSeguridadDTO.getPalabraSeguridad());
            
            redirectAttributes.addFlashAttribute("mensajePalabraSeguridad", "Palabra de seguridad configurada correctamente");
            return "redirect:/perfil";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorPalabraSeguridad", "Error al configurar palabra de seguridad: " + e.getMessage());
            return "redirect:/perfil";
        }
    }
    
    @PostMapping("/perfil/eliminar-cuenta")
    public String eliminarCuenta(@Valid @ModelAttribute EliminarCuentaDTO eliminarCuentaDTO,
                                 BindingResult result,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorEliminacion", "Datos incompletos");
            return "redirect:/perfil";
        }
        
        try {
            String email = principal.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            usuarioService.eliminarCuentaPermanentemente(usuario.getId(), eliminarCuentaDTO);
            
            // Redirigir al login con mensaje de cuenta eliminada
            redirectAttributes.addFlashAttribute("mensajeEliminacion", "Tu cuenta ha sido eliminada permanentemente");
            return "redirect:/login?cuentaEliminada";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorEliminacion", e.getMessage());
            return "redirect:/perfil";
        }
    }
}
