package com.medina.asocDev.Medina.Asociados.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.medina.asocDev.Medina.Asociados.entity.Usuario;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private int statusCode;
    private String message;

    private String token;
    private String role;
    private String expirationTime;

    private UsuarioDTO user;
    private TurnoDTO booking;
    private Object data;

    public void setData(Object data) {
        this.data = data;
    }
}