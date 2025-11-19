package com.example.proyectspring.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "configuracion_usuario")
public class ConfiguracionUsuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true, nullable = false)
    private Usuario usuario;
    
    // Apariencia
    @Column(name = "tema")
    private String tema = "light"; // light, dark, auto
    
    @Column(name = "idioma")
    private String idioma = "es"; // es, en, pt, fr
    
    @Column(name = "moneda")
    private String moneda = "CLP"; // CLP, USD, EUR, etc.
    
    // Notificaciones
    @Column(name = "notif_email")
    private boolean notifEmail = true;
    
    @Column(name = "notif_transacciones")
    private boolean notifTransacciones = true;
    
    @Column(name = "notif_presupuesto")
    private boolean notifPresupuesto = false;
    
    @Column(name = "notif_reportes")
    private boolean notifReportes = false;
    
    @Column(name = "notif_promociones")
    private boolean notifPromociones = false;
    
    // Privacidad y Seguridad
    @Column(name = "autenticacion_dos_factores")
    private boolean autenticacionDosFactores = false;
    
    // Auditoría
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    // Constructores
    public ConfiguracionUsuario() {}
    
    public ConfiguracionUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public String getTema() {
        return tema;
    }
    
    public void setTema(String tema) {
        this.tema = tema;
    }
    
    public String getIdioma() {
        return idioma;
    }
    
    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }
    
    public String getMoneda() {
        return moneda;
    }
    
    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }
    
    public boolean isNotifEmail() {
        return notifEmail;
    }
    
    public void setNotifEmail(boolean notifEmail) {
        this.notifEmail = notifEmail;
    }
    
    public boolean isNotifTransacciones() {
        return notifTransacciones;
    }
    
    public void setNotifTransacciones(boolean notifTransacciones) {
        this.notifTransacciones = notifTransacciones;
    }
    
    public boolean isNotifPresupuesto() {
        return notifPresupuesto;
    }
    
    public void setNotifPresupuesto(boolean notifPresupuesto) {
        this.notifPresupuesto = notifPresupuesto;
    }
    
    public boolean isNotifReportes() {
        return notifReportes;
    }
    
    public void setNotifReportes(boolean notifReportes) {
        this.notifReportes = notifReportes;
    }
    
    public boolean isNotifPromociones() {
        return notifPromociones;
    }
    
    public void setNotifPromociones(boolean notifPromociones) {
        this.notifPromociones = notifPromociones;
    }
    
    public boolean isAutenticacionDosFactores() {
        return autenticacionDosFactores;
    }
    
    public void setAutenticacionDosFactores(boolean autenticacionDosFactores) {
        this.autenticacionDosFactores = autenticacionDosFactores;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
