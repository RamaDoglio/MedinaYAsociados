package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TurnoConHistorialDTO {

    private Long idTurno;
    private Long idCliente;
    private String nombreCliente;
    private Long idAbogado;
    private String nombreAbogado;
    private Long idEspecialidad;
    private String nombreEspecialidad;
    private String observacionesCliente;
    private String observacionesAbogado;
    private LocalDateTime horarioTurno;
    private Long idEstado;
    private String nombreEstado;
    private List<HistorialConEstadoDTO> historial;

}