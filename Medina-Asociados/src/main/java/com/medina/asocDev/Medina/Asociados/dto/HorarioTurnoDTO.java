package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.medina.asocDev.Medina.Asociados.entity.Turno;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HorarioTurnoDTO {

    private Long idHorarioTurno;
    private Long idTurno;
    private LocalDateTime fechaHoraInicio;
    private Long idEstado;

}
