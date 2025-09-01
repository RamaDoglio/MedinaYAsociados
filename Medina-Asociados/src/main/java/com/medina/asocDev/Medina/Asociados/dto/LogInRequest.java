package com.medina.asocDev.Medina.Asociados.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogInRequest {

    @NotBlank(message = "El correo no debe estar en blanco")
    private String email;
    @NotBlank(message = "La contraseña no debe estar en blanco")
    private String password;

}
