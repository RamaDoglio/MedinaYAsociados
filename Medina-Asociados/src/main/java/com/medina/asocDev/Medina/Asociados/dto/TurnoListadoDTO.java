package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TurnoListadoDTO {
    private Long idTurno;
    private String persona; // puede ser cliente o abogado
    private LocalDateTime fechaHora;
    private String estado;
}
