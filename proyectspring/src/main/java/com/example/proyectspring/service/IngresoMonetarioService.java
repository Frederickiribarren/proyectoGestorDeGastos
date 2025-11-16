package com.example.proyectspring.service;

import java.time.LocalDate;
import java.util.List;

import com.example.proyectspring.entity.IngresoMonetario;
import com.example.proyectspring.entity.Usuario;

public interface IngresoMonetarioService {
    
    IngresoMonetario guardarIngreso(IngresoMonetario ingreso);
    
    List<IngresoMonetario> obtenerIngresosPorUsuario(Usuario usuario);
    
    List<IngresoMonetario> obtenerIngresosPorTipo(Usuario usuario, String tipoIngreso);
    
    List<IngresoMonetario> obtenerIngresosPorPeriodo(Usuario usuario, LocalDate fechaInicio, LocalDate fechaFin);
    
    void eliminarIngreso(Long id);
    
    Double calcularTotalIngresosMes(Usuario usuario, int mes, int anio);
}
