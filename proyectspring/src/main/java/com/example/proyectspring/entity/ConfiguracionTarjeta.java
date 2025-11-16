package com.example.proyectspring.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "configuracion_tarjetas")
public class ConfiguracionTarjeta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(name = "nombre_tarjeta", nullable = false)
    private String nombreTarjeta;
    
    @Column(name = "dia_corte")
    private Integer diaCorte;
    
    @Column(name = "dia_pago")
    private Integer diaPago;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Constructor vacío
    public ConfiguracionTarjeta() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
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
    
    public String getNombreTarjeta() {
        return nombreTarjeta;
    }
    
    public void setNombreTarjeta(String nombreTarjeta) {
        this.nombreTarjeta = nombreTarjeta;
    }
    
    public Integer getDiaCorte() {
        return diaCorte;
    }
    
    public void setDiaCorte(Integer diaCorte) {
        this.diaCorte = diaCorte;
    }
    
    public Integer getDiaPago() {
        return diaPago;
    }
    
    public void setDiaPago(Integer diaPago) {
        this.diaPago = diaPago;
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
