package com.example.proyectspring.dto;

import jakarta.validation.constraints.NotBlank;

public class EliminarCuentaDTO {
    
    // La palabra de seguridad es opcional, solo se valida si el usuario la tiene configurada
    private String palabraSeguridad;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    
    private boolean confirmarEliminacion = false;
    
    // Constructores
    public EliminarCuentaDTO() {}
    
    public EliminarCuentaDTO(String palabraSeguridad, String password, boolean confirmarEliminacion) {
        this.palabraSeguridad = palabraSeguridad;
        this.password = password;
        this.confirmarEliminacion = confirmarEliminacion;
    }
    
    // Getters y Setters
    public String getPalabraSeguridad() {
        return palabraSeguridad;
    }
    
    public void setPalabraSeguridad(String palabraSeguridad) {
        this.palabraSeguridad = palabraSeguridad;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean isConfirmarEliminacion() {
        return confirmarEliminacion;
    }
    
    public void setConfirmarEliminacion(boolean confirmarEliminacion) {
        this.confirmarEliminacion = confirmarEliminacion;
    }
}
