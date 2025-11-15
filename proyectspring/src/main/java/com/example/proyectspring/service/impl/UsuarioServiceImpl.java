package com.example.proyectspring.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.proyectspring.dao.IUsuarioDao;
import com.example.proyectspring.dto.PerfilDTO;
import com.example.proyectspring.dto.RegistroDTO;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    
    private final IUsuarioDao usuarioDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioServiceImpl(IUsuarioDao usuarioDao, PasswordEncoder passwordEncoder) {
        this.usuarioDao = usuarioDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Usuario saveUsuario(Usuario usuario) {
        return usuarioDao.save(usuario);
    }

    @Override
    @Transactional
    public Usuario registrarUsuario(RegistroDTO registroDTO) {
        // Validar que el email no exista
        if (existeEmail(registroDTO.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Validar que las contraseñas coincidan
        if (!registroDTO.getPassword().equals(registroDTO.getConfirmPassword())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        // Validar términos y condiciones
        if (!registroDTO.isAceptaTerminos()) {
            throw new RuntimeException("Debes aceptar los términos y condiciones");
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(registroDTO.getNombre());
        usuario.setEmail(registroDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword())); // Encriptar contraseña
        usuario.setRol("USER"); // Rol por defecto
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);

        return usuarioDao.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmail(String email) {
        return usuarioDao.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeEmail(String email) {
        return usuarioDao.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioDao.findById(id);
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        usuarioDao.deleteById(id);
    }

    @Override
    @Transactional
    public Usuario actualizarPerfil(Long id, PerfilDTO perfilDTO) {
        Usuario usuario = usuarioDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Actualizar solo los campos proporcionados
        if (perfilDTO.getNombre() != null && !perfilDTO.getNombre().isEmpty()) {
            usuario.setNombre(perfilDTO.getNombre());
        }
        if (perfilDTO.getApellido() != null && !perfilDTO.getApellido().isEmpty()) {
            usuario.setApellido(perfilDTO.getApellido());
        }
        if (perfilDTO.getTelefono() != null) {
            usuario.setTelefono(perfilDTO.getTelefono());
        }
        if (perfilDTO.getPais() != null) {
            usuario.setPais(perfilDTO.getPais());
        }
        if (perfilDTO.getBiografia() != null) {
            usuario.setBiografia(perfilDTO.getBiografia());
        }
        if (perfilDTO.getFechaNacimiento() != null) {
            usuario.setFechaNacimiento(perfilDTO.getFechaNacimiento());
        }
        
        return usuarioDao.save(usuario);
    }

    // Métodos legacy para compatibilidad
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return usuarioDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario findOne(Long id) {
        return usuarioDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        usuarioDao.deleteById(id);
    }
}