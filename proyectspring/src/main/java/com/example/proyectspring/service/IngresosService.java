package com.example.proyectspring.service;

import java.util.List;

import com.example.proyectspring.entity.Ingresos;

public interface IngresosService {
    public Ingresos saveIngresos(Ingresos ingresos);

    public List<Ingresos> findAllIngresos();

    public Ingresos findIngresosById(Long id);
    
    public void deleteIngresos(Long id);
}
