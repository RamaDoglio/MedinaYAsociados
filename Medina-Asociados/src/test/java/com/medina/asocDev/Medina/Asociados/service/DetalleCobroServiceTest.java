package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.DetalleCobroDTO;
import com.medina.asocDev.Medina.Asociados.entity.Cobro;
import com.medina.asocDev.Medina.Asociados.entity.DetalleCobro;
import com.medina.asocDev.Medina.Asociados.entity.TipoCobro;
import com.medina.asocDev.Medina.Asociados.repo.CobroRepository;
import com.medina.asocDev.Medina.Asociados.repo.DetalleCobroRepository;
import com.medina.asocDev.Medina.Asociados.repo.TipoCobroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DetalleCobroServiceTest {

    @Mock private DetalleCobroRepository detalleCobroRepository;
    @Mock private TipoCobroRepository tipoCobroRepository;
    @Mock private CobroRepository cobroRepository;

    @InjectMocks private DetalleCobroService detalleCobroService;

    private Cobro cobro;
    private TipoCobro tipoPago;
    private TipoCobro tipoReembolso;
    private DetalleCobro detalle;

    @BeforeEach
    void setUp() {
        cobro = new Cobro();
        cobro.setIdCobro(1L);
        cobro.setImporteTotal(5000f);

        tipoPago = new TipoCobro();
        tipoPago.setIdTipoCobro(1L);
        tipoPago.setNombreTipoCobro("PAGO");

        tipoReembolso = new TipoCobro();
        tipoReembolso.setIdTipoCobro(2L);
        tipoReembolso.setNombreTipoCobro("REEMBOLSO");

        detalle = new DetalleCobro();
        detalle.setIdDetalleCobro(1L);
        detalle.setCobro(cobro);
        detalle.setFecha(LocalDateTime.now());
        detalle.setSubTotal(5000f);
        detalle.setTipoCobro(tipoPago);
    }

    @Test
    void crearDetalleCobro_tipoPago_createsAndReturnsDTO() {
        when(cobroRepository.findByIdWithLock(1L)).thenReturn(Optional.of(cobro));
        when(tipoCobroRepository.findByNombreTipoCobro("PAGO")).thenReturn(tipoPago);
        when(detalleCobroRepository.findByCobro_IdCobro(1L)).thenReturn(List.of());
        when(detalleCobroRepository.save(any(DetalleCobro.class))).thenAnswer(i -> i.getArgument(0));

        DetalleCobroDTO resultado = detalleCobroService.crearDetalleCobro(1L, 1L);

        assertNotNull(resultado);
        verify(detalleCobroRepository).save(any(DetalleCobro.class));
    }

    @Test
    void crearDetalleCobro_tipoReembolso_createsAndReturnsDTO() {
        when(cobroRepository.findByIdWithLock(1L)).thenReturn(Optional.of(cobro));
        when(tipoCobroRepository.findByNombreTipoCobro("REEMBOLSO")).thenReturn(tipoReembolso);
        when(detalleCobroRepository.findByCobro_IdCobro(1L)).thenReturn(List.of());
        when(detalleCobroRepository.save(any(DetalleCobro.class))).thenAnswer(i -> i.getArgument(0));

        DetalleCobroDTO resultado = detalleCobroService.crearDetalleCobro(1L, 2L);

        assertNotNull(resultado);
        verify(detalleCobroRepository).save(any(DetalleCobro.class));
    }

    @Test
    void crearDetalleCobro_idempotent_alreadyExists_returnsNull() {
        when(cobroRepository.findByIdWithLock(1L)).thenReturn(Optional.of(cobro));
        when(tipoCobroRepository.findByNombreTipoCobro("PAGO")).thenReturn(tipoPago);
        when(detalleCobroRepository.findByCobro_IdCobro(1L)).thenReturn(List.of(detalle));

        DetalleCobroDTO resultado = detalleCobroService.crearDetalleCobro(1L, 1L);

        assertNull(resultado);
        verify(detalleCobroRepository, never()).save(any(DetalleCobro.class));
    }

    @Test
    void crearDetalleCobro_cobroNotFound_throwsRuntimeException() {
        when(cobroRepository.findByIdWithLock(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> detalleCobroService.crearDetalleCobro(99L, 1L));
    }

    @Test
    void getDetallesPorCobro_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(detalleCobroRepository.findByCobro_IdCobro(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(detalle)));

        Page<DetalleCobroDTO> resultado = detalleCobroService.getDetallesPorCobro(1L, pageable);

        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void getDetalleCobroById_found_returnsDTO() {
        when(detalleCobroRepository.findById(1L)).thenReturn(Optional.of(detalle));

        DetalleCobroDTO resultado = detalleCobroService.getDetalleCobroById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdDetalleCobro());
    }

    @Test
    void getDetalleCobroById_notFound_returnsNull() {
        when(detalleCobroRepository.findById(99L)).thenReturn(Optional.empty());

        DetalleCobroDTO resultado = detalleCobroService.getDetalleCobroById(99L);

        assertNull(resultado);
    }

    @Test
    void updateDetalleCobro_found_updatesAndReturnsDTO() {
        DetalleCobroDTO dto = new DetalleCobroDTO();
        dto.setFecha(LocalDateTime.now());
        dto.setDescripcionCobro("Actualizado");
        dto.setSubTotal(6000f);
        dto.setIdCobro(1L);
        dto.setIdTipoCobro(1L);

        when(detalleCobroRepository.findById(1L)).thenReturn(Optional.of(detalle));
        when(cobroRepository.findById(1L)).thenReturn(Optional.of(cobro));
        when(tipoCobroRepository.findById(1L)).thenReturn(Optional.of(tipoPago));
        when(detalleCobroRepository.save(any(DetalleCobro.class))).thenAnswer(i -> i.getArgument(0));

        DetalleCobroDTO resultado = detalleCobroService.updateDetalleCobro(1L, dto);

        assertNotNull(resultado);
        assertEquals("Actualizado", detalle.getDescripcionCobro());
        assertEquals(6000f, detalle.getSubTotal());
    }

    @Test
    void updateDetalleCobro_found_sinReferencias_returnsDTO() {
        DetalleCobroDTO dto = new DetalleCobroDTO();
        dto.setFecha(LocalDateTime.now());
        dto.setSubTotal(6000f);

        when(detalleCobroRepository.findById(1L)).thenReturn(Optional.of(detalle));
        when(detalleCobroRepository.save(any(DetalleCobro.class))).thenAnswer(i -> i.getArgument(0));

        DetalleCobroDTO resultado = detalleCobroService.updateDetalleCobro(1L, dto);

        assertNotNull(resultado);
        verify(cobroRepository, never()).findById(anyLong());
        verify(tipoCobroRepository, never()).findById(anyLong());
    }

    @Test
    void updateDetalleCobro_notFound_returnsNull() {
        when(detalleCobroRepository.findById(99L)).thenReturn(Optional.empty());

        DetalleCobroDTO resultado = detalleCobroService.updateDetalleCobro(99L, new DetalleCobroDTO());

        assertNull(resultado);
    }

    @Test
    void deleteDetalleCobro_exists_returnsTrue() {
        when(detalleCobroRepository.existsById(1L)).thenReturn(true);

        boolean resultado = detalleCobroService.deleteDetalleCobro(1L);

        assertTrue(resultado);
        verify(detalleCobroRepository).deleteById(1L);
    }

    @Test
    void deleteDetalleCobro_notFound_returnsFalse() {
        when(detalleCobroRepository.existsById(99L)).thenReturn(false);

        boolean resultado = detalleCobroService.deleteDetalleCobro(99L);

        assertFalse(resultado);
        verify(detalleCobroRepository, never()).deleteById(anyLong());
    }
}
