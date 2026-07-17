package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.entity.Parametro;
import com.medina.asocDev.Medina.Asociados.repo.ParametroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParametroServiceTest {

    @Mock
    private ParametroRepository parametroRepository;

    @InjectMocks
    private ParametroService parametroService;

    @Captor
    private ArgumentCaptor<Parametro> parametroCaptor;

    @Test
    void getValor_whenFound_shouldReturnValue() {
        Parametro param = new Parametro();
        param.setClave("iva");
        param.setValor("21");
        when(parametroRepository.findByClave("iva")).thenReturn(Optional.of(param));

        String result = parametroService.getValor("iva");

        assertEquals("21", result);
    }

    @Test
    void getValor_whenNotFound_shouldThrow() {
        when(parametroRepository.findByClave("inexistente")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> parametroService.getValor("inexistente"));
        assertTrue(ex.getMessage().contains("Parámetro no encontrado"));
    }

    @Test
    void setValor_whenExisting_shouldUpdateValue() {
        Parametro param = new Parametro();
        param.setClave("iva");
        param.setValor("21");
        when(parametroRepository.findByClave("iva")).thenReturn(Optional.of(param));

        parametroService.setValor("iva", "27");

        assertEquals("27", param.getValor());
        verify(parametroRepository).save(param);
    }

    @Test
    void setValor_whenNew_shouldCreateAndSave() {
        when(parametroRepository.findByClave("iva")).thenReturn(Optional.empty());
        when(parametroRepository.save(any(Parametro.class))).thenAnswer(i -> i.getArgument(0));

        parametroService.setValor("iva", "21");

        verify(parametroRepository).save(parametroCaptor.capture());
        Parametro saved = parametroCaptor.getValue();
        assertEquals("iva", saved.getClave());
        assertEquals("21", saved.getValor());
    }

    @Test
    void setValor_whenNew_shouldNotHaveId() {
        when(parametroRepository.findByClave("tasa")).thenReturn(Optional.empty());
        when(parametroRepository.save(any(Parametro.class))).thenAnswer(i -> i.getArgument(0));

        parametroService.setValor("tasa", "10.5");

        verify(parametroRepository).save(parametroCaptor.capture());
        Parametro saved = parametroCaptor.getValue();
        assertNull(saved.getId());
        assertEquals("tasa", saved.getClave());
        assertEquals("10.5", saved.getValor());
    }
}
