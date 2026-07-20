package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TurnoDetalleDTO {
    private Long idTurno;
    private String persona;       // Puede ser cliente o abogado, según el contexto
    private String dni;
    private String direccion;
    private String telefono;
    private String especialidad;
    private LocalDateTime fechaHora;
    private String observacionesCliente;
    private String observacionesAbogado;
    private String estado;
    private Long idAbogado;
}
