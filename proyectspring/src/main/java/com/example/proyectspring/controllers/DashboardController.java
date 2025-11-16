package com.example.proyectspring.controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.proyectspring.dto.ActividadDTO;
import com.example.proyectspring.entity.Ingresos;
import com.example.proyectspring.entity.IngresoMonetario;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.service.IngresosService;
import com.example.proyectspring.service.IngresoMonetarioService;
import com.example.proyectspring.service.UsuarioService;

@Controller
public class DashboardController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private IngresosService ingresosService;
    
    @Autowired
    private IngresoMonetarioService ingresoMonetarioService;

    @GetMapping("/dashboard")
    public String getDashboard(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        String email = principal.getName();
        Usuario usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Obtener mes actual
        YearMonth mesActual = YearMonth.now();
        LocalDate inicioMes = mesActual.atDay(1);
        LocalDate finMes = mesActual.atEndOfMonth();
        
        // Obtener todos los gastos e ingresos del usuario
        List<Ingresos> todosGastos = ingresosService.obtenerIngresosPorUsuario(usuario);
        List<IngresoMonetario> todosIngresos = ingresoMonetarioService.obtenerIngresosPorUsuario(usuario);
        
        // Filtrar por mes actual
        List<Ingresos> gastosMesActual = todosGastos.stream()
            .filter(g -> !g.getFecha().isBefore(inicioMes) && !g.getFecha().isAfter(finMes))
            .collect(Collectors.toList());
            
        List<IngresoMonetario> ingresosMesActual = todosIngresos.stream()
            .filter(i -> !i.getFecha().isBefore(inicioMes) && !i.getFecha().isAfter(finMes))
            .collect(Collectors.toList());
        
        // Calcular totales del mes
        double totalGastosMes = gastosMesActual.stream()
            .mapToDouble(Ingresos::getMonto)
            .sum();
            
        double totalIngresosMes = ingresosMesActual.stream()
            .mapToDouble(IngresoMonetario::getMonto)
            .sum();
        
        // Calcular ahorro (Ingresos - Gastos)
        double ahorro = totalIngresosMes - totalGastosMes;
        
        // Total de transacciones (gastos del mes)
        int totalTransacciones = gastosMesActual.size();
        
        // Combinar gastos e ingresos en una lista de actividades
        List<ActividadDTO> actividadesRecientes = new ArrayList<>();
        
        // Convertir gastos a ActividadDTO
        for (Ingresos gasto : todosGastos) {
            // Convertir Date a LocalDateTime
            LocalDateTime fechaCreacion = gasto.getCreatedAt() != null 
                ? gasto.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                : LocalDateTime.now();
                
            actividadesRecientes.add(new ActividadDTO(
                "GASTO",
                gasto.getDescripcion(),
                gasto.getFecha(),
                fechaCreacion,
                gasto.getMonto(),
                gasto.getCategoria(),
                gasto.getMetodoPago(),
                gasto.getNumeroCuotas(),
                gasto.getCuotaActual()
            ));
        }
        
        // Convertir ingresos a ActividadDTO
        for (IngresoMonetario ingreso : todosIngresos) {
            LocalDateTime fechaCreacion = ingreso.getFechaCreacion() != null 
                ? ingreso.getFechaCreacion()
                : LocalDateTime.now();
                
            actividadesRecientes.add(new ActividadDTO(
                "INGRESO",
                ingreso.getDescripcion(),
                ingreso.getFecha(),
                fechaCreacion,
                ingreso.getMonto(),
                ingreso.getTipoIngreso(),
                ingreso.getOrigen(),
                ingreso.getCategoria()
            ));
        }
        
        // Ordenar por fecha de creación descendente (más reciente primero) y limitar a 7
        List<ActividadDTO> ultimas7Actividades = actividadesRecientes.stream()
            .sorted((a1, a2) -> a2.getFechaCreacion().compareTo(a1.getFechaCreacion()))
            .limit(7)
            .collect(Collectors.toList());
        
        // Calcular categoría con más gastos del mes
        Map<String, Double> gastosPorCategoria = new HashMap<>();
        for (Ingresos gasto : gastosMesActual) {
            String categoria = gasto.getCategoria();
            gastosPorCategoria.put(categoria, 
                gastosPorCategoria.getOrDefault(categoria, 0.0) + gasto.getMonto());
        }
        
        // Ordenar categorías por monto descendente
        List<Map.Entry<String, Double>> categoriasOrdenadas = gastosPorCategoria.entrySet().stream()
            .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
            .limit(5)  // Top 5 categorías
            .collect(Collectors.toList());
        
        // Agregar datos al modelo
        model.addAttribute("usuario", usuario);
        model.addAttribute("totalGastosMes", totalGastosMes);
        model.addAttribute("totalIngresosMes", totalIngresosMes);
        model.addAttribute("ahorro", ahorro);
        model.addAttribute("totalTransacciones", totalTransacciones);
        model.addAttribute("actividadReciente", ultimas7Actividades);
        model.addAttribute("categoriasTop", categoriasOrdenadas);
        model.addAttribute("hayActividad", !ultimas7Actividades.isEmpty());
        model.addAttribute("hayCategorias", !categoriasOrdenadas.isEmpty());
        
        return "dashboard";
    }

}
