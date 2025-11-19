package com.example.proyectspring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "configuracion_notificaciones")
public class ConfiguracionNotificaciones {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;
    
    @Column(name = "notificaciones_email", nullable = false)
    private boolean notificacionesEmail = true;
    
    @Column(name = "alertas_transacciones", nullable = false)
    private boolean alertasTransacciones = true;
    
    @Column(name = "alertas_presupuesto", nullable = false)
    private boolean alertasPresupuesto = false;
    
    @Column(name = "reportes_mensuales", nullable = false)
    private boolean reportesMensuales = false;
    
    @Column(name = "promociones", nullable = false)
    private boolean promociones = false;
    
    public ConfiguracionNotificaciones(Usuario usuario) {
        this.usuario = usuario;
        this.notificacionesEmail = true;
        this.alertasTransacciones = true;
        this.alertasPresupuesto = false;
        this.reportesMensuales = false;
        this.promociones = false;
    }
}
