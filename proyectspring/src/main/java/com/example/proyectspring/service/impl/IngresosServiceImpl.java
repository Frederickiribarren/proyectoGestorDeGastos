package com.example.proyectspring.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.proyectspring.entity.Ingresos;
import com.example.proyectspring.service.IngresosService;

import jakarta.transaction.Transactional;

public class IngresosServiceImpl implements IngresosService {
    @Autowired
    private IngresosService ingresosService;

    @Transactional
    public Ingresos saveIngresos(Ingresos ingresos) {
        return ingresosService.saveIngresos(ingresos);
    }

    @Transactional
    public void deleteIngresos(Long id) {
        ingresosService.deleteIngresos(id);
    }

    @Transactional
    public Ingresos findIngresosById(Long id) {
        return ingresosService.findIngresosById(id);
    }

    @Transactional
    public List<Ingresos> findAllIngresos() {
        return ingresosService.findAllIngresos();
    }

}
