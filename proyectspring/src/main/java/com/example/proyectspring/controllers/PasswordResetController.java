package com.example.proyectspring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyectspring.entity.PasswordResetToken;
import com.example.proyectspring.service.PasswordResetService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/recuperar-password")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @GetMapping
    public String mostrarFormularioSolicitud() {
        return "recuperar-password";
    }

    @PostMapping("/solicitar")
    public String solicitarRecuperacion(
            @RequestParam String email,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        
        passwordResetService.crearTokenRecuperacion(email, baseUrl);
        
        redirectAttributes.addFlashAttribute("mensaje", 
            "Si el correo existe, recibirás instrucciones para restablecer tu contraseña.");
        
        return "redirect:/login";
    }

    @GetMapping("/cambiar")
    public String mostrarFormularioCambio(@RequestParam String token, Model model) {
        PasswordResetToken resetToken = passwordResetService.validarToken(token);
        
        if (resetToken == null) {
            model.addAttribute("error", "El enlace es inválido o ha expirado.");
            return "error-token";
        }
        
        model.addAttribute("token", token);
        return "cambiar-password";
    }

    @PostMapping("/cambiar")
    public String cambiarPassword(
            @RequestParam String token,
            @RequestParam String nuevaPassword,
            @RequestParam String confirmarPassword,
            RedirectAttributes redirectAttributes) {
        
        if (!nuevaPassword.equals(confirmarPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden.");
            return "redirect:/recuperar-password/cambiar?token=" + token;
        }
        
        if (nuevaPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
            return "redirect:/recuperar-password/cambiar?token=" + token;
        }
        
        boolean cambiado = passwordResetService.cambiarPassword(token, nuevaPassword);
        
        if (cambiado) {
            redirectAttributes.addFlashAttribute("mensajeExito", 
                "Tu contraseña ha sido restablecida exitosamente. Ya puedes iniciar sesión.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "El enlace es inválido o ha expirado.");
            return "redirect:/login";
        }
    }
}
