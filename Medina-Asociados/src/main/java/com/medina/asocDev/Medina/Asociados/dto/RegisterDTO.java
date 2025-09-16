package com.medina.asocDev.Medina.Asociados.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterDTO {
    private String nombre;
    private String apellido;
    private String dni;
    private DireccionDTO direccion;
    private String telefono;
    private String email;
    private String password;
    private RolDTO rol;;
}