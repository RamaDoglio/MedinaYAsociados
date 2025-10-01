package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DireccionDTO {

    private Long idDireccion;
    private String calle;
    private Integer numeroCalle;
    private String dpto;
    private String piso;
    @NotNull(message = "Debe seleccionar una localidad")
    private Long localidad;
    private String provincia="Córdoba";
}
