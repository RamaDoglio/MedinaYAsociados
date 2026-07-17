package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.LocalidadDTO;
import com.medina.asocDev.Medina.Asociados.entity.Localidad;
import com.medina.asocDev.Medina.Asociados.repo.LocalidadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalidadServiceTest {

    @Mock
    private LocalidadRepository localidadRepository;

    @InjectMocks
    private LocalidadService localidadService;

    @Test
    void getAllLocalidades_shouldReturnList() {
        Localidad loc1 = new Localidad();
        loc1.setIdLocalidad(1L);
        loc1.setNombreLocalidad("Córdoba");
        loc1.setCodigoPostal("5000");

        Localidad loc2 = new Localidad();
        loc2.setIdLocalidad(2L);
        loc2.setNombreLocalidad("Buenos Aires");
        loc2.setCodigoPostal("1000");

        when(localidadRepository.findAllLocalidades()).thenReturn(List.of(loc1, loc2));

        List<LocalidadDTO> result = localidadService.getAllLocalidades();

        assertEquals(2, result.size());
        assertEquals("Córdoba", result.get(0).getNombreLocalidad());
        assertEquals("5000", result.get(0).getCodigoPostal());
        assertEquals("Buenos Aires", result.get(1).getNombreLocalidad());
        assertEquals("1000", result.get(1).getCodigoPostal());
        verify(localidadRepository).findAllLocalidades();
    }

    @Test
    void getAllLocalidades_whenEmpty_shouldReturnEmptyList() {
        when(localidadRepository.findAllLocalidades()).thenReturn(List.of());

        List<LocalidadDTO> result = localidadService.getAllLocalidades();

        assertTrue(result.isEmpty());
        verify(localidadRepository).findAllLocalidades();
    }

    @Test
    void getAllLocalidades_shouldMapUsingUtils() {
        Localidad loc = new Localidad();
        loc.setIdLocalidad(10L);
        loc.setNombreLocalidad("Rosario");
        loc.setCodigoPostal("2000");

        when(localidadRepository.findAllLocalidades()).thenReturn(List.of(loc));

        List<LocalidadDTO> result = localidadService.getAllLocalidades();

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getIdLocalidad());
        assertEquals("Rosario", result.get(0).getNombreLocalidad());
        assertEquals("2000", result.get(0).getCodigoPostal());
    }
}
