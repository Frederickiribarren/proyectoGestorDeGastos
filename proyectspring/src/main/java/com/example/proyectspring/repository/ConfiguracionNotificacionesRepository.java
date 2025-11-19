package com.example.proyectspring.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.proyectspring.entity.ConfiguracionNotificaciones;

@Repository
public interface ConfiguracionNotificacionesRepository extends JpaRepository<ConfiguracionNotificaciones, Long> {
    Optional<ConfiguracionNotificaciones> findByUsuarioId(Long usuarioId);
}
