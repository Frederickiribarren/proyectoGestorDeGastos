package com.example.proyectspring.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.proyectspring.entity.Ingresos;
import com.example.proyectspring.entity.Usuario;

@Repository
public interface IIngresosDao extends JpaRepository<Ingresos, Long> {
    
    List<Ingresos> findByUsuarioOrderByFechaDesc(Usuario usuario);
    
    List<Ingresos> findByUsuario(Usuario usuario);
} 
    

