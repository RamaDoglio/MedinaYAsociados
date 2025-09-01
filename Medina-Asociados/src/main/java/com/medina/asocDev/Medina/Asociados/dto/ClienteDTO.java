package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClienteDTO {

    private Long idCliente;
    private String nombre;
    private String apellido;
    private Integer DNI;
    private DireccionDTO direccion;
    private String telefono;
    private String email;
    private List<TurnoDTO> listaTurnos= new ArrayList<>();
}
