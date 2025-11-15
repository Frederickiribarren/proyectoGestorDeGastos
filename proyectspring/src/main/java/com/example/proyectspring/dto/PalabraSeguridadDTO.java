package com.example.proyectspring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PalabraSeguridadDTO {
    
    @NotBlank(message = "La palabra de seguridad es obligatoria")
    @Size(min = 4, max = 50, message = "La palabra de seguridad debe tener entre 4 y 50 caracteres")
    private String palabraSeguridad;
    
    // Constructor vacío
    public PalabraSeguridadDTO() {}
    
    public PalabraSeguridadDTO(String palabraSeguridad) {
        this.palabraSeguridad = palabraSeguridad;
    }
    
    // Getters y Setters
    public String getPalabraSeguridad() {
        return palabraSeguridad;
    }
    
    public void setPalabraSeguridad(String palabraSeguridad) {
        this.palabraSeguridad = palabraSeguridad;
    }
}
