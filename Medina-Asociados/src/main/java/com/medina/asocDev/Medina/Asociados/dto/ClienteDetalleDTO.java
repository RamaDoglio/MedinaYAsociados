package com.medina.asocDev.Medina.Asociados.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClienteDetalleDTO {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String email;
    private DireccionDTO direccion;
    private LocalidadDTO localidad;
    private List<EstadisticaDTO> turnosPorEstado;
}
