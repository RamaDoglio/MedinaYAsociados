package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)

public class EstadoDTO {
    private Long idEstado;
    private String ambito;
    private String nombreEstado;
}
