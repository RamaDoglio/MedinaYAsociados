package com.medina.asocDev.Medina.Asociados.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MensajeResponse {
    private String mensaje;

    public MensajeResponse(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMessage() {
        return mensaje;
    }

    public void setMessage(String message) {
        this.mensaje = message;
    }
}
