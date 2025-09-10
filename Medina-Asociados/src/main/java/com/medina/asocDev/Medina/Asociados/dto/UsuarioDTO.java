package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioDTO{
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String dni;
    private DireccionDTO direccion;
    private String telefono;
    private String email;
    private String password;
    private RolDTO rol;
    private List<TurnoDTO> turnos; // Agregado para reflejar la relación con Turno
}