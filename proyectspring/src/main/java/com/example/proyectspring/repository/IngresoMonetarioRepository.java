package com.example.proyectspring.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.proyectspring.entity.IngresoMonetario;
import com.example.proyectspring.entity.Usuario;

@Repository
public interface IngresoMonetarioRepository extends JpaRepository<IngresoMonetario, Long> {
    
    List<IngresoMonetario> findByUsuarioOrderByFechaDesc(Usuario usuario);
    
    List<IngresoMonetario> findByUsuarioAndTipoIngresoOrderByFechaDesc(Usuario usuario, String tipoIngreso);
    
    List<IngresoMonetario> findByUsuarioAndFechaBetweenOrderByFechaDesc(Usuario usuario, LocalDate fechaInicio, LocalDate fechaFin);
    
    void deleteByUsuario(Usuario usuario);
}
