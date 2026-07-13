package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CobroDTO {

    private Long idCobro;
    private Long idTurno;
    private Float importeTotal;
    private Long idEstado;

}
