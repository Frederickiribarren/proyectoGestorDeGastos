package com.example.proyectspring.dto;

import java.time.LocalDate;

public class IngresoDTO {
    private Double monto;
    private String descripcion;
    private String categoria;
    private String metodoPago;
    private String notaAdicional;
    private LocalDate fecha;

    public IngresoDTO() {
    }

    public IngresoDTO(Double monto, String descripcion, String categoria, String metodoPago, 
                      String notaAdicional, LocalDate fecha) {
        this.monto = monto;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.metodoPago = metodoPago;
        this.notaAdicional = notaAdicional;
        this.fecha = fecha;
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

    public String getNotaAdicional() {
        return notaAdicional;
    }

    public void setNotaAdicional(String notaAdicional) {
        this.notaAdicional = notaAdicional;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
