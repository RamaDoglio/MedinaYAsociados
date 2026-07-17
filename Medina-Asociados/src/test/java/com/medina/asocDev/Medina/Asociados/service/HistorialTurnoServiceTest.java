package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.EstadoDTO;
import com.medina.asocDev.Medina.Asociados.dto.HistorialTurnoDTO;
import com.medina.asocDev.Medina.Asociados.entity.Estado;
import com.medina.asocDev.Medina.Asociados.entity.HistorialTurno;
import com.medina.asocDev.Medina.Asociados.entity.Turno;
import com.medina.asocDev.Medina.Asociados.repo.HistorialTurnoRepository;
import com.medina.asocDev.Medina.Asociados.repo.TurnoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistorialTurnoServiceTest {

    @Mock
    private HistorialTurnoRepository historialTurnoRepository;

    @Mock
    private TurnoRepository turnoRepository;

    @InjectMocks
    private HistorialTurnoService historialTurnoService;

    @Test
    void getHistorialByTurnoId_turnoConHistorial_returnsList() {
        HistorialTurno h1 = new HistorialTurno();
        h1.setIdHistorial(1L);
        Estado e1 = new Estado();
        e1.setNombreEstado("RESERVADO");
        h1.setEstadoHistorial(e1);

        HistorialTurno h2 = new HistorialTurno();
        h2.setIdHistorial(2L);
        Estado e2 = new Estado();
        e2.setNombreEstado("PAGADO");
        h2.setEstadoHistorial(e2);

        when(historialTurnoRepository.findByTurno_IdTurno(1L)).thenReturn(List.of(h1, h2));

        List<HistorialTurnoDTO> resultado = historialTurnoService.getHistorialByTurnoId(1L);

        assertEquals(2, resultado.size());
        verify(historialTurnoRepository).findByTurno_IdTurno(1L);
    }

    @Test
    void getHistorialByTurnoId_sinHistorial_returnsEmptyList() {
        when(historialTurnoRepository.findByTurno_IdTurno(1L)).thenReturn(List.of());

        List<HistorialTurnoDTO> resultado = historialTurnoService.getHistorialByTurnoId(1L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void getEstadoActualByTurnoId_turnoExiste_returnsEstadoDTO() {
        Turno turno = new Turno();
        Estado estado = new Estado();
        estado.setIdEstado(1L);
        estado.setNombreEstado("RESERVADO");
        estado.setAmbito("TURNO");
        turno.setEstadoActual(estado);

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        EstadoDTO resultado = historialTurnoService.getEstadoActualByTurnoId(1L);

        assertNotNull(resultado);
        assertEquals("RESERVADO", resultado.getNombreEstado());
    }

    @Test
    void getEstadoActualByTurnoId_turnoNoExiste_returnsNull() {
        when(turnoRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(historialTurnoService.getEstadoActualByTurnoId(99L));
    }

    @Test
    void getHistorialById_idExiste_returnsHistorialDTO() {
        HistorialTurno historial = new HistorialTurno();
        historial.setIdHistorial(1L);
        Estado estado = new Estado();
        estado.setNombreEstado("RESERVADO");
        historial.setEstadoHistorial(estado);

        when(historialTurnoRepository.findById(1L)).thenReturn(Optional.of(historial));

        HistorialTurnoDTO resultado = historialTurnoService.getHistorialById(1L);

        assertNotNull(resultado);
    }

    @Test
    void getHistorialById_idNoExiste_returnsNull() {
        when(historialTurnoRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(historialTurnoService.getHistorialById(99L));
    }

    @Test
    void registrarCambio_conHistorialesAbiertos_losCierraYCreaNuevo() {
        Turno turno = new Turno();
        turno.setIdTurno(1L);

        Estado estadoAnterior = new Estado();
        estadoAnterior.setIdEstado(1L);
        estadoAnterior.setNombreEstado("RESERVADO");

        Estado estadoNuevo = new Estado();
        estadoNuevo.setIdEstado(2L);
        estadoNuevo.setNombreEstado("PAGADO");

        HistorialTurno abierto = new HistorialTurno();
        abierto.setIdHistorial(1L);
        abierto.setFechaHoraFin(null);
        abierto.setEstadoHistorial(estadoAnterior);

        when(historialTurnoRepository.findByTurno_IdTurnoAndFechaHoraFinIsNull(1L))
                .thenReturn(List.of(abierto));

        historialTurnoService.registrarCambio(turno, estadoAnterior, estadoNuevo);

        assertNotNull(abierto.getFechaHoraFin());
        verify(historialTurnoRepository).save(abierto);
        ArgumentCaptor<HistorialTurno> captor = ArgumentCaptor.forClass(HistorialTurno.class);
        verify(historialTurnoRepository, times(2)).save(captor.capture());
        HistorialTurno nuevo = captor.getAllValues().get(1);
        assertEquals(turno, nuevo.getTurno());
        assertEquals(estadoNuevo, nuevo.getEstadoHistorial());
        assertNull(nuevo.getFechaHoraFin());
    }

    @Test
    void registrarCambio_sinHistorialesAbiertos_soloCreaNuevo() {
        Turno turno = new Turno();
        turno.setIdTurno(1L);

        Estado estadoAnterior = new Estado();
        estadoAnterior.setIdEstado(1L);

        Estado estadoNuevo = new Estado();
        estadoNuevo.setIdEstado(2L);
        estadoNuevo.setNombreEstado("PAGADO");

        when(historialTurnoRepository.findByTurno_IdTurnoAndFechaHoraFinIsNull(1L))
                .thenReturn(List.of());

        historialTurnoService.registrarCambio(turno, estadoAnterior, estadoNuevo);

        ArgumentCaptor<HistorialTurno> captor = ArgumentCaptor.forClass(HistorialTurno.class);
        verify(historialTurnoRepository).save(captor.capture());
        HistorialTurno nuevo = captor.getValue();
        assertEquals(turno, nuevo.getTurno());
        assertEquals(estadoNuevo, nuevo.getEstadoHistorial());
        assertNull(nuevo.getFechaHoraFin());
        assertNotNull(nuevo.getFechaHoraInicio());
    }
}
