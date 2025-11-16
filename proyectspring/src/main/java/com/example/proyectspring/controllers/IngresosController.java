package com.example.proyectspring.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.proyectspring.entity.IngresoMonetario;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.service.IngresoMonetarioService;
import com.example.proyectspring.service.UsuarioService;

@Controller
public class IngresosController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private IngresoMonetarioService ingresoMonetarioService;
    
    @GetMapping("/ingresos")
    public String getIngresos(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        String email = principal.getName();
        Usuario usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        List<IngresoMonetario> ingresos = ingresoMonetarioService.obtenerIngresosPorUsuario(usuario);
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("ingresos", ingresos);
        return "ingresos/ingresos";
    }
    
    @PostMapping("/ingresos/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarIngreso(@RequestBody IngresoMonetario ingreso, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }
        
        try {
            String email = principal.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            ingreso.setUsuario(usuario);
            ingresoMonetarioService.guardarIngreso(ingreso);
            return ResponseEntity.ok("Ingreso guardado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al guardar el ingreso: " + e.getMessage());
        }
    }
    
    @PostMapping("/ingresos/actualizar/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarIngreso(@PathVariable Long id, @RequestBody IngresoMonetario ingreso, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }
        
        try {
            String email = principal.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            ingreso.setId(id);
            ingreso.setUsuario(usuario);
            ingresoMonetarioService.guardarIngreso(ingreso);
            return ResponseEntity.ok("Ingreso actualizado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar el ingreso: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/ingresos/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarIngreso(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }
        
        try {
            ingresoMonetarioService.eliminarIngreso(id);
            return ResponseEntity.ok("Ingreso eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar el ingreso: " + e.getMessage());
        }
    }
}
