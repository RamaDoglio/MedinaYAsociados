package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.EspecialidadDTO;
import com.medina.asocDev.Medina.Asociados.entity.Especialidad;
import com.medina.asocDev.Medina.Asociados.repo.EspecialidadRepository;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EspecialidadServiceTest {

    @Mock
    private EspecialidadRepository especialidadRepository;

    @InjectMocks
    private EspecialidadService especialidadService;

    @Test
    void createEspecialidad_datosValidos_returnsDTO() {
        EspecialidadDTO dto = new EspecialidadDTO();
        dto.setNombreEspecialidad("Penal");
        dto.setDescripcionEspecialidad("Derecho penal");

        Especialidad entity = new Especialidad();
        entity.setIdEspecialidad(1L);
        entity.setNombreEspecialidad("Penal");
        entity.setDescripcionEspecialidad("Derecho penal");

        when(especialidadRepository.save(any(Especialidad.class))).thenReturn(entity);

        EspecialidadDTO result = especialidadService.createEspecialidad(dto);

        assertNotNull(result);
        assertEquals(1L, result.getIdEspecialidad());
        assertEquals("Penal", result.getNombreEspecialidad());
        assertEquals("Derecho penal", result.getDescripcionEspecialidad());
        verify(especialidadRepository).save(any(Especialidad.class));
    }

    @Test
    void getAllEspecialidades_returnsPagedDTOs() {
        Especialidad especialidad = new Especialidad();
        especialidad.setIdEspecialidad(1L);
        especialidad.setNombreEspecialidad("Penal");
        especialidad.setDescripcionEspecialidad("Derecho penal");

        Pageable pageable = PageRequest.of(0, 10);
        when(especialidadRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(especialidad)));

        Page<EspecialidadDTO> result = especialidadService.getAllEspecialidades(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Penal", result.getContent().get(0).getNombreEspecialidad());
    }

    @Test
    void getEspecialidadById_idExiste_returnsDTO() {
        Especialidad especialidad = new Especialidad();
        especialidad.setIdEspecialidad(1L);
        especialidad.setNombreEspecialidad("Penal");
        especialidad.setDescripcionEspecialidad("Derecho penal");

        when(especialidadRepository.findById(1L)).thenReturn(Optional.of(especialidad));

        EspecialidadDTO result = especialidadService.getEspecialidadById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getIdEspecialidad());
        assertEquals("Penal", result.getNombreEspecialidad());
    }

    @Test
    void getEspecialidadById_idNoExiste_returnsNull() {
        when(especialidadRepository.findById(99L)).thenReturn(Optional.empty());

        EspecialidadDTO result = especialidadService.getEspecialidadById(99L);

        assertNull(result);
    }

    @Test
    void getEspecialidadByName_nombreExiste_returnsDTO() {
        Especialidad especialidad = new Especialidad();
        especialidad.setIdEspecialidad(1L);
        especialidad.setNombreEspecialidad("Penal");
        especialidad.setDescripcionEspecialidad("Derecho penal");

        when(especialidadRepository.findByNombreEspecialidad("Penal"))
                .thenReturn(Optional.of(especialidad));

        EspecialidadDTO result = especialidadService.getEspecialidadByName("Penal");

        assertNotNull(result);
        assertEquals(1L, result.getIdEspecialidad());
        assertEquals("Penal", result.getNombreEspecialidad());
    }

    @Test
    void getEspecialidadByName_nombreNoExiste_throwsEntityNotFoundException() {
        when(especialidadRepository.findByNombreEspecialidad("Inexistente"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> especialidadService.getEspecialidadByName("Inexistente"));
    }

    @Test
    void updateEspecialidad_idExiste_updatesAndReturnsDTO() {
        Especialidad existing = new Especialidad();
        existing.setIdEspecialidad(1L);
        existing.setNombreEspecialidad("Penal");
        existing.setDescripcionEspecialidad("Derecho penal");

        EspecialidadDTO dto = new EspecialidadDTO();
        dto.setNombreEspecialidad("Civil");
        dto.setDescripcionEspecialidad("Derecho civil");

        Especialidad updated = new Especialidad();
        updated.setIdEspecialidad(1L);
        updated.setNombreEspecialidad("Civil");
        updated.setDescripcionEspecialidad("Derecho civil");

        when(especialidadRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(especialidadRepository.save(any(Especialidad.class))).thenReturn(updated);

        EspecialidadDTO result = especialidadService.updateEspecialidad(1L, dto);

        assertNotNull(result);
        assertEquals(1L, result.getIdEspecialidad());
        assertEquals("Civil", result.getNombreEspecialidad());
        assertEquals("Derecho civil", result.getDescripcionEspecialidad());
        verify(especialidadRepository).save(any(Especialidad.class));
    }

    @Test
    void updateEspecialidad_idNoExiste_returnsNull() {
        EspecialidadDTO dto = new EspecialidadDTO();
        dto.setNombreEspecialidad("Civil");

        when(especialidadRepository.findById(99L)).thenReturn(Optional.empty());

        EspecialidadDTO result = especialidadService.updateEspecialidad(99L, dto);

        assertNull(result);
        verify(especialidadRepository, never()).save(any(Especialidad.class));
    }

    @Test
    void deleteEspecialidad_idExiste_returnsTrue() {
        when(especialidadRepository.existsById(1L)).thenReturn(true);

        boolean result = especialidadService.deleteEspecialidad(1L);

        assertTrue(result);
        verify(especialidadRepository).deleteById(1L);
    }

    @Test
    void deleteEspecialidad_idNoExiste_returnsFalse() {
        when(especialidadRepository.existsById(99L)).thenReturn(false);

        boolean result = especialidadService.deleteEspecialidad(99L);

        assertFalse(result);
        verify(especialidadRepository, never()).deleteById(anyLong());
    }
}
