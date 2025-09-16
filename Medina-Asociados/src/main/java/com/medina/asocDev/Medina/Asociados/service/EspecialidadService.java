package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.EspecialidadDTO;
import com.medina.asocDev.Medina.Asociados.entity.Especialidad;
import com.medina.asocDev.Medina.Asociados.repo.EspecialidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EspecialidadService {

    @Autowired
    private EspecialidadRepository especialidadRepository;

    // Crear nueva especialidad
    public EspecialidadDTO createEspecialidad(EspecialidadDTO especialidadDTO) {
        Especialidad especialidad = new Especialidad();
        especialidad.setNombreEspecialidad(especialidadDTO.getNombreEspecialidad());
        especialidad.setDescripcionEspecialidad(especialidadDTO.getDescripcionEspecialidad());
        
        Especialidad especialidadGuardada = especialidadRepository.save(especialidad);
        
        EspecialidadDTO result = new EspecialidadDTO();
        result.setIdEspecialidad(especialidadGuardada.getIdEspecialidad());
        result.setNombreEspecialidad(especialidadGuardada.getNombreEspecialidad());
        result.setDescripcionEspecialidad(especialidadGuardada.getDescripcionEspecialidad());
        
        return result;
    }

    // Obtener todas las especialidades
    public List<EspecialidadDTO> getAllEspecialidades() {
        List<Especialidad> especialidades = especialidadRepository.findAll();
        return especialidades.stream()
                .map(especialidad -> {
                    EspecialidadDTO dto = new EspecialidadDTO();
                    dto.setIdEspecialidad(especialidad.getIdEspecialidad());
                    dto.setNombreEspecialidad(especialidad.getNombreEspecialidad());
                    dto.setDescripcionEspecialidad(especialidad.getDescripcionEspecialidad());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Obtener especialidad por ID
    public EspecialidadDTO getEspecialidadById(Long id) {
        Optional<Especialidad> especialidad = especialidadRepository.findById(id);
        if (especialidad.isPresent()) {
            Especialidad esp = especialidad.get();
            EspecialidadDTO dto = new EspecialidadDTO();
            dto.setIdEspecialidad(esp.getIdEspecialidad());
            dto.setNombreEspecialidad(esp.getNombreEspecialidad());
            dto.setDescripcionEspecialidad(esp.getDescripcionEspecialidad());
            return dto;
        }
        return null;
    }

    // Obtener especialidad por nombre
    public EspecialidadDTO getEspecialidadByName(String nombreEspecialidad) {
        Especialidad especialidad = especialidadRepository.findByNombreEspecialidad(nombreEspecialidad);
        if (especialidad != null) {
            EspecialidadDTO dto = new EspecialidadDTO();
            dto.setIdEspecialidad(especialidad.getIdEspecialidad());
            dto.setNombreEspecialidad(especialidad.getNombreEspecialidad());
            dto.setDescripcionEspecialidad(especialidad.getDescripcionEspecialidad());
            return dto;
        }
        return null;
    }

    // Actualizar especialidad
    public EspecialidadDTO updateEspecialidad(Long id, EspecialidadDTO especialidadDTO) {
        Optional<Especialidad> especialidadExistente = especialidadRepository.findById(id);
        if (especialidadExistente.isPresent()) {
            Especialidad especialidad = especialidadExistente.get();
            especialidad.setNombreEspecialidad(especialidadDTO.getNombreEspecialidad());
            especialidad.setDescripcionEspecialidad(especialidadDTO.getDescripcionEspecialidad());
            Especialidad especialidadActualizada = especialidadRepository.save(especialidad);
            
            EspecialidadDTO result = new EspecialidadDTO();
            result.setIdEspecialidad(especialidadActualizada.getIdEspecialidad());
            result.setNombreEspecialidad(especialidadActualizada.getNombreEspecialidad());
            result.setDescripcionEspecialidad(especialidadActualizada.getDescripcionEspecialidad());
            
            return result;
        }
        return null;
    }

    // Eliminar especialidad
    public boolean deleteEspecialidad(Long id) {
        if (especialidadRepository.existsById(id)) {
            especialidadRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
