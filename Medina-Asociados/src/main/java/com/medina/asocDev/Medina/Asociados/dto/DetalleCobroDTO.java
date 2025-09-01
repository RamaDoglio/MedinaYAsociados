package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetalleCobroDTO {

    private Long idDetalleCobro;
    private CobroDTO cobro;
    private LocalDateTime fecha;
    private String descripcionCobro;
    private Float subTotal;
    private TipoCobroDTO tipoCobro;

}
