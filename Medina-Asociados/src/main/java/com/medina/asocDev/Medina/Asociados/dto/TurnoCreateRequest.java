package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.medina.asocDev.Medina.Asociados.entity.Cobro;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TurnoCreateRequest {
    private Long idCliente;
    private Long idAbogado;
    private Long idEspecialidad;
    private LocalDateTime horarioTurno;
    private String observacionesCliente;
    private CobroDTO cobro;
}

