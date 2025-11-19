package com.example.proyectspring.scheduler;

import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.proyectspring.dao.IUsuarioDao;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.service.NotificacionService;

@Component
public class ReporteMensualScheduler {

    @Autowired
    private NotificacionService notificacionService;
    
    @Autowired
    private IUsuarioDao usuarioDao;

    /**
     * Se ejecuta el día 1 de cada mes a las 8:00 AM
     * Cron: segundo minuto hora día mes día-semana
     * 0 0 8 1 * * = a las 8:00 AM el día 1 de cada mes
     */
    @Scheduled(cron = "0 0 8 1 * *")
    public void enviarReportesMensuales() {
        System.out.println("Iniciando envío de reportes mensuales...");
        
        // Obtener el mes anterior
        YearMonth mesAnterior = YearMonth.now().minusMonths(1);
        
        // Obtener todos los usuarios
        List<Usuario> usuarios = usuarioDao.findAll();
        
        int enviados = 0;
        for (Usuario usuario : usuarios) {
            try {
                notificacionService.enviarReporteMensual(usuario, mesAnterior);
                enviados++;
            } catch (Exception e) {
                System.err.println("Error al enviar reporte a " + usuario.getEmail() + ": " + e.getMessage());
            }
        }
        
        System.out.println("Reportes mensuales enviados: " + enviados + " de " + usuarios.size());
    }
}
