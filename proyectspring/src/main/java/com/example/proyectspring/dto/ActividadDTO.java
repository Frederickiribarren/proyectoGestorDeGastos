package com.example.proyectspring.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ActividadDTO {
    private String tipo; // "GASTO" o "INGRESO"
    private String descripcion;
    private LocalDate fecha;
    private LocalDateTime fechaCreacion; // Para ordenar por hora de creación
    private Double monto;
    private String categoria;
    private String metodoPago;
    private String tipoIngreso;
    private String origen;
    private Integer numeroCuotas;
    private Integer cuotaActual;
    
    // Constructor para gastos
    public ActividadDTO(String tipo, String descripcion, LocalDate fecha, LocalDateTime fechaCreacion,
                       Double monto, String categoria, String metodoPago, Integer numeroCuotas, Integer cuotaActual) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.fechaCreacion = fechaCreacion;
        this.monto = monto;
        this.categoria = categoria;
        this.metodoPago = metodoPago;
        this.numeroCuotas = numeroCuotas;
        this.cuotaActual = cuotaActual;
    }
    
    // Constructor para ingresos
    public ActividadDTO(String tipo, String descripcion, LocalDate fecha, LocalDateTime fechaCreacion,
                       Double monto, String tipoIngreso, String origen, String categoria) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.fechaCreacion = fechaCreacion;
        this.monto = monto;
        this.tipoIngreso = tipoIngreso;
        this.origen = origen;
        this.categoria = categoria;
    }
    
    // Getters y Setters
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public Double getMonto() {
        return monto;
    }
    
    public void setMonto(Double monto) {
        this.monto = monto;
    }
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    public String getMetodoPago() {
        return metodoPago;
    }
    
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    public String getTipoIngreso() {
        return tipoIngreso;
    }
    
    public void setTipoIngreso(String tipoIngreso) {
        this.tipoIngreso = tipoIngreso;
    }
    
    public String getOrigen() {
        return origen;
    }
    
    public void setOrigen(String origen) {
        this.origen = origen;
    }
    
    public Integer getNumeroCuotas() {
        return numeroCuotas;
    }
    
    public void setNumeroCuotas(Integer numeroCuotas) {
        this.numeroCuotas = numeroCuotas;
    }
    
    public Integer getCuotaActual() {
        return cuotaActual;
    }
    
    public void setCuotaActual(Integer cuotaActual) {
        this.cuotaActual = cuotaActual;
    }
}
