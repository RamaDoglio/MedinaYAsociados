package com.medina.asocDev.Medina.Asociados.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogInRequest {

    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
}
