package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.CobroConDetallesDTO;
import com.medina.asocDev.Medina.Asociados.dto.CobroDTO;
import com.medina.asocDev.Medina.Asociados.entity.Cobro;
import com.medina.asocDev.Medina.Asociados.entity.DetalleCobro;
import com.medina.asocDev.Medina.Asociados.entity.Estado;
import com.medina.asocDev.Medina.Asociados.entity.Turno;
import com.medina.asocDev.Medina.Asociados.repo.CobroRepository;
import com.medina.asocDev.Medina.Asociados.repo.DetalleCobroRepository;
import com.medina.asocDev.Medina.Asociados.repo.EstadoRepository;
import com.medina.asocDev.Medina.Asociados.repo.TurnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CobroServiceTest {

    @Mock private CobroRepository cobroRepository;
    @Mock private EstadoRepository estadoRepository;
    @Mock private DetalleCobroService detalleCobroService;
    @Mock private DetalleCobroRepository detalleCobroRepository;
    @Mock private TurnoRepository turnoRepository;
    @Mock private HistorialTurnoService historialTurnoService;
    @Mock private NotificacionTurnoService notificacionTurnoService;
    @Mock private EmailQueueService emailQueueService;

    @InjectMocks private CobroService cobroService;

    private Estado estadoPagado;
    private Estado estadoReembolsado;
    private Estado estadoPagadoEfectivo;
    private Estado estadoPagadoTurno;
    private Estado estadoPendiente;
    private Cobro cobro;
    private CobroDTO cobroDTO;
    private Turno turno;

    @BeforeEach
    void setUp() {
        estadoPendiente = new Estado();
        estadoPendiente.setIdEstado(1L);
        estadoPendiente.setNombreEstado("PENDIENTE");
        estadoPendiente.setAmbito("COBRO");

        estadoPagado = new Estado();
        estadoPagado.setIdEstado(2L);
        estadoPagado.setNombreEstado("PAGADO");
        estadoPagado.setAmbito("COBRO");

        estadoReembolsado = new Estado();
        estadoReembolsado.setIdEstado(3L);
        estadoReembolsado.setNombreEstado("REEMBOLSADO");
        estadoReembolsado.setAmbito("COBRO");

        estadoPagadoEfectivo = new Estado();
        estadoPagadoEfectivo.setIdEstado(4L);
        estadoPagadoEfectivo.setNombreEstado("PAGADO EFECTIVO/TRANSFERENCIA");
        estadoPagadoEfectivo.setAmbito("COBRO");

        estadoPagadoTurno = new Estado();
        estadoPagadoTurno.setIdEstado(5L);
        estadoPagadoTurno.setNombreEstado("PAGADO");
        estadoPagadoTurno.setAmbito("TURNO");

        cobro = new Cobro();
        cobro.setIdCobro(1L);
        cobro.setImporteTotal(5000f);
        cobro.setEstadoCobro(estadoPendiente);

        turno = new Turno();
        turno.setIdTurno(1L);
        turno.setEstadoActual(estadoPendiente);
        cobro.setTurno(turno);
        turno.setCobro(cobro);

        cobroDTO = new CobroDTO();
        cobroDTO.setImporteTotal(5000f);
        cobroDTO.setIdEstado(1L);
    }

    @Test
    void createCobro_sinEstado_createsAndReturnsDTO() {
        cobroDTO.setIdEstado(null);
        when(cobroRepository.save(any(Cobro.class))).thenAnswer(i -> {
            Cobro c = i.getArgument(0);
            c.setIdCobro(1L);
            return c;
        });

        CobroDTO resultado = cobroService.createCobro(cobroDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdCobro());
        assertEquals(5000f, resultado.getImporteTotal());
        verify(cobroRepository).save(any(Cobro.class));
    }

    @Test
    void createCobro_conEstado_createsAndReturnsDTO() {
        when(estadoRepository.findById(1L)).thenReturn(Optional.of(estadoPendiente));
        when(cobroRepository.save(any(Cobro.class))).thenAnswer(i -> {
            Cobro c = i.getArgument(0);
            c.setIdCobro(1L);
            return c;
        });

        CobroDTO resultado = cobroService.createCobro(cobroDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdCobro());
        verify(estadoRepository).findById(1L);
    }

    @Test
    void getCobroPorTurno_found_returnsDTO() {
        when(cobroRepository.findByTurno_IdTurno(1L)).thenReturn(List.of(cobro));

        CobroDTO resultado = cobroService.getCobroPorTurno(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdCobro());
    }

    @Test
    void getCobroPorTurno_notFound_returnsNull() {
        when(cobroRepository.findByTurno_IdTurno(99L)).thenReturn(List.of());

        CobroDTO resultado = cobroService.getCobroPorTurno(99L);

        assertNull(resultado);
    }

    @Test
    void getCobroPorId_found_returnsDTO() {
        when(cobroRepository.findById(1L)).thenReturn(Optional.of(cobro));

        CobroDTO resultado = cobroService.getCobroPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdCobro());
    }

    @Test
    void getCobroPorId_notFound_returnsNull() {
        when(cobroRepository.findById(99L)).thenReturn(Optional.empty());

        CobroDTO resultado = cobroService.getCobroPorId(99L);

        assertNull(resultado);
    }

    @Test
    void updateCobro_found_updatesAndReturnsDTO() {
        when(cobroRepository.findById(1L)).thenReturn(Optional.of(cobro));
        when(estadoRepository.findById(1L)).thenReturn(Optional.of(estadoPendiente));
        when(cobroRepository.save(any(Cobro.class))).thenAnswer(i -> i.getArgument(0));

        CobroDTO resultado = cobroService.updateCobro(1L, cobroDTO);

        assertNotNull(resultado);
        verify(cobroRepository).save(cobro);
    }

    @Test
    void updateCobro_notFound_returnsNull() {
        when(cobroRepository.findById(99L)).thenReturn(Optional.empty());

        CobroDTO resultado = cobroService.updateCobro(99L, cobroDTO);

        assertNull(resultado);
    }

    @Test
    void deleteCobro_exists_returnsTrue() {
        when(cobroRepository.existsById(1L)).thenReturn(true);

        boolean resultado = cobroService.deleteCobro(1L);

        assertTrue(resultado);
        verify(cobroRepository).deleteById(1L);
    }

    @Test
    void deleteCobro_notFound_returnsFalse() {
        when(cobroRepository.existsById(99L)).thenReturn(false);

        boolean resultado = cobroService.deleteCobro(99L);

        assertFalse(resultado);
        verify(cobroRepository, never()).deleteById(anyLong());
    }

    @Test
    void reembolsar_setsEstadoAndCreatesDetalle_returnsDTO() {
        when(estadoRepository.findByNombreAndAmbito("REEMBOLSADO", "COBRO"))
                .thenReturn(Optional.of(estadoReembolsado));
        when(cobroRepository.save(any(Cobro.class))).thenAnswer(i -> i.getArgument(0));

        CobroDTO resultado = cobroService.reembolsar(cobro);

        assertNotNull(resultado);
        assertEquals(estadoReembolsado, cobro.getEstadoCobro());
        verify(detalleCobroService).crearDetalleCobro(1L, 2L);
    }

    @Test
    void marcarComoPagado_idempotent_alreadyPagado_returnsEarly() {
        cobro.setEstadoCobro(estadoPagado);

        CobroDTO resultado = cobroService.marcarComoPagado(cobro);

        assertNotNull(resultado);
        verify(estadoRepository, never()).findByNombreAndAmbito(anyString(), anyString());
        verify(detalleCobroService, never()).crearDetalleCobro(anyLong(), anyLong());
    }

    @Test
    void marcarComoPagado_fullFlow_success() {
        doNothing().when(emailQueueService).enviarConDelay(any(Runnable.class));
        when(estadoRepository.findByNombreAndAmbito("PAGADO", "COBRO"))
                .thenReturn(Optional.of(estadoPagado));
        when(cobroRepository.save(any(Cobro.class))).thenAnswer(i -> i.getArgument(0));
        when(estadoRepository.findByNombreAndAmbito("PAGADO", "TURNO"))
                .thenReturn(Optional.of(estadoPagadoTurno));

        CobroDTO resultado = cobroService.marcarComoPagado(cobro);

        assertNotNull(resultado);
        assertEquals(estadoPagado, cobro.getEstadoCobro());
        verify(detalleCobroService).crearDetalleCobro(1L, 1L);
        verify(historialTurnoService).registrarCambio(turno, estadoPendiente, estadoPagadoTurno);
        verify(turnoRepository).save(turno);
    }

    @Test
    void marcarComoPagadoEfectivoTransferencia_idempotent_alreadySet_returnsEarly() {
        cobro.setEstadoCobro(estadoPagadoEfectivo);

        CobroDTO resultado = cobroService.marcarComoPagadoEfectivoTransferencia(cobro);

        assertNotNull(resultado);
        verify(estadoRepository, never()).findByNombreAndAmbito(anyString(), anyString());
        verify(detalleCobroService, never()).crearDetalleCobro(anyLong(), anyLong());
    }

    @Test
    void marcarComoPagadoEfectivoTransferencia_fullFlow_success() {
        when(estadoRepository.findByNombreAndAmbito("PAGADO EFECTIVO/TRANSFERENCIA", "COBRO"))
                .thenReturn(Optional.of(estadoPagadoEfectivo));
        when(cobroRepository.save(any(Cobro.class))).thenAnswer(i -> i.getArgument(0));

        CobroDTO resultado = cobroService.marcarComoPagadoEfectivoTransferencia(cobro);

        assertNotNull(resultado);
        assertEquals(estadoPagadoEfectivo, cobro.getEstadoCobro());
        verify(detalleCobroService).crearDetalleCobro(1L, 3L);
    }

    @Test
    void getCobroConDetalles_found_returnsDTO() {
        DetalleCobro detalle = new DetalleCobro();
        detalle.setIdDetalleCobro(1L);
        detalle.setSubTotal(5000f);
        detalle.setCobro(cobro);

        when(cobroRepository.findById(1L)).thenReturn(Optional.of(cobro));
        when(detalleCobroRepository.findByCobro_IdCobro(1L)).thenReturn(List.of(detalle));

        CobroConDetallesDTO resultado = cobroService.getCobroConDetalles(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdCobro());
    }

    @Test
    void getCobroConDetalles_notFound_returnsNull() {
        when(cobroRepository.findById(99L)).thenReturn(Optional.empty());

        CobroConDetallesDTO resultado = cobroService.getCobroConDetalles(99L);

        assertNull(resultado);
    }
}
