package com.example.proyectspring.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.proyectspring.entity.ConfiguracionTarjeta;
import com.example.proyectspring.entity.Usuario;

@Repository
public interface ConfiguracionTarjetaRepository extends JpaRepository<ConfiguracionTarjeta, Long> {
    
    List<ConfiguracionTarjeta> findByUsuario(Usuario usuario);
    
    Optional<ConfiguracionTarjeta> findByUsuarioAndNombreTarjeta(Usuario usuario, String nombreTarjeta);
    
    void deleteByUsuario(Usuario usuario);
}
