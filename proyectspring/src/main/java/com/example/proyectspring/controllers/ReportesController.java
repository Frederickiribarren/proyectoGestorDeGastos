package com.example.proyectspring.controllers;

import com.example.proyectspring.entity.Ingresos;
import com.example.proyectspring.entity.IngresoMonetario;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.service.IngresoMonetarioService;
import com.example.proyectspring.service.IngresosService;
import com.example.proyectspring.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reportes")
public class ReportesController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private IngresosService ingresosService;

    @Autowired
    private IngresoMonetarioService ingresoMonetarioService;

    @GetMapping
    public String reportes(Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }

        String email = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioService.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuarioOpt.get());
        return "reportes/reportes";
    }

    @GetMapping("/datos-mensuales")
    @ResponseBody
    public Map<String, Object> getDatosMensuales(Authentication auth) {
        String email = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioService.findByEmail(email);
        
        if (usuarioOpt.isEmpty()) {
            return new HashMap<>();
        }
        
        Usuario usuario = usuarioOpt.get();

        // Obtener últimos 6 meses
        List<String> meses = new ArrayList<>();
        List<Double> gastosData = new ArrayList<>();
        List<Double> ingresosData = new ArrayList<>();

        YearMonth mesActual = YearMonth.now();
        
        for (int i = 5; i >= 0; i--) {
            YearMonth mes = mesActual.minusMonths(i);
            meses.add(mes.getMonth().toString().substring(0, 3) + " " + mes.getYear());

            // Calcular gastos del mes
            double totalGastos = ingresosService.obtenerIngresosPorUsuario(usuario).stream()
                    .filter(gasto -> {
                        LocalDate fecha = gasto.getFecha();
                        return YearMonth.from(fecha).equals(mes);
                    })
                    .mapToDouble(Ingresos::getMonto)
                    .sum();

            // Calcular ingresos del mes
            double totalIngresos = ingresoMonetarioService.obtenerIngresosPorUsuario(usuario).stream()
                    .filter(ingreso -> {
                        LocalDate fecha = ingreso.getFecha();
                        return YearMonth.from(fecha).equals(mes);
                    })
                    .mapToDouble(IngresoMonetario::getMonto)
                    .sum();

            gastosData.add(totalGastos);
            ingresosData.add(totalIngresos);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("meses", meses);
        response.put("gastos", gastosData);
        response.put("ingresos", ingresosData);

        return response;
    }

    @GetMapping("/categorias-gastos")
    @ResponseBody
    public Map<String, Object> getCategoriasGastos(Authentication auth) {
        String email = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioService.findByEmail(email);
        
        if (usuarioOpt.isEmpty()) {
            return new HashMap<>();
        }
        
        Usuario usuario = usuarioOpt.get();
        YearMonth mesActual = YearMonth.now();

        Map<String, Double> categorias = ingresosService.obtenerIngresosPorUsuario(usuario).stream()
                .filter(gasto -> {
                    LocalDate fecha = gasto.getFecha();
                    return YearMonth.from(fecha).equals(mesActual);
                })
                .collect(Collectors.groupingBy(
                        Ingresos::getCategoria,
                        Collectors.summingDouble(Ingresos::getMonto)
                ));

        List<String> labels = new ArrayList<>(categorias.keySet());
        List<Double> data = new ArrayList<>(categorias.values());

        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("data", data);

        return response;
    }

    @GetMapping("/metodos-pago")
    @ResponseBody
    public Map<String, Object> getMetodosPago(Authentication auth) {
        String email = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioService.findByEmail(email);
        
        if (usuarioOpt.isEmpty()) {
            return new HashMap<>();
        }
        
        Usuario usuario = usuarioOpt.get();
        YearMonth mesActual = YearMonth.now();

        Map<String, Double> metodos = ingresosService.obtenerIngresosPorUsuario(usuario).stream()
                .filter(gasto -> {
                    LocalDate fecha = gasto.getFecha();
                    return YearMonth.from(fecha).equals(mesActual);
                })
                .collect(Collectors.groupingBy(
                        Ingresos::getMetodoPago,
                        Collectors.summingDouble(Ingresos::getMonto)
                ));

        List<String> labels = new ArrayList<>(metodos.keySet());
        List<Double> data = new ArrayList<>(metodos.values());

        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("data", data);

        return response;
    }

    @GetMapping("/tipos-ingreso")
    @ResponseBody
    public Map<String, Object> getTiposIngreso(Authentication auth) {
        String email = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioService.findByEmail(email);
        
        if (usuarioOpt.isEmpty()) {
            return new HashMap<>();
        }
        
        Usuario usuario = usuarioOpt.get();
        YearMonth mesActual = YearMonth.now();

        Map<String, Double> tipos = ingresoMonetarioService.obtenerIngresosPorUsuario(usuario).stream()
                .filter(ingreso -> {
                    LocalDate fecha = ingreso.getFecha();
                    return YearMonth.from(fecha).equals(mesActual);
                })
                .collect(Collectors.groupingBy(
                        IngresoMonetario::getTipoIngreso,
                        Collectors.summingDouble(IngresoMonetario::getMonto)
                ));

        List<String> labels = new ArrayList<>(tipos.keySet());
        List<Double> data = new ArrayList<>(tipos.values());

        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("data", data);

        return response;
    }

    @GetMapping("/estadisticas-mes")
    @ResponseBody
    public Map<String, Object> getEstadisticasMes(Authentication auth) {
        String email = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioService.findByEmail(email);
        
        if (usuarioOpt.isEmpty()) {
            return new HashMap<>();
        }
        
        Usuario usuario = usuarioOpt.get();
        YearMonth mesActual = YearMonth.now();

        // Total gastos del mes
        double totalGastos = ingresosService.obtenerIngresosPorUsuario(usuario).stream()
                .filter(gasto -> {
                    LocalDate fecha = gasto.getFecha();
                    return YearMonth.from(fecha).equals(mesActual);
                })
                .mapToDouble(Ingresos::getMonto)
                .sum();

        // Total ingresos del mes
        double totalIngresos = ingresoMonetarioService.obtenerIngresosPorUsuario(usuario).stream()
                .filter(ingreso -> {
                    LocalDate fecha = ingreso.getFecha();
                    return YearMonth.from(fecha).equals(mesActual);
                })
                .mapToDouble(IngresoMonetario::getMonto)
                .sum();

        // Conteo de transacciones
        long cantidadGastos = ingresosService.obtenerIngresosPorUsuario(usuario).stream()
                .filter(gasto -> {
                    LocalDate fecha = gasto.getFecha();
                    return YearMonth.from(fecha).equals(mesActual);
                })
                .count();

        long cantidadIngresos = ingresoMonetarioService.obtenerIngresosPorUsuario(usuario).stream()
                .filter(ingreso -> {
                    LocalDate fecha = ingreso.getFecha();
                    return YearMonth.from(fecha).equals(mesActual);
                })
                .count();

        // Promedio diario
        int diasDelMes = mesActual.lengthOfMonth();
        double promedioGastosDiario = totalGastos / diasDelMes;
        double promedioIngresosDiario = totalIngresos / diasDelMes;

        Map<String, Object> response = new HashMap<>();
        response.put("totalGastos", totalGastos);
        response.put("totalIngresos", totalIngresos);
        response.put("ahorro", totalIngresos - totalGastos);
        response.put("cantidadGastos", cantidadGastos);
        response.put("cantidadIngresos", cantidadIngresos);
        response.put("promedioGastosDiario", promedioGastosDiario);
        response.put("promedioIngresosDiario", promedioIngresosDiario);

        return response;
    }
}
