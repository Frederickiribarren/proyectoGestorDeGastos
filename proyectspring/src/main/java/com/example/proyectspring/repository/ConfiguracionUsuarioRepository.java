package com.example.proyectspring.repository;

import com.example.proyectspring.entity.ConfiguracionUsuario;
import com.example.proyectspring.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionUsuarioRepository extends JpaRepository<ConfiguracionUsuario, Long> {
    Optional<ConfiguracionUsuario> findByUsuario(Usuario usuario);
    Optional<ConfiguracionUsuario> findByUsuarioId(Long usuarioId);
    boolean existsByUsuario(Usuario usuario);
}
