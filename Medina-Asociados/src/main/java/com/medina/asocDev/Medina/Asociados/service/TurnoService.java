package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.TurnoCreateRequest;
import com.medina.asocDev.Medina.Asociados.dto.TurnoDTO;
import com.medina.asocDev.Medina.Asociados.entity.*;
import com.medina.asocDev.Medina.Asociados.repo.*;
import com.medina.asocDev.Medina.Asociados.utils.TurnoProperties;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import jakarta.transaction.Transactional;
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

    @Autowired
    private NotificacionTurnoService notificacionTurnoService;

    @Autowired
    private TurnoProperties turnoProperties;

    //Crear turno (reserva)
    @Transactional
    public Turno crearTurno(TurnoCreateRequest turnoDTO) {
        // 1. Buscar cliente y abogado
        Usuario cliente = usuarioRepository.findById(turnoDTO.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Usuario abogado = usuarioRepository.findById(turnoDTO.getIdAbogado())
                .orElseThrow(() -> new RuntimeException("Abogado no encontrado"));

        // 2. Validar disponibilidad
        LocalDateTime fechaHoraTurno = turnoDTO.getHorarioTurno();
        if (!abogadoService.verificarDisponibilidad(turnoDTO.getIdAbogado(), fechaHoraTurno)) {
            throw new RuntimeException("El horario seleccionado no está disponible");
        }

        // 3. Validar que no sea sábado o domingo
        Utils.validarDiaHabil(fechaHoraTurno);

        // 4. Buscar especialidad
        Especialidad especialidad = especialidadRepository.findById(turnoDTO.getIdEspecialidad())
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));

        // 5. Crear cobro (importe desde config, no desde el front)
        Cobro cobro = new Cobro();
        cobro.setImporteTotal(turnoProperties.getPrecioBase());

        Estado estadoCobro = estadoRepository
                .findByNombreAndAmbito("PENDIENTE", "COBRO")
                .orElseThrow(() -> new RuntimeException("Estado de cobro no encontrado"));
        cobro.setEstadoCobro(estadoCobro);

        // 6. Estado inicial del turno
        Estado estadoTurno = estadoRepository
                .findByNombreAndAmbito("RESERVADO", "TURNO")
                .orElseThrow(() -> new RuntimeException("Estado RESERVADO no encontrado"));

        // 7. Crear turno
        Turno turno = Turno.builder()
                .clienteTurno(cliente)
                .abogadoTurno(abogado)
                .especialidad(especialidad)
                .cobro(cobro)
                .estadoActual(estadoTurno)
                .horarioTurno(fechaHoraTurno)
                .observacionesCliente(turnoDTO.getObservacionesCliente())
                .build();

        Turno guardado = turnoRepository.save(turno);

        // 8. Registrar en historial (estado inicial RESERVADO)
        historialTurnoService.registrarCambio(guardado, null, estadoTurno);

        return guardado;
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
    @Transactional
    public TurnoDTO reprogramarTurno(Long id, LocalDateTime nuevaFechaHora) {
        Turno turno = obtenerPorId(id);

        String estado = turno.getEstadoActual().getNombreEstado();
        if (!List.of("PAGADO", "REPROGRAMADO").contains(estado)) {
            throw new IllegalArgumentException("No se puede reprogramar un turno que no esté pagado o reprogramado");
        }
        if (turno.getHorarioTurno().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new IllegalArgumentException("No se puede reprogramar con menos de 24 horas de anticipación");
        }
        if (nuevaFechaHora.isBefore(LocalDateTime.now().plusHours(24))) {
            throw new IllegalArgumentException("La nueva fecha debe tener al menos 24 horas de antelación");
        }
        if (!abogadoService.verificarDisponibilidad(turno.getAbogadoTurno().getIdUsuario(), nuevaFechaHora)) {
            throw new RuntimeException("El nuevo horario no está disponible");
        }

        Estado anterior = turno.getEstadoActual();
        turno.setHorarioTurno(nuevaFechaHora);

        Estado reprogramado = estadoRepository.findByNombreAndAmbito("REPROGRAMADO", "TURNO")
                .orElseThrow(() -> new RuntimeException("Estado REPROGRAMADO no encontrado"));
        turno.setEstadoActual(reprogramado);

        historialTurnoService.registrarCambio(turno, anterior, reprogramado);

        Turno actualizado = turnoRepository.save(turno);

        // 👇 Notificación
        try {
            notificacionTurnoService.enviarReprogramacion(actualizado);
        } catch (Exception e) {
            // loguear error, no romper la transacción
            e.printStackTrace();
        }

        return Utils.mapTurnoEntityToDTO(actualizado);
    }

    //Cancelar turno
    @Transactional
    public TurnoDTO cancelarTurno(Long id) {
        Turno turno = obtenerPorId(id);
        String estado = turno.getEstadoActual().getNombreEstado();

        if (!List.of("PAGADO", "REPROGRAMADO").contains(estado)) {
            throw new IllegalStateException("Solo un turno pagado o reprogramado puede cancelarse");
        }

        LocalDateTime fechaTurno = turno.getHorarioTurno();
        Estado anterior = turno.getEstadoActual();

        if (fechaTurno.isBefore(LocalDateTime.now().plusHours(24))) {
            Estado sinReembolso = estadoRepository.findByNombreAndAmbito("CANCELADO_SIN_REEMBOLSO", "TURNO")
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
            turno.setEstadoActual(sinReembolso);
        } else {
            Estado conReembolso = estadoRepository.findByNombreAndAmbito("CANCELADO_CON_REEMBOLSO", "TURNO")
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
            turno.setEstadoActual(conReembolso);
            if (turno.getCobro() != null) {
                cobroService.reembolsar(turno.getCobro());
            }
        }

        historialTurnoService.registrarCambio(turno, anterior, turno.getEstadoActual());
        Turno actualizado = turnoRepository.save(turno);

        // 👇 Notificación
        try {
            notificacionTurnoService.enviarCancelacion(actualizado);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Utils.mapTurnoEntityToDTO(actualizado);
    }

    @Transactional
    public TurnoDTO pagarTurno(Long idTurno) {
        Turno turno = obtenerPorId(idTurno);

        Estado anterior = turno.getEstadoActual();
        Estado pagado = estadoRepository.findByNombreAndAmbito("PAGADO", "TURNO")
                .orElseThrow(() -> new RuntimeException("Estado PAGADO no encontrado"));

        turno.setEstadoActual(pagado);

        if (turno.getCobro() != null) {
            cobroService.marcarComoPagado(turno.getCobro());
        }

        historialTurnoService.registrarCambio(turno, anterior, pagado);

        Turno actualizado = turnoRepository.save(turno);

        // 👇 Notificación
        try {
            notificacionTurnoService.enviarConfirmacionReserva(actualizado);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Utils.mapTurnoEntityToDTO(actualizado);
    }

    @Transactional
    public TurnoDTO marcarEnCurso(Long idTurno) {
        Turno turno = obtenerPorId(idTurno);

        String estado = turno.getEstadoActual().getNombreEstado();
        if (!List.of("PAGADO", "REPROGRAMADO").contains(estado)) {
            throw new IllegalStateException("Solo un turno pagado o reprogramado puede pasar a EN_CURSO");
        }

        Estado enCurso = estadoRepository.findByNombreAndAmbito("EN_CURSO", "TURNO")
                .orElseThrow(() -> new RuntimeException("Estado EN_CURSO no encontrado"));

        Estado anterior = turno.getEstadoActual();
        turno.setEstadoActual(enCurso);

        historialTurnoService.registrarCambio(turno, anterior, enCurso);

        return Utils.mapTurnoEntityToDTO(turnoRepository.save(turno));
    }

    // Marcar no asistió
    @Transactional
    public TurnoDTO marcarNoAsistio(Long idTurno) {
        Turno turno = obtenerPorId(idTurno);

        if (!turno.getEstadoActual().getNombreEstado().equals("EN_CURSO")) {
            throw new IllegalStateException("Solo un turno en curso puede marcarse como NO_ASISTIO");
        }

        Estado noAsistio = estadoRepository.findByNombreAndAmbito("NO_ASISTIO", "TURNO")
                .orElseThrow(() -> new RuntimeException("Estado NO_ASISTIO no encontrado"));

        Estado anterior = turno.getEstadoActual();
        turno.setEstadoActual(noAsistio);

        historialTurnoService.registrarCambio(turno, anterior, noAsistio);

        return Utils.mapTurnoEntityToDTO(turnoRepository.save(turno));
    }

    // Finalizar turno
    @Transactional
    public TurnoDTO finalizarTurno(Long idTurno) {
        Turno turno = obtenerPorId(idTurno);

        if (!turno.getEstadoActual().getNombreEstado().equals("EN_CURSO")) {
            throw new IllegalStateException("Solo un turno en curso puede finalizarse");
        }

        Estado finalizado = estadoRepository.findByNombreAndAmbito("FINALIZADO", "TURNO")
                .orElseThrow(() -> new RuntimeException("Estado FINALIZADO no encontrado"));

        Estado anterior = turno.getEstadoActual();
        turno.setEstadoActual(finalizado);

        historialTurnoService.registrarCambio(turno, anterior, finalizado);

        return Utils.mapTurnoEntityToDTO(turnoRepository.save(turno));
    }
}