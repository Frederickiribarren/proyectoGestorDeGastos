package com.example.proyectspring.controllers;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.proyectspring.entity.ConfiguracionTarjeta;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.service.ConfiguracionTarjetaService;
import com.example.proyectspring.service.UsuarioService;

@Controller
@RequestMapping("/tarjetas")
public class TarjetasController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private ConfiguracionTarjetaService configuracionTarjetaService;
    
    @GetMapping("/configuraciones")
    @ResponseBody
    public ResponseEntity<?> obtenerConfiguraciones(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }
        
        try {
            String email = principal.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Obtener todas las configuraciones de tarjetas del usuario
            List<ConfiguracionTarjeta> configuraciones = configuracionTarjetaService.obtenerConfiguracionesPorUsuario(usuario);
            
            // Convertir a Map para la respuesta JSON
            List<Map<String, Object>> resultado = configuraciones.stream()
                    .map(config -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("nombreTarjeta", config.getNombreTarjeta());
                        map.put("diaCorte", config.getDiaCorte());
                        map.put("diaPago", config.getDiaPago());
                        return map;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al obtener configuraciones: " + e.getMessage());
        }
    }
    
    @PostMapping("/configurar")
    @ResponseBody
    public ResponseEntity<?> configurarTarjeta(@RequestBody Map<String, Object> datos, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }
        
        try {
            String email = principal.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            String nombreTarjeta = (String) datos.get("nombreTarjeta");
            Integer diaCorte = (Integer) datos.get("diaCorte");
            Integer diaPago = (Integer) datos.get("diaPago");
            
            // Validar datos
            if (nombreTarjeta == null || diaCorte == null || diaPago == null) {
                return ResponseEntity.badRequest().body("Datos incompletos");
            }
            
            if (diaCorte < 1 || diaCorte > 31 || diaPago < 1 || diaPago > 31) {
                return ResponseEntity.badRequest().body("Los días deben estar entre 1 y 31");
            }
            
            // Guardar o actualizar la configuración en la tabla dedicada
            configuracionTarjetaService.guardarOActualizarConfiguracion(usuario, nombreTarjeta, diaCorte, diaPago);
            
            return ResponseEntity.ok("Configuración guardada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al configurar tarjeta: " + e.getMessage());
        }
    }
}
