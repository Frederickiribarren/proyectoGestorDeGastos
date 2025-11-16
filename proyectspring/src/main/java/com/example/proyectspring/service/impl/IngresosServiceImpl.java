package com.example.proyectspring.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.proyectspring.dao.IIngresosDao;
import com.example.proyectspring.dto.IngresoDTO;
import com.example.proyectspring.entity.Ingresos;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.service.IngresosService;

@Service
public class IngresosServiceImpl implements IngresosService {
    
    private final IIngresosDao ingresosDao;

    public IngresosServiceImpl(IIngresosDao ingresosDao) {
        this.ingresosDao = ingresosDao;
    }

    @Override
    @Transactional
    public Ingresos guardarIngreso(IngresoDTO ingresoDTO, Usuario usuario) {
        Ingresos ingreso = new Ingresos();
        ingreso.setMonto(ingresoDTO.getMonto());
        ingreso.setDescripcion(ingresoDTO.getDescripcion());
        ingreso.setCategoria(ingresoDTO.getCategoria());
        ingreso.setMetodoPago(ingresoDTO.getMetodoPago());
        ingreso.setNotaAdicional(ingresoDTO.getNotaAdicional());
        ingreso.setFecha(ingresoDTO.getFecha());
        ingreso.setUsuario(usuario);
        
        return ingresosDao.save(ingreso);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ingresos> obtenerIngresosPorUsuario(Usuario usuario) {
        return ingresosDao.findByUsuarioOrderByFechaDesc(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ingresos> buscarPorId(Long id) {
        return ingresosDao.findById(id);
    }

    @Override
    @Transactional
    public void eliminarIngreso(Long id) {
        ingresosDao.deleteById(id);
    }

    @Override
    @Transactional
    public Ingresos actualizarIngreso(Long id, IngresoDTO ingresoDTO) {
        Ingresos ingreso = ingresosDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingreso no encontrado"));
        
        ingreso.setMonto(ingresoDTO.getMonto());
        ingreso.setDescripcion(ingresoDTO.getDescripcion());
        ingreso.setCategoria(ingresoDTO.getCategoria());
        ingreso.setMetodoPago(ingresoDTO.getMetodoPago());
        ingreso.setNotaAdicional(ingresoDTO.getNotaAdicional());
        ingreso.setFecha(ingresoDTO.getFecha());
        
        return ingresosDao.save(ingreso);
    }

    // Métodos legacy
    @Override
    @Transactional
    public Ingresos saveIngresos(Ingresos ingresos) {
        return ingresosDao.save(ingresos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ingresos> findAllIngresos() {
        return ingresosDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Ingresos findIngresosById(Long id) {
        return ingresosDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void deleteIngresos(Long id) {
        ingresosDao.deleteById(id);
    }
}
