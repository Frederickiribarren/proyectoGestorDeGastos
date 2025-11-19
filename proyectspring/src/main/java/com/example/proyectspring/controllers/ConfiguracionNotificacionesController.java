package com.example.proyectspring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.proyectspring.dto.ConfiguracionNotificacionesDTO;
import com.example.proyectspring.entity.ConfiguracionNotificaciones;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.service.NotificacionService;
import com.example.proyectspring.service.UsuarioService;

@RestController
@RequestMapping("/api/notificaciones")
public class ConfiguracionNotificacionesController {

    @Autowired
    private NotificacionService notificacionService;
    
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/configuracion")
    public ResponseEntity<?> obtenerConfiguracion(Authentication authentication) {
        try {
            String email = authentication.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            ConfiguracionNotificaciones config = notificacionService.obtenerConfiguracion(usuario.getId());
            
            if (config == null) {
                // Crear configuración por defecto si no existe
                config = notificacionService.crearConfiguracionPorDefecto(usuario);
            }

            ConfiguracionNotificacionesDTO dto = new ConfiguracionNotificacionesDTO(
                config.isNotificacionesEmail(),
                config.isAlertasTransacciones(),
                config.isAlertasPresupuesto(),
                config.isReportesMensuales(),
                config.isPromociones()
            );

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/configuracion")
    public ResponseEntity<?> actualizarConfiguracion(
            @RequestBody ConfiguracionNotificacionesDTO dto,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            ConfiguracionNotificaciones config = notificacionService.obtenerConfiguracion(usuario.getId());
            
            if (config == null) {
                config = new ConfiguracionNotificaciones(usuario);
            }

            config.setNotificacionesEmail(dto.isNotificacionesEmail());
            config.setAlertasTransacciones(dto.isAlertasTransacciones());
            config.setAlertasPresupuesto(dto.isAlertasPresupuesto());
            config.setReportesMensuales(dto.isReportesMensuales());
            config.setPromociones(dto.isPromociones());

            notificacionService.guardarConfiguracion(config);

            return ResponseEntity.ok("Configuración actualizada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
