package com.example.proyectspring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionNotificacionesDTO {
    private boolean notificacionesEmail;
    private boolean alertasTransacciones;
    private boolean alertasPresupuesto;
    private boolean reportesMensuales;
    private boolean promociones;
}
