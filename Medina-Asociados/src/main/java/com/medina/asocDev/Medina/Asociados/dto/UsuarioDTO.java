import java.util.List;

public class UsuarioDTO{
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String dni;
    private DireccionDTO direccion;
    private String telefono;
    private String email;
    private RolDTO rol;
    private String contraseña;
    private List<TurnoDTO> turnos; // Agregado para reflejar la relación con Turno

}