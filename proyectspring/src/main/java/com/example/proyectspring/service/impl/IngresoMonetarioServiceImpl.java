package com.example.proyectspring.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyectspring.entity.IngresoMonetario;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.repository.IngresoMonetarioRepository;
import com.example.proyectspring.service.IngresoMonetarioService;

@Service
public class IngresoMonetarioServiceImpl implements IngresoMonetarioService {
    
    @Autowired
    private IngresoMonetarioRepository ingresoMonetarioRepository;
    
    @Override
    public IngresoMonetario guardarIngreso(IngresoMonetario ingreso) {
        return ingresoMonetarioRepository.save(ingreso);
    }
    
    @Override
    public List<IngresoMonetario> obtenerIngresosPorUsuario(Usuario usuario) {
        return ingresoMonetarioRepository.findByUsuarioOrderByFechaDesc(usuario);
    }
    
    @Override
    public List<IngresoMonetario> obtenerIngresosPorTipo(Usuario usuario, String tipoIngreso) {
        return ingresoMonetarioRepository.findByUsuarioAndTipoIngresoOrderByFechaDesc(usuario, tipoIngreso);
    }
    
    @Override
    public List<IngresoMonetario> obtenerIngresosPorPeriodo(Usuario usuario, LocalDate fechaInicio, LocalDate fechaFin) {
        return ingresoMonetarioRepository.findByUsuarioAndFechaBetweenOrderByFechaDesc(usuario, fechaInicio, fechaFin);
    }
    
    @Override
    public void eliminarIngreso(Long id) {
        ingresoMonetarioRepository.deleteById(id);
    }
    
    @Override
    public Double calcularTotalIngresosMes(Usuario usuario, int mes, int anio) {
        LocalDate fechaInicio = LocalDate.of(anio, mes, 1);
        LocalDate fechaFin = fechaInicio.plusMonths(1).minusDays(1);
        
        List<IngresoMonetario> ingresos = obtenerIngresosPorPeriodo(usuario, fechaInicio, fechaFin);
        
        return ingresos.stream()
                .mapToDouble(IngresoMonetario::getMonto)
                .sum();
    }
}
