package com.example.proyectspring.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.proyectspring.dao.IUsuarioDao;
import com.example.proyectspring.dto.PerfilDTO;
import com.example.proyectspring.dto.RegistroDTO;
import com.example.proyectspring.dto.SeguridadDTO;
import com.example.proyectspring.dto.EliminarCuentaDTO;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    
    private final IUsuarioDao usuarioDao;
    private final PasswordEncoder passwordEncoder;

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

        // Formatear nombre: primera letra en mayúscula
        String nombreFormateado = capitalizarPrimeraLetra(registroDTO.getNombre());

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(nombreFormateado);
        usuario.setEmail(registroDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword())); // Encriptar contraseña
        usuario.setRol("USER"); // Rol por defecto
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);

        return usuarioDao.save(usuario);
    }
    
    /**
     * Capitaliza la primera letra de cada palabra en un texto
     * @param texto El texto a capitalizar
     * @return El texto con la primera letra de cada palabra en mayúscula
     */
    private String capitalizarPrimeraLetra(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return texto;
        }
        
        String[] palabras = texto.trim().split("\\s+");
        StringBuilder resultado = new StringBuilder();
        
        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                         .append(palabra.substring(1).toLowerCase())
                         .append(" ");
            }
        }
        
        return resultado.toString().trim();
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
            usuario.setNombre(capitalizarPrimeraLetra(perfilDTO.getNombre()));
        }
        if (perfilDTO.getApellido() != null && !perfilDTO.getApellido().isEmpty()) {
            usuario.setApellido(capitalizarPrimeraLetra(perfilDTO.getApellido()));
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

    // Métodos de seguridad
    @Override
    @Transactional
    public boolean cambiarPassword(Long id, SeguridadDTO seguridadDTO) {
        Usuario usuario = usuarioDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(seguridadDTO.getPasswordActual(), usuario.getPassword())) {
            return false;
        }
        
        // Verificar que las nuevas contraseñas coincidan
        if (!seguridadDTO.getNuevaPassword().equals(seguridadDTO.getConfirmarPassword())) {
            return false;
        }
        
        // Actualizar contraseña
        usuario.setPassword(passwordEncoder.encode(seguridadDTO.getNuevaPassword()));
        
        usuarioDao.save(usuario);
        return true;
    }
    
    @Override
    @Transactional
    public boolean configurarPalabraSeguridad(Long id, String palabraSeguridad) {
        Usuario usuario = usuarioDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setPalabraSeguridad(passwordEncoder.encode(palabraSeguridad));
        usuarioDao.save(usuario);
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean verificarPalabraSeguridad(Long id, String palabraSeguridad) {
        Usuario usuario = usuarioDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (usuario.getPalabraSeguridad() == null) {
            return false;
        }
        
        return passwordEncoder.matches(palabraSeguridad, usuario.getPalabraSeguridad());
    }
    
    @Override
    @Transactional
    public void eliminarCuentaPermanentemente(Long id, EliminarCuentaDTO eliminarCuentaDTO) throws Exception {
        Usuario usuario = usuarioDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Verificar confirmación
        if (!eliminarCuentaDTO.isConfirmarEliminacion()) {
            throw new Exception("Debe confirmar la eliminación de la cuenta");
        }
        
        // Verificar contraseña
        if (!passwordEncoder.matches(eliminarCuentaDTO.getPassword(), usuario.getPassword())) {
            throw new Exception("Contraseña incorrecta");
        }
        
        // Verificar palabra de seguridad
        if (usuario.getPalabraSeguridad() != null) {
            if (!passwordEncoder.matches(eliminarCuentaDTO.getPalabraSeguridad(), usuario.getPalabraSeguridad())) {
                throw new Exception("Palabra de seguridad incorrecta");
            }
        }
        
        // Eliminar permanentemente
        usuarioDao.deleteById(id);
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