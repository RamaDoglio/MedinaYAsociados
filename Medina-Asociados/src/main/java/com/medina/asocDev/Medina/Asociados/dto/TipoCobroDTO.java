package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TipoCobroDTO {

    private Long idTipoCobro;
    private String nombreTipoCobro;
    private String descripcionTipoCobro;

}
