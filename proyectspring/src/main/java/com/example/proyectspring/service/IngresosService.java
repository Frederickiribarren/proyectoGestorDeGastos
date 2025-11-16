package com.example.proyectspring.service;

import java.util.List;
import java.util.Optional;

import com.example.proyectspring.dto.IngresoDTO;
import com.example.proyectspring.entity.Ingresos;
import com.example.proyectspring.entity.Usuario;

public interface IngresosService {
    
    Ingresos guardarIngreso(IngresoDTO ingresoDTO, Usuario usuario);
    
    List<Ingresos> obtenerIngresosPorUsuario(Usuario usuario);
    
    Optional<Ingresos> buscarPorId(Long id);
    
    void eliminarIngreso(Long id);
    
    Ingresos actualizarIngreso(Long id, IngresoDTO ingresoDTO);
    
    // Métodos legacy
    public Ingresos saveIngresos(Ingresos ingresos);

    public List<Ingresos> findAllIngresos();

    public Ingresos findIngresosById(Long id);
    
    public void deleteIngresos(Long id);
}
