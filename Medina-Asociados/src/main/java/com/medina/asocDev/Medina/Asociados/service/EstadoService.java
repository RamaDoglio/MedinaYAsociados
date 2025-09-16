package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.EstadoDTO;
import com.medina.asocDev.Medina.Asociados.entity.Estado;
import com.medina.asocDev.Medina.Asociados.repo.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EstadoService {

    @Autowired
    private EstadoRepository estadoRepository;

    // Crear nuevo estado
    public EstadoDTO createEstado(EstadoDTO estadoDTO) {
        Estado estado = new Estado();
        estado.setAmbito(estadoDTO.getAmbito());
        estado.setNombreEstado(estadoDTO.getNombreEstado());
        
        Estado estadoGuardado = estadoRepository.save(estado);
        
        EstadoDTO result = new EstadoDTO();
        result.setIdEstado(estadoGuardado.getIdEstado());
        result.setAmbito(estadoGuardado.getAmbito());
        result.setNombreEstado(estadoGuardado.getNombreEstado());
        
        return result;
    }

    // Obtener todos los estados
    public List<EstadoDTO> getAllEstados() {
        List<Estado> estados = estadoRepository.findAll();
        return estados.stream()
                .map(estado -> {
                    EstadoDTO dto = new EstadoDTO();
                    dto.setIdEstado(estado.getIdEstado());
                    dto.setAmbito(estado.getAmbito());
                    dto.setNombreEstado(estado.getNombreEstado());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Obtener estado por ID
    public EstadoDTO getEstadoById(Long id) {
        Optional<Estado> estado = estadoRepository.findById(id);
        if (estado.isPresent()) {
            Estado est = estado.get();
            EstadoDTO dto = new EstadoDTO();
            dto.setIdEstado(est.getIdEstado());
            dto.setAmbito(est.getAmbito());
            dto.setNombreEstado(est.getNombreEstado());
            return dto;
        }
        return null;
    }

    // Obtener ID por nombre y ámbito (método específico del repositorio)
    public Long getIdEstadoByNameAndAmbito(String nombreEstado, String ambito) {
        return estadoRepository.findIdByNombreAndAmbito(nombreEstado, ambito);
    }

    // Actualizar estado
    public EstadoDTO updateEstado(Long id, EstadoDTO estadoDTO) {
        Optional<Estado> estadoExistente = estadoRepository.findById(id);
        if (estadoExistente.isPresent()) {
            Estado estado = estadoExistente.get();
            estado.setAmbito(estadoDTO.getAmbito());
            estado.setNombreEstado(estadoDTO.getNombreEstado());
            Estado estadoActualizado = estadoRepository.save(estado);
            
            EstadoDTO result = new EstadoDTO();
            result.setIdEstado(estadoActualizado.getIdEstado());
            result.setAmbito(estadoActualizado.getAmbito());
            result.setNombreEstado(estadoActualizado.getNombreEstado());
            
            return result;
        }
        return null;
    }

    // Eliminar estado
    public boolean deleteEstado(Long id) {
        if (estadoRepository.existsById(id)) {
            estadoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
