package com.example.proyectspring.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

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

import com.example.proyectspring.dto.IngresoDTO;
import com.example.proyectspring.entity.Ingresos;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.service.IngresosService;
import com.example.proyectspring.service.UsuarioService;

@Controller
public class GastosController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private IngresosService ingresosService;
    
    @GetMapping("/gastos")
    public String getGastos(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        String email = principal.getName();
        Usuario usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Obtener los gastos del usuario
        List<Ingresos> gastos = ingresosService.obtenerIngresosPorUsuario(usuario);

        // Preparar una versión serializable a JSON (fechas como strings)
        List<Map<String, Object>> gastosForJs = new ArrayList<>();
        for (Ingresos g : gastos) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", g.getId());
            m.put("monto", g.getMonto());
            m.put("descripcion", g.getDescripcion());
            m.put("categoria", g.getCategoria());
            m.put("metodoPago", g.getMetodoPago());
            m.put("fecha", g.getFecha() != null ? g.getFecha().toString() : null);
            gastosForJs.add(m);
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("gastos", gastos);
        model.addAttribute("gastosForJs", gastosForJs);
        return "gastos/gastos";
    }
    
    @PostMapping("/gastos/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarGasto(@RequestBody IngresoDTO ingresoDTO, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }
        
        try {
            String email = principal.getName();
            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            ingresosService.guardarIngreso(ingresoDTO, usuario);
            return ResponseEntity.ok("Gasto guardado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al guardar el gasto: " + e.getMessage());
        }
    }
    
    @PostMapping("/gastos/actualizar")
    @ResponseBody
    public ResponseEntity<?> actualizarGasto(@RequestBody IngresoDTO ingresoDTO, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }
        
        try {
            ingresosService.actualizarIngreso(ingresoDTO.getId(), ingresoDTO);
            return ResponseEntity.ok("Gasto actualizado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar el gasto: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/gastos/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarGasto(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }
        
        try {
            ingresosService.eliminarIngreso(id);
            return ResponseEntity.ok("Gasto eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar el gasto: " + e.getMessage());
        }
    }
    
}
