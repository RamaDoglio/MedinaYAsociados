package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.entity.TipoCobro;
import com.medina.asocDev.Medina.Asociados.repo.TipoCobroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoCobroServiceTest {

    @Mock
    private TipoCobroRepository tipoCobroRepository;

    @InjectMocks
    private TipoCobroService tipoCobroService;

    @Test
    void listarTodos_shouldReturnList() {
        when(tipoCobroRepository.findAll()).thenReturn(List.of(new TipoCobro(), new TipoCobro()));

        List<TipoCobro> result = tipoCobroService.listarTodos();

        assertEquals(2, result.size());
        verify(tipoCobroRepository).findAll();
    }

    @Test
    void listarTodos_whenEmpty_shouldReturnEmptyList() {
        when(tipoCobroRepository.findAll()).thenReturn(List.of());

        List<TipoCobro> result = tipoCobroService.listarTodos();

        assertTrue(result.isEmpty());
        verify(tipoCobroRepository).findAll();
    }

    @Test
    void buscarPorId_whenFound_shouldReturnEntity() {
        TipoCobro tc = new TipoCobro();
        tc.setIdTipoCobro(1L);
        tc.setNombreTipoCobro("Efectivo");
        when(tipoCobroRepository.findById(1L)).thenReturn(Optional.of(tc));

        TipoCobro result = tipoCobroService.buscarPorId(1L);

        assertEquals(1L, result.getIdTipoCobro());
        assertEquals("Efectivo", result.getNombreTipoCobro());
    }

    @Test
    void buscarPorId_whenNotFound_shouldThrow() {
        when(tipoCobroRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> tipoCobroService.buscarPorId(99L));
        assertTrue(ex.getMessage().contains("TipoCobro no encontrado"));
    }

    @Test
    void buscarPorNombre_whenFound_shouldReturnEntity() {
        TipoCobro tc = new TipoCobro();
        tc.setNombreTipoCobro("Transferencia");
        tc.setDescTipoCobro("Transferencia bancaria");
        when(tipoCobroRepository.findByNombreTipoCobro("Transferencia")).thenReturn(tc);

        TipoCobro result = tipoCobroService.buscarPorNombre("Transferencia");

        assertNotNull(result);
        assertEquals("Transferencia", result.getNombreTipoCobro());
        assertEquals("Transferencia bancaria", result.getDescTipoCobro());
    }

    @Test
    void buscarPorNombre_whenNotFound_shouldReturnNull() {
        when(tipoCobroRepository.findByNombreTipoCobro("Inexistente")).thenReturn(null);

        TipoCobro result = tipoCobroService.buscarPorNombre("Inexistente");

        assertNull(result);
    }

    @Test
    void crear_shouldSaveAndReturn() {
        TipoCobro tc = new TipoCobro();
        tc.setNombreTipoCobro("Efectivo");
        when(tipoCobroRepository.save(tc)).thenReturn(tc);

        TipoCobro result = tipoCobroService.crear(tc);

        assertNotNull(result);
        assertEquals("Efectivo", result.getNombreTipoCobro());
        verify(tipoCobroRepository).save(tc);
    }

    @Test
    void actualizar_shouldUpdateFieldsAndSave() {
        TipoCobro existente = new TipoCobro();
        existente.setIdTipoCobro(1L);
        existente.setNombreTipoCobro("Efectivo");
        existente.setDescTipoCobro("Original");

        TipoCobro datos = new TipoCobro();
        datos.setNombreTipoCobro("Transferencia");
        datos.setDescTipoCobro("Actualizado");

        when(tipoCobroRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(tipoCobroRepository.save(existente)).thenReturn(existente);

        TipoCobro result = tipoCobroService.actualizar(1L, datos);

        assertEquals("Transferencia", result.getNombreTipoCobro());
        assertEquals("Actualizado", result.getDescTipoCobro());
        verify(tipoCobroRepository).save(existente);
    }

    @Test
    void actualizar_whenNotFound_shouldThrow() {
        when(tipoCobroRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> tipoCobroService.actualizar(1L, new TipoCobro()));
    }

    @Test
    void eliminar_shouldDeleteById() {
        tipoCobroService.eliminar(1L);

        verify(tipoCobroRepository).deleteById(1L);
    }
}
