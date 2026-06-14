package com.medina.asocDev.Medina.Asociados.config;

import com.medina.asocDev.Medina.Asociados.entity.Especialidad;
import com.medina.asocDev.Medina.Asociados.entity.Estado;
import com.medina.asocDev.Medina.Asociados.entity.Rol;
import com.medina.asocDev.Medina.Asociados.entity.Turno;
import com.medina.asocDev.Medina.Asociados.entity.Usuario;
import com.medina.asocDev.Medina.Asociados.repo.EspecialidadRepository;
import com.medina.asocDev.Medina.Asociados.repo.EstadoRepository;
import com.medina.asocDev.Medina.Asociados.repo.RolRepository;
import com.medina.asocDev.Medina.Asociados.repo.TurnoRepository;
import com.medina.asocDev.Medina.Asociados.repo.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EstadoRepository estadoRepository;
    private final EspecialidadRepository especialidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final TurnoRepository turnoRepository;
    private final RolRepository rolRepository;

    public DataInitializer(
            EstadoRepository estadoRepository,
            EspecialidadRepository especialidadRepository,
            UsuarioRepository usuarioRepository,
            TurnoRepository turnoRepository,
            RolRepository rolRepository
    ) {
        this.estadoRepository = estadoRepository;
        this.especialidadRepository = especialidadRepository;
        this.usuarioRepository = usuarioRepository;
        this.turnoRepository = turnoRepository;
        this.rolRepository = rolRepository;
    }

    @Override
    public void run(String... args) {
        if (turnoRepository.count() > 0) {
            return;
        }

        Estado finalizado = getOrCreateEstado("FINALIZADO", "TURNO");
        Estado canceladoSinReembolso = getOrCreateEstado("CANCELADO_SIN_REEMBOLSO", "TURNO");
        Estado noAsistio = getOrCreateEstado("NO_ASISTIO", "TURNO");

        Especialidad laboral = getOrCreateEspecialidad("Derecho Laboral", "Consultas de trabajo");
        Especialidad penal = getOrCreateEspecialidad("Derecho Penal", "Defensa criminal");

        Rol rolAbogado = getOrCreateRol("ABOGADO", "Rol para profesionales del estudio");
        Rol rolCliente = getOrCreateRol("CLIENTE", "Rol para clientes");

        Usuario abogado = getOrCreateUsuario(
                "Dr. Medina",
                "Lopez",
                "20111222",
                "+5491160010001",
                "abogado.demo@medina.local",
                rolAbogado
        );

        Usuario cliente = getOrCreateUsuario(
                "Juan",
                "Perez",
                "30999888",
                "+5491160010002",
                "cliente.demo@medina.local",
                rolCliente
        );

        turnoRepository.save(crearTurno(finalizado, laboral, abogado, cliente, LocalDateTime.now().minusDays(1)));
        turnoRepository.save(crearTurno(canceladoSinReembolso, laboral, abogado, cliente, LocalDateTime.now().minusDays(2)));
        turnoRepository.save(crearTurno(noAsistio, penal, abogado, cliente, LocalDateTime.now().minusDays(3)));
    }

    private Estado getOrCreateEstado(String nombreEstado, String ambito) {
        return estadoRepository
                .findByNombreAndAmbito(nombreEstado, ambito)
                .orElseGet(() -> {
                    Estado estado = new Estado();
                    estado.setNombreEstado(nombreEstado);
                    estado.setAmbito(ambito);
                    return estadoRepository.save(estado);
                });
    }

    private Especialidad getOrCreateEspecialidad(String nombre, String descripcion) {
        return especialidadRepository
                .findByNombreEspecialidad(nombre)
                .orElseGet(() -> {
                    Especialidad especialidad = new Especialidad();
                    especialidad.setNombreEspecialidad(nombre);
                    especialidad.setDescripcionEspecialidad(descripcion);
                    return especialidadRepository.save(especialidad);
                });
    }

    private Rol getOrCreateRol(String nombre, String descripcion) {
        return rolRepository
                .findByNombre(nombre)
                .orElseGet(() -> {
                    Rol rol = new Rol();
                    rol.setNombre(nombre);
                    rol.setDescripcion(descripcion);
                    return rolRepository.save(rol);
                });
    }

    private Usuario getOrCreateUsuario(
            String nombre,
            String apellido,
            String dni,
            String telefono,
            String email,
            Rol rol
    ) {
        return usuarioRepository
                .findByEmail(email)
                .orElseGet(() -> {
                    Usuario usuario = new Usuario();
                    usuario.setNombre(nombre);
                    usuario.setApellido(apellido);
                    usuario.setDni(dni);
                    usuario.setTelefono(telefono);
                    usuario.setEmail(email);
                    usuario.setRolesUsuario((List<Rol>) rol);
                    usuario.setPassword("demo");
                    return usuarioRepository.save(usuario);
                });
    }

    private Turno crearTurno(
            Estado estado,
            Especialidad especialidad,
            Usuario abogado,
            Usuario cliente,
            LocalDateTime fecha
    ) {
        Turno turno = new Turno();
        turno.setEstadoActual(estado);
        turno.setEspecialidad(especialidad);
        turno.setAbogadoTurno(abogado);
        turno.setClienteTurno(cliente);
        turno.setHorarioTurno(fecha);
        return turno;
    }
}