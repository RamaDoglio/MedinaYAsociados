package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CobroConDetallesDTO {

    private Long idCobro;
    private Long idTurno;
    private Float importeTotal;
    private Long idEstado;
    private String nombreEstado;
    private String ambitoEstado;
    private List<DetalleCobroConTipoDTO> detalles;

}