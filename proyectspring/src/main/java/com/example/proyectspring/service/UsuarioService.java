package com.example.proyectspring.service;

import java.util.List;
import java.util.Optional;

import com.example.proyectspring.dto.PerfilDTO;
import com.example.proyectspring.dto.RegistroDTO;
import com.example.proyectspring.dto.SeguridadDTO;
import com.example.proyectspring.dto.EliminarCuentaDTO;
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
    
    // Métodos de seguridad
    boolean cambiarPassword(Long id, SeguridadDTO seguridadDTO);
    
    boolean configurarPalabraSeguridad(Long id, String palabraSeguridad);
    
    boolean verificarPalabraSeguridad(Long id, String palabraSeguridad);
    
    void eliminarCuentaPermanentemente(Long id, EliminarCuentaDTO eliminarCuentaDTO) throws Exception;
    
    // Métodos legacy para compatibilidad
    List<Usuario> findAll();
    Usuario findOne(Long id);
    void delete(Long id);
}
