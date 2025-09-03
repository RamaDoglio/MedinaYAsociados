package com.medina.asocDev.Medina.Asociados.dto;

import lombok.Data;
import java.util.List;

@Data
public class UsuarioDTO{
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private Integer dni;
    private DireccionDTO direccion;
    private Integer telefono;
    private String email;
    private String password;
    private RolDTO rol;
    private List<TurnoDTO> turnos; // Agregado para reflejar la relación con Turno
    private AbogadoDTO abogado; // Esto no iria ya que el dato lo tendria la clase abogado
}