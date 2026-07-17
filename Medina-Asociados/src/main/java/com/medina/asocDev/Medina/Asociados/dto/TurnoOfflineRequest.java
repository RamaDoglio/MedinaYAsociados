package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TurnoOfflineRequest {
    private Long idAbogado;
    private Long idEspecialidad;
    private LocalDateTime horarioTurno;
    private String observacionesCliente;
    private ClienteOfflineRequest cliente;
}
