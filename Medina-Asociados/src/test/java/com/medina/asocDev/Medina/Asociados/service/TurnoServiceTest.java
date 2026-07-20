package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.TurnoCreateRequest;
import com.medina.asocDev.Medina.Asociados.dto.TurnoDTO;
import com.medina.asocDev.Medina.Asociados.entity.*;
import com.medina.asocDev.Medina.Asociados.repo.*;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurnoServiceTest {

    @Mock private TurnoRepository turnoRepository;
    @Mock private EstadoRepository estadoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private UsuarioService usuarioService;
    @Mock private EspecialidadRepository especialidadRepository;
    @Mock private AbogadoService abogadoService;
    @Mock private CobroService cobroService;
    @Mock private HistorialTurnoService historialTurnoService;
    @Mock private NotificacionTurnoService notificacionTurnoService;
    @Mock private EmailQueueService emailQueueService;
    @Mock private MercadoPagoService mercadoPagoService;
    @Mock private ParametroService parametroService;

    @InjectMocks private TurnoService turnoService;

    private Usuario cliente;
    private Usuario abogado;
    private Especialidad especialidad;
    private Estado estadoReservado;
    private Estado estadoPendienteCobro;
    private Estado estadoPagadoTurno;
    private Estado estadoPagadoCobro;
    private Estado estadoEnCurso;
    private Estado estadoNoAsistio;
    private Estado estadoFinalizado;
    private Estado estadoReprogramado;
    private Estado estadoCanceladoSinReembolso;
    private Estado estadoCanceladoConReembolso;
    private Estado estadoReembolsadoCobro;
    private Estado estadoPendienteCobroEstado;
    private Cobro cobro;
    private Turno turno;

    @BeforeEach
    void setUp() {
        cliente = new Usuario();
        cliente.setIdUsuario(1L);
        cliente.setNombre("Carlos");
        cliente.setApellido("García");

        abogado = new Usuario();
        abogado.setIdUsuario(2L);
        abogado.setNombre("María");
        abogado.setApellido("López");

        especialidad = new Especialidad();
        especialidad.setIdEspecialidad(1L);
        especialidad.setNombreEspecialidad("Penal");

        estadoReservado = new Estado();
        estadoReservado.setIdEstado(4L);
        estadoReservado.setNombreEstado("RESERVADO");
        estadoReservado.setAmbito("TURNO");

        estadoPendienteCobro = new Estado();
        estadoPendienteCobro.setIdEstado(5L);
        estadoPendienteCobro.setNombreEstado("PENDIENTE_COBRO");
        estadoPendienteCobro.setAmbito("TURNO");

        estadoPagadoTurno = new Estado();
        estadoPagadoTurno.setIdEstado(6L);
        estadoPagadoTurno.setNombreEstado("PAGADO");
        estadoPagadoTurno.setAmbito("TURNO");

        estadoPagadoCobro = new Estado();
        estadoPagadoCobro.setIdEstado(7L);
        estadoPagadoCobro.setNombreEstado("PAGADO");
        estadoPagadoCobro.setAmbito("COBRO");

        estadoEnCurso = new Estado();
        estadoEnCurso.setIdEstado(8L);
        estadoEnCurso.setNombreEstado("EN_CURSO");
        estadoEnCurso.setAmbito("TURNO");

        estadoNoAsistio = new Estado();
        estadoNoAsistio.setIdEstado(9L);
        estadoNoAsistio.setNombreEstado("NO_ASISTIO");
        estadoNoAsistio.setAmbito("TURNO");

        estadoFinalizado = new Estado();
        estadoFinalizado.setIdEstado(10L);
        estadoFinalizado.setNombreEstado("FINALIZADO");
        estadoFinalizado.setAmbito("TURNO");

        estadoReprogramado = new Estado();
        estadoReprogramado.setIdEstado(11L);
        estadoReprogramado.setNombreEstado("REPROGRAMADO");
        estadoReprogramado.setAmbito("TURNO");

        estadoCanceladoSinReembolso = new Estado();
        estadoCanceladoSinReembolso.setIdEstado(12L);
        estadoCanceladoSinReembolso.setNombreEstado("CANCELADO_SIN_REEMBOLSO");
        estadoCanceladoSinReembolso.setAmbito("TURNO");

        estadoCanceladoConReembolso = new Estado();
        estadoCanceladoConReembolso.setIdEstado(13L);
        estadoCanceladoConReembolso.setNombreEstado("CANCELADO_CON_REEMBOLSO");
        estadoCanceladoConReembolso.setAmbito("TURNO");

        estadoReembolsadoCobro = new Estado();
        estadoReembolsadoCobro.setIdEstado(14L);
        estadoReembolsadoCobro.setNombreEstado("REEMBOLSADO");
        estadoReembolsadoCobro.setAmbito("COBRO");

        estadoPendienteCobroEstado = new Estado();
        estadoPendienteCobroEstado.setIdEstado(15L);
        estadoPendienteCobroEstado.setNombreEstado("PENDIENTE");
        estadoPendienteCobroEstado.setAmbito("COBRO");

        cobro = new Cobro();
        cobro.setIdCobro(1L);
        cobro.setImporteTotal(5000f);
        cobro.setEstadoCobro(estadoPendienteCobroEstado);

        turno = new Turno();
        turno.setIdTurno(1L);
        turno.setClienteTurno(cliente);
        turno.setAbogadoTurno(abogado);
        turno.setEspecialidad(especialidad);
        turno.setCobro(cobro);
        cobro.setTurno(turno);
        turno.setEstadoActual(estadoReservado);
        turno.setHorarioTurno(LocalDateTime.of(2026, 7, 20, 14, 0));
        turno.setObservacionesCliente("Consulta inicial");
    }

    // ──────────────────────────────────────────────
    // crearTurno
    // ──────────────────────────────────────────────

    @Test
    void crearTurno_datosValidos_createsTurno() {
        TurnoCreateRequest request = new TurnoCreateRequest();
        request.setIdCliente(1L);
        request.setIdAbogado(2L);
        request.setIdEspecialidad(1L);
        request.setHorarioTurno(LocalDateTime.of(2026, 7, 20, 14, 0));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(abogado));
        when(especialidadRepository.findById(1L)).thenReturn(Optional.of(especialidad));
        when(estadoRepository.findByNombreAndAmbito("RESERVADO", "TURNO"))
                .thenReturn(Optional.of(estadoReservado));
        when(abogadoService.esFinDeSemana(any(LocalDate.class))).thenReturn(false);
        when(parametroService.getValor("PRECIO_TURNO")).thenReturn("5000");
        when(estadoRepository.findByNombreAndAmbito("PENDIENTE", "COBRO"))
                .thenReturn(Optional.of(estadoPendienteCobroEstado));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(i -> i.getArgument(0));

        Turno resultado = turnoService.crearTurno(request);

        assertNotNull(resultado);
        assertEquals(cliente, resultado.getClienteTurno());
        assertEquals(abogado, resultado.getAbogadoTurno());
        assertEquals(estadoReservado, resultado.getEstadoActual());
        assertEquals(5000f, resultado.getCobro().getImporteTotal());
        verify(historialTurnoService).registrarCambio(resultado, null, estadoReservado);
    }

    @Test
    void crearTurno_clienteNoExiste_throwsException() {
        TurnoCreateRequest request = new TurnoCreateRequest();
        request.setIdCliente(99L);

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> turnoService.crearTurno(request));
    }

    @Test
    void crearTurno_abogadoNoExiste_throwsException() {
        TurnoCreateRequest request = new TurnoCreateRequest();
        request.setIdCliente(1L);
        request.setIdAbogado(99L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> turnoService.crearTurno(request));
    }

    @Test
    void crearTurno_especialidadNoExiste_throwsException() {
        TurnoCreateRequest request = new TurnoCreateRequest();
        request.setIdCliente(1L);
        request.setIdAbogado(2L);
        request.setIdEspecialidad(99L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(abogado));
        when(especialidadRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> turnoService.crearTurno(request));
    }

    @Test
    void crearTurno_finde_throwsException() {
        TurnoCreateRequest request = new TurnoCreateRequest();
        request.setIdCliente(1L);
        request.setIdAbogado(2L);
        request.setIdEspecialidad(1L);
        request.setHorarioTurno(LocalDateTime.of(2026, 7, 20, 14, 0));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(abogado));
        when(especialidadRepository.findById(1L)).thenReturn(Optional.of(especialidad));
        when(estadoRepository.findByNombreAndAmbito("RESERVADO", "TURNO"))
                .thenReturn(Optional.of(estadoReservado));
        when(abogadoService.esFinDeSemana(any(LocalDate.class))).thenReturn(true);

        assertThrows(RuntimeException.class, () -> turnoService.crearTurno(request));
    }

    // ──────────────────────────────────────────────
    // pagarTurno
    // ──────────────────────────────────────────────

    @Test
    void pagarTurno_turnoReservadoYCobroPendiente_creaPreferencia() throws Exception {
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(mercadoPagoService.crearPreferencia(cobro, turno)).thenReturn("https://mp-pago.test");

        String url = turnoService.pagarTurno(1L);

        assertEquals("https://mp-pago.test", url);
    }

    @Test
    void pagarTurno_turnoNoReservado_throwsException() {
        turno.setEstadoActual(estadoPagadoTurno);
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        assertThrows(IllegalStateException.class, () -> turnoService.pagarTurno(1L));
    }

    @Test
    void pagarTurno_cobroNoPendiente_throwsException() {
        cobro.setEstadoCobro(estadoPagadoCobro);
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        assertThrows(IllegalStateException.class, () -> turnoService.pagarTurno(1L));
    }

    // ──────────────────────────────────────────────
    // reprogramarTurno
    // ──────────────────────────────────────────────

    @Test
    void reprogramarTurno_estadoInvalido_throwsException() {
        // Estado RESERVADO no permite reprogramación
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        assertThrows(IllegalArgumentException.class,
                () -> turnoService.reprogramarTurno(1L, LocalDateTime.of(2026, 8, 1, 14, 0)));
    }

    @Test
    void reprogramarTurno_horarioNoDisponible_throwsException() {
        turno.setEstadoActual(estadoPagadoTurno);
        // Fecha del turno actual > 24h
        turno.setHorarioTurno(LocalDateTime.now().plusDays(10));
        LocalDateTime nuevaFecha = LocalDateTime.now().plusDays(15);

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(abogadoService.verificarDisponibilidad(2L, nuevaFecha)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> turnoService.reprogramarTurno(1L, nuevaFecha));
    }

    @Test
    void reprogramarTurno_datosValidos_reprograma() {
        turno.setEstadoActual(estadoPagadoTurno);
        turno.setHorarioTurno(LocalDateTime.now().plusDays(10));
        LocalDateTime nuevaFecha = LocalDateTime.now().plusDays(15);

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(abogadoService.verificarDisponibilidad(2L, nuevaFecha)).thenReturn(true);
        when(estadoRepository.findByNombreAndAmbito("REPROGRAMADO", "TURNO"))
                .thenReturn(Optional.of(estadoReprogramado));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(emailQueueService).enviarConDelay(any(Runnable.class));

        TurnoDTO resultado = turnoService.reprogramarTurno(1L, nuevaFecha);

        assertNotNull(resultado);
        verify(historialTurnoService).registrarCambio(turno, estadoPagadoTurno, estadoReprogramado);
        assertEquals(estadoReprogramado, turno.getEstadoActual());
        assertEquals(nuevaFecha, turno.getHorarioTurno());
    }

    // ──────────────────────────────────────────────
    // cancelarTurno
    // ──────────────────────────────────────────────

    @Test
    void cancelarTurno_estadoNoPermitido_throwsException() {
        // RESERVADO no se puede cancelar según la regla
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        assertThrows(IllegalStateException.class,
                () -> turnoService.cancelarTurno(1L));
    }

    @Test
    void cancelarTurno_cancelacionTardia_sinReembolso() {
        turno.setEstadoActual(estadoPagadoTurno);
        // Fecha del turno hace menos de 24h
        turno.setHorarioTurno(LocalDateTime.now().plusHours(2));

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(estadoRepository.findByNombreAndAmbito("CANCELADO_SIN_REEMBOLSO", "TURNO"))
                .thenReturn(Optional.of(estadoCanceladoSinReembolso));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(emailQueueService).enviarConDelay(any(Runnable.class));

        TurnoDTO resultado = turnoService.cancelarTurno(1L);

        assertNotNull(resultado);
        assertEquals(estadoCanceladoSinReembolso, turno.getEstadoActual());
        verify(historialTurnoService).registrarCambio(turno, estadoPagadoTurno, estadoCanceladoSinReembolso);
        verify(cobroService, never()).reembolsar(any());
    }

    @Test
    void cancelarTurno_cancelacionTemprana_conReembolsoMP() {
        turno.setEstadoActual(estadoPagadoTurno);
        turno.setHorarioTurno(LocalDateTime.now().plusDays(10));
        cobro.setPaymentId(12345L);

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(estadoRepository.findByNombreAndAmbito("CANCELADO_CON_REEMBOLSO", "TURNO"))
                .thenReturn(Optional.of(estadoCanceladoConReembolso));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(emailQueueService).enviarConDelay(any(Runnable.class));

        TurnoDTO resultado = turnoService.cancelarTurno(1L);

        assertNotNull(resultado);
        assertEquals(estadoCanceladoConReembolso, turno.getEstadoActual());
        verify(mercadoPagoService).reembolsarPago(12345L);
        verify(cobroService).reembolsar(cobro);
    }

    @Test
    void cancelarTurno_cancelacionTemprana_sinPaymentId_soloReembolsoInterno() {
        turno.setEstadoActual(estadoPagadoTurno);
        turno.setHorarioTurno(LocalDateTime.now().plusDays(10));
        cobro.setPaymentId(null);

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(estadoRepository.findByNombreAndAmbito("CANCELADO_CON_REEMBOLSO", "TURNO"))
                .thenReturn(Optional.of(estadoCanceladoConReembolso));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(emailQueueService).enviarConDelay(any(Runnable.class));

        turnoService.cancelarTurno(1L);

        verify(mercadoPagoService, never()).reembolsarPago(anyLong());
        verify(cobroService).reembolsar(cobro);
    }

    @Test
    void cancelarTurno_yaReembolsado_noReembolsaDosVeces() {
        turno.setEstadoActual(estadoPagadoTurno);
        turno.setHorarioTurno(LocalDateTime.now().plusDays(10));
        cobro.setPaymentId(12345L);
        cobro.setEstadoCobro(estadoReembolsadoCobro);

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(estadoRepository.findByNombreAndAmbito("CANCELADO_CON_REEMBOLSO", "TURNO"))
                .thenReturn(Optional.of(estadoCanceladoConReembolso));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(emailQueueService).enviarConDelay(any(Runnable.class));

        turnoService.cancelarTurno(1L);

        verify(mercadoPagoService, never()).reembolsarPago(anyLong());
        verify(cobroService, never()).reembolsar(any());
    }

    // ──────────────────────────────────────────────
    // Transiciones de estado
    // ──────────────────────────────────────────────

    @Test
    void marcarEnCurso_desdePagado_actualizaEstado() {
        turno.setEstadoActual(estadoPagadoTurno);
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(estadoRepository.findByNombreAndAmbito("EN_CURSO", "TURNO"))
                .thenReturn(Optional.of(estadoEnCurso));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(i -> i.getArgument(0));

        TurnoDTO resultado = turnoService.marcarEnCurso(1L);

        assertNotNull(resultado);
        verify(historialTurnoService).registrarCambio(turno, estadoPagadoTurno, estadoEnCurso);
    }

    @Test
    void marcarEnCurso_desdeReservado_throwsException() {
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        assertThrows(IllegalStateException.class, () -> turnoService.marcarEnCurso(1L));
    }

    @Test
    void marcarNoAsistio_desdeEnCurso_actualizaEstado() {
        turno.setEstadoActual(estadoEnCurso);
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(estadoRepository.findByNombreAndAmbito("NO_ASISTIO", "TURNO"))
                .thenReturn(Optional.of(estadoNoAsistio));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(i -> i.getArgument(0));

        TurnoDTO resultado = turnoService.marcarNoAsistio(1L);

        assertNotNull(resultado);
        verify(historialTurnoService).registrarCambio(turno, estadoEnCurso, estadoNoAsistio);
    }

    @Test
    void marcarNoAsistio_desdePagado_throwsException() {
        turno.setEstadoActual(estadoPagadoTurno);
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        assertThrows(IllegalStateException.class, () -> turnoService.marcarNoAsistio(1L));
    }

    @Test
    void finalizarTurno_desdeEnCurso_actualizaEstado() {
        turno.setEstadoActual(estadoEnCurso);
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(estadoRepository.findByNombreAndAmbito("FINALIZADO", "TURNO"))
                .thenReturn(Optional.of(estadoFinalizado));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(i -> i.getArgument(0));

        TurnoDTO resultado = turnoService.finalizarTurno(1L);

        assertNotNull(resultado);
        verify(historialTurnoService).registrarCambio(turno, estadoEnCurso, estadoFinalizado);
    }

    @Test
    void finalizarTurno_desdeNoAsistio_throwsException() {
        turno.setEstadoActual(estadoNoAsistio);
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        assertThrows(IllegalStateException.class, () -> turnoService.finalizarTurno(1L));
    }

    // ──────────────────────────────────────────────
    // marcarPagado
    // ──────────────────────────────────────────────

    @Test
    void marcarPagado_desdeReservado_actualizaCobroYEstado() {
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(estadoRepository.findByNombreAndAmbito("PAGADO", "TURNO"))
                .thenReturn(Optional.of(estadoPagadoTurno));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(i -> i.getArgument(0));

        TurnoDTO resultado = turnoService.marcarPagado(1L);

        assertNotNull(resultado);
        verify(cobroService).marcarComoPagadoEfectivoTransferencia(cobro);
        verify(historialTurnoService).registrarCambio(turno, estadoReservado, estadoPagadoTurno);
    }

    @Test
    void marcarPagado_cobroYaPagado_throwsException() {
        Estado pagadoEfectivo = new Estado();
        pagadoEfectivo.setNombreEstado("PAGADO_EFECTIVO/TRANSFERENCIA");
        cobro.setEstadoCobro(pagadoEfectivo);

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        assertThrows(IllegalStateException.class, () -> turnoService.marcarPagado(1L));
    }

    // ──────────────────────────────────────────────
    // obtenerPorId / listarTurnos / eliminarTurno
    // ──────────────────────────────────────────────

    @Test
    void obtenerPorId_idExiste_returnsTurno() {
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        Turno resultado = turnoService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdTurno());
    }

    @Test
    void obtenerPorId_idNoExiste_throwsException() {
        when(turnoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> turnoService.obtenerPorId(99L));
    }

    @Test
    void listarTurnos_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(turnoRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(turno)));

        Page<Turno> resultado = turnoService.listarTurnos(pageable);

        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void eliminarTurno_deletesById() {
        turnoService.eliminarTurno(1L);
        verify(turnoRepository).deleteById(1L);
    }

    @Test
    void agregarObservacionesAbogado_guardaObservaciones() {
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(i -> i.getArgument(0));

        TurnoDTO resultado = turnoService.agregarObservacionesAbogado(1L, "Observación test");

        assertNotNull(resultado);
        assertEquals("Observación test", turno.getObservacionesAbogado());
    }

    // ──────────────────────────────────────────────
    // getTurnoConHistorial
    // ──────────────────────────────────────────────

    @Test
    void getTurnoConHistorial_idExiste_returnsDTO() {
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        var resultado = turnoService.getTurnoConHistorial(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdTurno());
    }
}
