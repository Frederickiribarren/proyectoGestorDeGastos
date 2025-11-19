package com.example.proyectspring.service;

import com.example.proyectspring.entity.ConfiguracionUsuario;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.repository.ConfiguracionUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ConfiguracionUsuarioService {
    
    @Autowired
    private ConfiguracionUsuarioRepository configuracionRepository;
    
    /**
     * Obtiene la configuración de un usuario, si no existe la crea con valores por defecto
     */
    @Transactional
    public ConfiguracionUsuario obtenerOCrearConfiguracion(Usuario usuario) {
        Optional<ConfiguracionUsuario> configOpt = configuracionRepository.findByUsuario(usuario);
        
        if (configOpt.isPresent()) {
            return configOpt.get();
        }
        
        // Crear nueva configuración con valores por defecto
        ConfiguracionUsuario nuevaConfig = new ConfiguracionUsuario(usuario);
        return configuracionRepository.save(nuevaConfig);
    }
    
    /**
     * Guarda o actualiza la configuración del usuario
     */
    @Transactional
    public ConfiguracionUsuario guardarConfiguracion(ConfiguracionUsuario configuracion) {
        return configuracionRepository.save(configuracion);
    }
    
    /**
     * Actualiza solo las preferencias de apariencia
     */
    @Transactional
    public ConfiguracionUsuario actualizarApariencia(Usuario usuario, String tema, String idioma, String moneda) {
        ConfiguracionUsuario config = obtenerOCrearConfiguracion(usuario);
        config.setTema(tema);
        config.setIdioma(idioma);
        config.setMoneda(moneda);
        return configuracionRepository.save(config);
    }
    
    /**
     * Actualiza las preferencias de notificaciones
     */
    @Transactional
    public ConfiguracionUsuario actualizarNotificaciones(Usuario usuario, 
            boolean notifEmail, boolean notifTransacciones, boolean notifPresupuesto,
            boolean notifReportes, boolean notifPromociones) {
        ConfiguracionUsuario config = obtenerOCrearConfiguracion(usuario);
        config.setNotifEmail(notifEmail);
        config.setNotifTransacciones(notifTransacciones);
        config.setNotifPresupuesto(notifPresupuesto);
        config.setNotifReportes(notifReportes);
        config.setNotifPromociones(notifPromociones);
        return configuracionRepository.save(config);
    }
    
    /**
     * Activa o desactiva la autenticación de dos factores
     */
    @Transactional
    public ConfiguracionUsuario actualizarAutenticacionDosFactores(Usuario usuario, boolean activar) {
        ConfiguracionUsuario config = obtenerOCrearConfiguracion(usuario);
        config.setAutenticacionDosFactores(activar);
        return configuracionRepository.save(config);
    }
    
    /**
     * Obtiene la configuración por ID de usuario
     */
    public Optional<ConfiguracionUsuario> obtenerPorUsuarioId(Long usuarioId) {
        return configuracionRepository.findByUsuarioId(usuarioId);
    }
}
