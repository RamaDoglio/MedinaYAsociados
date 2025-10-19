package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.TurnoCreateRequest;
import com.medina.asocDev.Medina.Asociados.entity.*;
import com.medina.asocDev.Medina.Asociados.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TurnoService {

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private AbogadoService abogadoService;

    @Autowired
    private CobroService cobroService;

    @Autowired
    private HistorialTurnoService historialTurnoService;

    //Crear turno (reserva)
    public Turno crearTurno(TurnoCreateRequest turnoDTO) {
        // Buscar cliente y abogado por ID
        Usuario cliente = usuarioRepository.findById(turnoDTO.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Usuario abogado = usuarioRepository.findById(turnoDTO.getIdAbogado())
                .orElseThrow(() -> new RuntimeException("Abogado no encontrado"));

        // Validar que el horario esté disponible
        LocalDateTime fechaHoraTurno = turnoDTO.getHorarioTurno();
        if (!(abogadoService.verificarDisponibilidad(turnoDTO.getIdAbogado(), turnoDTO.getHorarioTurno()))) {
            throw new RuntimeException("El horario seleccionado no está disponible");
        }

        // Buscar especialidad
        Especialidad especialidad = especialidadRepository.findById(turnoDTO.getIdEspecialidad())
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));

        // Crear cobro
        Cobro cobro = new Cobro();
        cobro.setImporteTotal(turnoDTO.getCobro().getImporteTotal());
        Estado estadoCobro = estadoRepository.findById(turnoDTO.getCobro().getIdEstado())
                .orElseThrow(() -> new RuntimeException("Estado de cobro no encontrado"));
        cobro.setEstadoCobro(estadoCobro);

        // Estado inicial del turno
        Estado estadoTurno = estadoRepository
                .findByNombreAndAmbito("RESERVADO", "TURNO")
                .orElseThrow(() -> new RuntimeException("Estado RESERVADO no encontrado"));

        // Crear turno
        Turno turno = new Turno();
        turno.setClienteTurno(cliente);
        turno.setAbogadoTurno(abogado);
        turno.setEspecialidad(especialidad);
        turno.setCobro(cobro);
        turno.setEstadoActual(estadoTurno);
        turno.setHorarioTurno(fechaHoraTurno);
        turno.setObservacionesCliente(turnoDTO.getObservacionesCliente());

        return turnoRepository.save(turno);
    }

    //Listar todos los turnos
    public List<Turno> listarTurnos() {
        return turnoRepository.findAll();
    }

    //Obtener turno por ID
    public Turno obtenerPorId(Long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
    }

    //Actualizar observaciones
    public Turno actualizarTurno(Long id, Turno datos) {
        Turno turno = obtenerPorId(id);
        turno.setObservacionesCliente(datos.getObservacionesCliente());
        turno.setObservacionesAbogado(datos.getObservacionesAbogado());
        return turnoRepository.save(turno);
    }

    //Eliminar turno
    public void eliminarTurno(Long id) {
        turnoRepository.deleteById(id);
    }

    //Reprogramar turno
    public Turno reprogramarTurno(Long id, LocalDateTime nuevaFechaHora) {
        Turno turno = obtenerPorId(id);

        if (turno.getHorarioTurno().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new IllegalArgumentException("No se puede reprogramar con menos de 24 horas de anticipación");
        }

        if (nuevaFechaHora.isBefore(LocalDateTime.now().plusHours(24))) {
            throw new IllegalArgumentException("La nueva fecha debe tener al menos 24 horas de antelación");
        }

        if (!(abogadoService.verificarDisponibilidad(turno.getAbogadoTurno().getIdUsuario(), nuevaFechaHora))) {
            throw new RuntimeException("El nuevo horario no está disponible");
        }

        turno.setHorarioTurno(nuevaFechaHora);
        Estado estadoReprogramado = estadoRepository.findByNombreAndAmbito("REPROGRAMADO", "TURNO")
                .orElseThrow(() -> new RuntimeException("Estado REPROGRAMADO no encontrado"));
        turno.setEstadoActual(estadoReprogramado);

        return turnoRepository.save(turno);
    }

    //Cancelar turno
    public Turno cancelarTurno(Long id) {
        Turno turno = obtenerPorId(id);
        LocalDateTime fechaTurno = turno.getHorarioTurno();

        Estado estadoAnterior = turno.getEstadoActual();

        if (fechaTurno.isBefore(LocalDateTime.now().plusHours(24))) {
            // 👉 Cancelación sin reembolso
            Estado estadoCanceladoSinReembolso = estadoRepository
                    .findByNombreAndAmbito("CANCELADO_SIN_REEMBOLSO", "TURNO")
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
            turno.setEstadoActual(estadoCanceladoSinReembolso);

        } else {
            // 👉 Cancelación con reembolso
            Estado estadoCanceladoConReembolso = estadoRepository
                    .findByNombreAndAmbito("CANCELADO_CON_REEMBOLSO", "TURNO")
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
            turno.setEstadoActual(estadoCanceladoConReembolso);

            if (turno.getCobro() != null) {
                // 👉 delegar al CobroService, que a su vez delega al DetalleCobroService
                cobroService.reembolsar(turno.getCobro());
            }
        }

        // 👉 registrar historial de cambio de estado
        historialTurnoService.registrarCambio(turno, estadoAnterior, turno.getEstadoActual());

        return turnoRepository.save(turno);
    }

    public Turno pagarTurno(Long idTurno) {
        Turno turno = obtenerPorId(idTurno);

        Estado estadoAnterior = turno.getEstadoActual();
        Estado estadoPagado = estadoRepository.findByNombreAndAmbito("PAGADO", "TURNO")
                .orElseThrow(() -> new RuntimeException("Estado PAGADO no encontrado"));

        turno.setEstadoActual(estadoPagado);

        // Actualizar cobro
        if (turno.getCobro() != null) {
            cobroService.marcarComoPagado(turno.getCobro());
        }

        // Registrar historial
        historialTurnoService.registrarCambio(turno, estadoAnterior, estadoPagado);

        return turnoRepository.save(turno);
    }
}