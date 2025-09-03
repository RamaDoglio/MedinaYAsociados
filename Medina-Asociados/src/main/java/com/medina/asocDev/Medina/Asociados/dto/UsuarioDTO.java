import java.util.List;

import com.medina.asocDev.Medina.Asociados.entity.Abogado;

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
    private AbogadoDTO abogado;
}