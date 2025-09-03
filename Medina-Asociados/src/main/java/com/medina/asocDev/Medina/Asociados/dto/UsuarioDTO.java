package com.medina.asocDev.Medina.Asociados.dto;
import java.util.List;

public class UsuarioDTO{
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private Integer dni;
    private DireccionDTO direccion;
    private Integer telefono;
    private String email;
    private RolDTO rol;
    private List<TurnoDTO> turnos; // Agregado para reflejar la relación con Turno
    private AbogadoDTO abogado; // Esto no iria ya que el dato lo tendria la clase abogado
}