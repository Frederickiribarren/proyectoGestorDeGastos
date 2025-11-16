package com.example.proyectspring.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.proyectspring.entity.ConfiguracionTarjeta;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.repository.ConfiguracionTarjetaRepository;

@Service
public class ConfiguracionTarjetaService {
    
    @Autowired
    private ConfiguracionTarjetaRepository configuracionTarjetaRepository;
    
    public List<ConfiguracionTarjeta> obtenerConfiguracionesPorUsuario(Usuario usuario) {
        return configuracionTarjetaRepository.findByUsuario(usuario);
    }
    
    public Optional<ConfiguracionTarjeta> obtenerConfiguracionPorUsuarioYTarjeta(Usuario usuario, String nombreTarjeta) {
        return configuracionTarjetaRepository.findByUsuarioAndNombreTarjeta(usuario, nombreTarjeta);
    }
    
    @Transactional
    public ConfiguracionTarjeta guardarConfiguracion(ConfiguracionTarjeta configuracion) {
        configuracion.setFechaActualizacion(LocalDateTime.now());
        return configuracionTarjetaRepository.save(configuracion);
    }
    
    @Transactional
    public ConfiguracionTarjeta guardarOActualizarConfiguracion(Usuario usuario, String nombreTarjeta, Integer diaCorte, Integer diaPago) {
        Optional<ConfiguracionTarjeta> existente = obtenerConfiguracionPorUsuarioYTarjeta(usuario, nombreTarjeta);
        
        ConfiguracionTarjeta configuracion;
        if (existente.isPresent()) {
            configuracion = existente.get();
        } else {
            configuracion = new ConfiguracionTarjeta();
            configuracion.setUsuario(usuario);
            configuracion.setNombreTarjeta(nombreTarjeta);
        }
        
        configuracion.setDiaCorte(diaCorte);
        configuracion.setDiaPago(diaPago);
        
        return guardarConfiguracion(configuracion);
    }
}
