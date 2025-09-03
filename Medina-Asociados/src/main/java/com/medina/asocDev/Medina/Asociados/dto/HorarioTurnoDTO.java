package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

import org.hibernate.grammars.hql.HqlParser.HourContext;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HorarioTurnoDTO {

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private EstadoDTO estadoHorario;

}
