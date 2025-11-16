package com.example.proyectspring.dto;

import java.time.LocalDate;

public class IngresoDTO {
    private Long id;
    private Double monto;
    private String descripcion;
    private String categoria;
    private String metodoPago;
    private LocalDate fecha;
    private Integer numeroCuotas;

    public IngresoDTO() {
    }

    public IngresoDTO(Double monto, String descripcion, String categoria, String metodoPago, 
                      LocalDate fecha) {
        this.monto = monto;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.metodoPago = metodoPago;
        this.fecha = fecha;
        this.numeroCuotas = 1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getNumeroCuotas() {
        return numeroCuotas;
    }

    public void setNumeroCuotas(Integer numeroCuotas) {
        this.numeroCuotas = numeroCuotas;
    }
}
