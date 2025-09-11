package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbogadoDTO {

    private Long idAbogado;
    private UsuarioDTO usuario;
    private String matricula;
    private List<EspecialidadDTO> especialidadesAbogado= new ArrayList<>();

}
