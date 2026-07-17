package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.EstadoDTO;
import com.medina.asocDev.Medina.Asociados.entity.Estado;
import com.medina.asocDev.Medina.Asociados.repo.EstadoRepository;
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
class EstadoServiceTest {

    @Mock
    private EstadoRepository estadoRepository;

    @InjectMocks
    private EstadoService estadoService;

    @Test
    void createEstado_datosValidos_returnsDTO() {
        EstadoDTO dto = new EstadoDTO();
        dto.setAmbito("TURNO");
        dto.setNombreEstado("RESERVADO");

        Estado entity = new Estado();
        entity.setIdEstado(1L);
        entity.setAmbito("TURNO");
        entity.setNombreEstado("RESERVADO");

        when(estadoRepository.save(any(Estado.class))).thenReturn(entity);

        EstadoDTO result = estadoService.createEstado(dto);

        assertNotNull(result);
        assertEquals(1L, result.getIdEstado());
        assertEquals("TURNO", result.getAmbito());
        assertEquals("RESERVADO", result.getNombreEstado());
        verify(estadoRepository).save(any(Estado.class));
    }

    @Test
    void getAllEstados_returnsListOfDTOs() {
        Estado estado1 = new Estado();
        estado1.setIdEstado(1L);
        estado1.setAmbito("TURNO");
        estado1.setNombreEstado("RESERVADO");

        Estado estado2 = new Estado();
        estado2.setIdEstado(2L);
        estado2.setAmbito("TURNO");
        estado2.setNombreEstado("PAGADO");

        when(estadoRepository.findAll()).thenReturn(List.of(estado1, estado2));

        List<EstadoDTO> result = estadoService.getAllEstados();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("RESERVADO", result.get(0).getNombreEstado());
        assertEquals("PAGADO", result.get(1).getNombreEstado());
    }

    @Test
    void getEstadoById_idExiste_returnsDTO() {
        Estado estado = new Estado();
        estado.setIdEstado(1L);
        estado.setAmbito("TURNO");
        estado.setNombreEstado("RESERVADO");

        when(estadoRepository.findById(1L)).thenReturn(Optional.of(estado));

        EstadoDTO result = estadoService.getEstadoById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getIdEstado());
        assertEquals("RESERVADO", result.getNombreEstado());
    }

    @Test
    void getEstadoById_idNoExiste_returnsNull() {
        when(estadoRepository.findById(99L)).thenReturn(Optional.empty());

        EstadoDTO result = estadoService.getEstadoById(99L);

        assertNull(result);
    }

    @Test
    void updateEstado_idExiste_updatesAndReturnsDTO() {
        Estado existing = new Estado();
        existing.setIdEstado(1L);
        existing.setAmbito("TURNO");
        existing.setNombreEstado("RESERVADO");

        EstadoDTO dto = new EstadoDTO();
        dto.setAmbito("COBRO");
        dto.setNombreEstado("PAGADO");

        Estado updated = new Estado();
        updated.setIdEstado(1L);
        updated.setAmbito("COBRO");
        updated.setNombreEstado("PAGADO");

        when(estadoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(estadoRepository.save(any(Estado.class))).thenReturn(updated);

        EstadoDTO result = estadoService.updateEstado(1L, dto);

        assertNotNull(result);
        assertEquals(1L, result.getIdEstado());
        assertEquals("COBRO", result.getAmbito());
        assertEquals("PAGADO", result.getNombreEstado());
        verify(estadoRepository).save(any(Estado.class));
    }

    @Test
    void updateEstado_idNoExiste_returnsNull() {
        EstadoDTO dto = new EstadoDTO();
        dto.setAmbito("COBRO");
        dto.setNombreEstado("PAGADO");

        when(estadoRepository.findById(99L)).thenReturn(Optional.empty());

        EstadoDTO result = estadoService.updateEstado(99L, dto);

        assertNull(result);
        verify(estadoRepository, never()).save(any(Estado.class));
    }

    @Test
    void deleteEstado_idExiste_returnsTrue() {
        when(estadoRepository.existsById(1L)).thenReturn(true);

        boolean result = estadoService.deleteEstado(1L);

        assertTrue(result);
        verify(estadoRepository).deleteById(1L);
    }

    @Test
    void deleteEstado_idNoExiste_returnsFalse() {
        when(estadoRepository.existsById(99L)).thenReturn(false);

        boolean result = estadoService.deleteEstado(99L);

        assertFalse(result);
        verify(estadoRepository, never()).deleteById(anyLong());
    }
}
