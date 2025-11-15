package com.example.proyectspring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SeguridadDTO {
    
    @NotBlank(message = "La contraseña actual es obligatoria")
    private String passwordActual;
    
    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String nuevaPassword;
    
    @NotBlank(message = "Confirma tu nueva contraseña")
    private String confirmarPassword;
    
    // Constructores
    public SeguridadDTO() {}
    
    public SeguridadDTO(String passwordActual, String nuevaPassword, String confirmarPassword) {
        this.passwordActual = passwordActual;
        this.nuevaPassword = nuevaPassword;
        this.confirmarPassword = confirmarPassword;
    }
    
    // Getters y Setters
    public String getPasswordActual() {
        return passwordActual;
    }
    
    public void setPasswordActual(String passwordActual) {
        this.passwordActual = passwordActual;
    }
    
    public String getNuevaPassword() {
        return nuevaPassword;
    }
    
    public void setNuevaPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }
    
    public String getConfirmarPassword() {
        return confirmarPassword;
    }
    
    public void setConfirmarPassword(String confirmarPassword) {
        this.confirmarPassword = confirmarPassword;
    }
}
