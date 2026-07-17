package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.*;
import com.medina.asocDev.Medina.Asociados.entity.*;
import com.medina.asocDev.Medina.Asociados.repo.*;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TurnoService {

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

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
    private EmailQueueService emailQueueService;

    @Autowired
    private MercadoPagoService mercadoPagoService;

    @Autowired
    private ParametroService parametroService;


    @Transactional
    public Turno crearTurno(TurnoCreateRequest request) {


        Usuario cliente = usuarioRepository.findById(request.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Usuario abogado = usuarioRepository.findById(request.getIdAbogado())
                .orElseThrow(() -> new RuntimeException("Abogado no encontrado"));

        Especialidad especialidad = especialidadRepository.findById(request.getIdEspecialidad())
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));


        Estado estadoReservado = estadoRepository.findByNombreAndAmbito("RESERVADO", "TURNO")
                .orElseThrow(() -> new RuntimeException("Estado RESERVADO no encontrado"));

        LocalDate fechaTurno = request.getHorarioTurno().toLocalDate();

        if (abogadoService.esFinDeSemana(fechaTurno)) {
            throw new RuntimeException("No se pueden reservar turnos los sábados ni domingos");
        }

        Cobro cobro = new Cobro();
        cobro.setImporteTotal(Float.valueOf(parametroService.getValor("PRECIO_TURNO")));
        Estado estadoCobro = estadoRepository
                .findByNombreAndAmbito("PENDIENTE", "COBRO")
                .orElseThrow(() -> new RuntimeException("Estado de cobro no encontrado"));
        cobro.setEstadoCobro(estadoCobro);


        Turno turno = new Turno();
        turno.setClienteTurno(cliente);
        turno.setAbogadoTurno(abogado);
        turno.setEspecialidad(especialidad);
        turno.setCobro(cobro);
        cobro.setTurno(turno);
        turno.setHorarioTurno(request.getHorarioTurno());
        turno.setObservacionesCliente(request.getObservacionesCliente());
        turno.setEstadoActual(estadoReservado);


        Turno turnoGuardado = turnoRepository.save(turno);


        historialTurnoService.registrarCambio(
                turnoGuardado,
                null,
                estadoReservado
        );

        return turnoGuardado;
    }

    @Transactional
    public TurnoDTO createTurnoOffline(TurnoOfflineRequest request) {

        Usuario cliente = usuarioService.getOrCreateUsuario(request.getCliente());


        TurnoCreateRequest createRequest = new TurnoCreateRequest();
        createRequest.setIdCliente(cliente.getIdUsuario());
        createRequest.setIdAbogado(request.getIdAbogado());
        createRequest.setIdEspecialidad(request.getIdEspecialidad());

        LocalDate fechaTurno = request.getHorarioTurno().toLocalDate();

        if (abogadoService.esFinDeSemana(fechaTurno)) {
            throw new RuntimeException("No se pueden reservar turnos los sábados ni domingos");
        }

        createRequest.setHorarioTurno(request.getHorarioTurno());
        createRequest.setObservacionesCliente(request.getObservacionesCliente());
        



        Turno turnoGuardado = crearTurno(createRequest);

        Estado pendienteCobro = estadoRepository.findByNombreAndAmbito("PENDIENTE_COBRO", "TURNO")
                .orElseThrow(() -> new RuntimeException("Estado PENDIENTE_COBRO no encontrado"));

        Estado anterior= turnoGuardado.getEstadoActual();

        turnoGuardado.setEstadoActual(pendienteCobro);

        historialTurnoService.registrarCambio(
                turnoGuardado, anterior,
                pendienteCobro
        );

        turnoRepository.save(turnoGuardado);
        return Utils.mapTurnoEntityToDTO(turnoGuardado);
    }


    public Page<Turno> listarTurnos(Pageable pageable) {
        return turnoRepository.findAll(pageable);
    }


    public Turno obtenerPorId(Long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
    }


    @Transactional
    public Turno actualizarTurno(Long id, Turno datos) {
        Turno turno = obtenerPorId(id);
        turno.setObservacionesCliente(datos.getObservacionesCliente());
        return turnoRepository.save(turno);
    }


    @Transactional
    public void eliminarTurno(Long id) {
        turnoRepository.deleteById(id);
    }


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

        // Notificación
        try {
            emailQueueService.enviarConDelay(() -> {
                try {
                    notificacionTurnoService.enviarReprogramacion(actualizado);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Utils.mapTurnoEntityToDTO(actualizado);
    }


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
                    .orElseThrow(() -> new RuntimeException("Estado CANCELADO_SIN_REEMBOLSO no encontrado"));
            turno.setEstadoActual(sinReembolso);

        } else {

            Estado conReembolso = estadoRepository.findByNombreAndAmbito("CANCELADO_CON_REEMBOLSO", "TURNO")
                    .orElseThrow(() -> new RuntimeException("Estado CANCELADO_CON_REEMBOLSO no encontrado"));
            turno.setEstadoActual(conReembolso);

            if (turno.getCobro() != null) {
                String estadoCobro = turno.getCobro().getEstadoCobro() != null
                        ? turno.getCobro().getEstadoCobro().getNombreEstado()
                        : null;


                if (!"REEMBOLSADO".equals(estadoCobro)) {

                    if (turno.getCobro().getPaymentId() != null) {
                        mercadoPagoService.reembolsarPago(turno.getCobro().getPaymentId());
                    }


                    cobroService.reembolsar(turno.getCobro());
                }
            }
        }


        historialTurnoService.registrarCambio(turno, anterior, turno.getEstadoActual());


        Turno actualizado = turnoRepository.save(turno);


        try {
            emailQueueService.enviarConDelay(() -> {
                try {
                    notificacionTurnoService.enviarCancelacion(actualizado);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Utils.mapTurnoEntityToDTO(actualizado);
    }

    @Transactional
    public String pagarTurno(Long idTurno) {
        Turno turno = obtenerPorId(idTurno);


        if (!"RESERVADO".equals(turno.getEstadoActual().getNombreEstado()) ||
                !"PENDIENTE".equals(turno.getCobro().getEstadoCobro().getNombreEstado())) {
            throw new IllegalStateException("El turno no está disponible para pagar");
        }

        try {

            String initPoint = mercadoPagoService.crearPreferencia(turno.getCobro(), turno);


            return initPoint;

        } catch (Exception e) {
            throw new RuntimeException("Error al generar preferencia de pago en Mercado Pago", e);
        }
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


    public Page<TurnoListadoDTO> listarTurnosPorCliente(Long idCliente, Pageable pageable) {
        return turnoRepository.findByClienteTurno_IdUsuario(idCliente, pageable)
                .map(Utils::mapTurnoToListadoDTOParaCliente);
    }


    public Page<TurnoListadoDTO> listarTurnosPorAbogado(Long idAbogado,
                                                        LocalDate fechaDesde,
                                                        LocalDate fechaHasta,
                                                        String estado,
                                                        String cliente,
                                                        Pageable pageable) {
        LocalDateTime fechaDesdeDT = fechaDesde != null ? fechaDesde.atStartOfDay() : LocalDateTime.of(1900, 1, 1, 0, 0, 0);
        LocalDateTime fechaHastaDT = fechaHasta != null ? fechaHasta.atTime(23, 59, 59) : LocalDateTime.of(9999, 12, 31, 23, 59, 59);
        String estadoParam = estado != null ? estado : "";
        String clienteParam = cliente != null ? cliente : "";
        return turnoRepository.buscarTurnosAbogado(idAbogado, fechaDesdeDT, fechaHastaDT,
                        estadoParam, clienteParam, pageable)
                .map(Utils::mapTurnoToListadoDTOParaAbogado);
    }

    @Transactional
    public TurnoDTO agregarObservacionesAbogado(Long idTurno, String observaciones) {
        Turno turno = obtenerPorId(idTurno);
        turno.setObservacionesAbogado(observaciones);

        Turno actualizado = turnoRepository.save(turno);
        return Utils.mapTurnoEntityToDTO(actualizado);
    }

    @Transactional
    public TurnoDTO marcarPagado(Long idTurno) {
        Turno turno = obtenerPorId(idTurno);


        String estadoActual = turno.getEstadoActual().getNombreEstado();
        if (!"RESERVADO".equals(estadoActual) && !"PENDIENTE_COBRO".equals(estadoActual)) {
            throw new IllegalStateException("Solo un turno reservado o pendiente de cobro puede marcarse como pagado");
        }


        if (turno.getCobro() != null && turno.getCobro().getEstadoCobro() != null
                && ("PAGADO".equals(turno.getCobro().getEstadoCobro().getNombreEstado())
                || "PAGADO_EFECTIVO/TRANSFERENCIA".equals(turno.getCobro().getEstadoCobro().getNombreEstado()))) {
            throw new IllegalStateException("El cobro ya está marcado como pagado");
        }


        Cobro cobro = turno.getCobro();
        if (cobro == null) {
            cobro = new Cobro();
            cobro.setImporteTotal(Float.valueOf(parametroService.getValor("PRECIO_TURNO")));
            turno.setCobro(cobro);
        }
        cobroService.marcarComoPagadoEfectivoTransferencia(cobro);


        Estado estadoTurnoPagado = estadoRepository.findByNombreAndAmbito("PAGADO", "TURNO")
                .orElseThrow(() -> new RuntimeException("Estado PAGADO de TURNO no encontrado"));

        Estado anterior = turno.getEstadoActual();
        turno.setEstadoActual(estadoTurnoPagado);

        historialTurnoService.registrarCambio(turno, anterior, estadoTurnoPagado);

        Turno guardado = turnoRepository.save(turno);


        return Utils.mapTurnoEntityToDTO(guardado);
    }

    public TurnoConHistorialDTO getTurnoConHistorial(Long idTurno) {
        Turno turno = obtenerPorId(idTurno);
        return Utils.mapTurnoToConHistorialDTO(turno);
    }

}