package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClienteOfflineRequest {
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String email;
    private DireccionDTO direccion;
}