package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TurnoDTO {

    private Long idTurno;
    private EstadoDTO estadoActual;
    private EspecialidadDTO especialidad;
    private CobroDTO cobro;
    private String observacionesCliente;
    private String observacionesAbogado;
    private HorarioTurnoDTO horarioTurno;
    private UsuarioDTO abogadoTurno;
    private UsuarioDTO usuarioTurno;
    private List<HistorialTurnoDTO> historialTurno= new ArrayList<>();

}
