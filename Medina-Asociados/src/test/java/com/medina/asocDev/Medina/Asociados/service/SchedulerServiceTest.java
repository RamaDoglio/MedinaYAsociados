package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.entity.Estado;
import com.medina.asocDev.Medina.Asociados.entity.HistorialTurno;
import com.medina.asocDev.Medina.Asociados.entity.Turno;
import com.medina.asocDev.Medina.Asociados.repo.EstadoRepository;
import com.medina.asocDev.Medina.Asociados.repo.HistorialTurnoRepository;
import com.medina.asocDev.Medina.Asociados.repo.TokenBlacklistedRepository;
import com.medina.asocDev.Medina.Asociados.repo.TurnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulerServiceTest {

    @Mock private TurnoRepository turnoRepository;
    @Mock private TurnoService turnoService;
    @Mock private HistorialTurnoService historialTurnoService;
    @Mock private HistorialTurnoRepository historialTurnoRepository;
    @Mock private EstadoRepository estadoRepository;
    @Mock private TokenBlacklistedRepository tokenBlacklistedRepository;

    @InjectMocks private SchedulerService schedulerService;

    private Estado estadoReservado;
    private Estado estadoExpirado;
    private Turno turno;
    private HistorialTurno historial;

    @BeforeEach
    void setUp() {
        estadoReservado = new Estado();
        estadoReservado.setIdEstado(1L);
        estadoReservado.setNombreEstado("RESERVADO");
        estadoReservado.setAmbito("TURNO");

        estadoExpirado = new Estado();
        estadoExpirado.setIdEstado(2L);
        estadoExpirado.setNombreEstado("EXPIRO_PAGO");
        estadoExpirado.setAmbito("TURNO");

        turno = new Turno();
        turno.setIdTurno(1L);

        historial = new HistorialTurno();
        historial.setIdHistorial(1L);
        historial.setTurno(turno);
        historial.setEstadoHistorial(estadoReservado);
        historial.setFechaHoraInicio(LocalDateTime.now().minusHours(1));
    }

    @Test
    void expirarTurnosReservados_conTurnosVencidos_losExpira() {
        when(estadoRepository.findByNombreAndAmbito("RESERVADO", "TURNO"))
                .thenReturn(Optional.of(estadoReservado));
        when(historialTurnoRepository.findByEstadoHistorial_IdEstadoAndFechaHoraFinIsNull(1L))
                .thenReturn(List.of(historial));
        when(estadoRepository.findByNombreAndAmbito("EXPIRO_PAGO", "TURNO"))
                .thenReturn(Optional.of(estadoExpirado));

        schedulerService.expirarTurnosReservados();

        verify(historialTurnoService).registrarCambio(turno, estadoReservado, estadoExpirado);
        assertEquals(estadoExpirado, turno.getEstadoActual());
        verify(turnoRepository).save(turno);
    }

    @Test
    void expirarTurnosReservados_sinTurnosVencidos_noHaceNada() {
        historial.setFechaHoraInicio(LocalDateTime.now().minusMinutes(5));

        when(estadoRepository.findByNombreAndAmbito("RESERVADO", "TURNO"))
                .thenReturn(Optional.of(estadoReservado));
        when(estadoRepository.findByNombreAndAmbito("EXPIRO_PAGO", "TURNO"))
                .thenReturn(Optional.of(estadoExpirado));
        when(historialTurnoRepository.findByEstadoHistorial_IdEstadoAndFechaHoraFinIsNull(1L))
                .thenReturn(List.of(historial));

        schedulerService.expirarTurnosReservados();

        verify(historialTurnoService, never()).registrarCambio(any(), any(), any());
        verify(turnoRepository, never()).save(any());
    }

    @Test
    void expirarTurnosReservados_sinHistoriales_noHaceNada() {
        when(estadoRepository.findByNombreAndAmbito("RESERVADO", "TURNO"))
                .thenReturn(Optional.of(estadoReservado));
        when(estadoRepository.findByNombreAndAmbito("EXPIRO_PAGO", "TURNO"))
                .thenReturn(Optional.of(estadoExpirado));
        when(historialTurnoRepository.findByEstadoHistorial_IdEstadoAndFechaHoraFinIsNull(1L))
                .thenReturn(List.of());

        schedulerService.expirarTurnosReservados();

        verify(historialTurnoService, never()).registrarCambio(any(), any(), any());
        verify(turnoRepository, never()).save(any());
    }

    @Test
    void iniciarTurnosAutomaticamente_turnosVencidos_losMarcaEnCurso() {
        turno.setHorarioTurno(LocalDateTime.now().minusMinutes(5));
        Estado estadoPagado = new Estado();
        estadoPagado.setNombreEstado("PAGADO");
        turno.setEstadoActual(estadoPagado);

        when(turnoRepository.findByEstadoActualNombreEstadoIn(List.of("PAGADO", "REPROGRAMADO")))
                .thenReturn(List.of(turno));

        schedulerService.iniciarTurnosAutomaticamente();

        verify(turnoService).marcarEnCurso(1L);
    }

    @Test
    void iniciarTurnosAutomaticamente_turnoFuturo_noLlamamarcarEnCurso() {
        turno.setHorarioTurno(LocalDateTime.now().plusHours(2));
        Estado estadoPagado = new Estado();
        estadoPagado.setNombreEstado("PAGADO");
        turno.setEstadoActual(estadoPagado);

        when(turnoRepository.findByEstadoActualNombreEstadoIn(List.of("PAGADO", "REPROGRAMADO")))
                .thenReturn(List.of(turno));

        schedulerService.iniciarTurnosAutomaticamente();

        verify(turnoService, never()).marcarEnCurso(anyLong());
    }

    @Test
    void iniciarTurnosAutomaticamente_sinTurnos_noHaceNada() {
        when(turnoRepository.findByEstadoActualNombreEstadoIn(List.of("PAGADO", "REPROGRAMADO")))
                .thenReturn(List.of());

        schedulerService.iniciarTurnosAutomaticamente();

        verify(turnoService, never()).marcarEnCurso(anyLong());
    }

    @Test
    void limpiarTokenBlacklist_eliminaTokensExpirados() {
        schedulerService.limpiarTokenBlacklist();

        verify(tokenBlacklistedRepository).deleteByFechaExpiracionBefore(any(Date.class));
    }
}
