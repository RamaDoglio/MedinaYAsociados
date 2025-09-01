package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DireccionDTO {

    private String calle;
    private Integer numeroCalle;
    private String dpto;
    private String piso;
    private LocalidadDTO localidad;
    private String provincia="Córdoba";

}
