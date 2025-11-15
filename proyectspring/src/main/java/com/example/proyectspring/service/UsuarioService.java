package com.example.proyectspring.service;

import java.util.List;
import java.util.Optional;

import com.example.proyectspring.dto.PerfilDTO;
import com.example.proyectspring.dto.RegistroDTO;
import com.example.proyectspring.entity.Usuario;

public interface UsuarioService {
    
    Usuario saveUsuario(Usuario usuario);
    
    Usuario registrarUsuario(RegistroDTO registroDTO);
    
    Optional<Usuario> findByEmail(String email);
    
    boolean existeEmail(String email);
    
    List<Usuario> listarTodos();
    
    Optional<Usuario> buscarPorId(Long id);
    
    void eliminarUsuario(Long id);
    
    Usuario actualizarPerfil(Long id, PerfilDTO perfilDTO);
    
    // Métodos legacy para compatibilidad
    List<Usuario> findAll();
    Usuario findOne(Long id);
    void delete(Long id);
}
