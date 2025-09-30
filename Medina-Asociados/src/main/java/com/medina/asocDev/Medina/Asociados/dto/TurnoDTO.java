package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TurnoDTO {

    private Long idTurno;
    private Long idEstado;
    private Long idEspecialidad;
    private Long idCobro;
    private String observacionesCliente;
    private String observacionesAbogado;
    private Long idHorarioTurno;
    private Long abogadoTurno;
    private Long usuarioTurno;
    private List<Long> historialTurno;

}
