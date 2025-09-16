package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.EstadoDTO;
import com.medina.asocDev.Medina.Asociados.dto.HistorialTurnoDTO;
import com.medina.asocDev.Medina.Asociados.dto.TurnoDTO;
import com.medina.asocDev.Medina.Asociados.entity.HistorialTurno;
import com.medina.asocDev.Medina.Asociados.repo.HistorialTurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistorialTurnoService {

    @Autowired
    private HistorialTurnoRepository historialTurnoRepository;

    // Obtener todo el historial de un turno específico
    public List<HistorialTurnoDTO> getHistorialByTurnoId(Long idTurno) {
        List<HistorialTurno> historial = historialTurnoRepository.findByTurno_IdTurno(idTurno);
        return historial.stream()
                .map(this::convertirEntityADTO)
                .collect(Collectors.toList());
    }

    // Obtener el estado actual de un turno (historial sin fecha fin)
    public HistorialTurnoDTO getEstadoActualByTurnoId(Long idTurno) {
        Optional<HistorialTurno> historialActual = historialTurnoRepository.findByTurno_IdTurnoAndFechaHoraFinIsNull(idTurno);
        return historialActual.map(this::convertirEntityADTO).orElse(null);
    }

    // Obtener historial específico por ID
    public HistorialTurnoDTO getHistorialById(Long idHistorial) {
        Optional<HistorialTurno> historial = historialTurnoRepository.findById(idHistorial);
        return historial.map(this::convertirEntityADTO).orElse(null);
    }

    // Obtener todo el historial
    public List<HistorialTurnoDTO> getAllHistorial() {
        List<HistorialTurno> historial = historialTurnoRepository.findAll();
        return historial.stream()
                .map(this::convertirEntityADTO)
                .collect(Collectors.toList());
    }

    // Método auxiliar para convertir Entity a DTO
    private HistorialTurnoDTO convertirEntityADTO(HistorialTurno historial) {
        HistorialTurnoDTO dto = new HistorialTurnoDTO();
        dto.setIdHistorial(historial.getIdHistorial());
        dto.setFechaHoraInicio(historial.getFechaHoraInicio());
        dto.setFechaHoraFin(historial.getFechaHoraFin());
        
        // Convertir Estado a EstadoDTO
        if (historial.getEstadoHistorial() != null) {
            EstadoDTO estadoDTO = new EstadoDTO();
            estadoDTO.setIdEstado(historial.getEstadoHistorial().getIdEstado());
            estadoDTO.setAmbito(historial.getEstadoHistorial().getAmbito());
            estadoDTO.setNombreEstado(historial.getEstadoHistorial().getNombreEstado());
            dto.setEstadoHistorial(estadoDTO);
        }
        
        // Convertir Turno a TurnoDTO (solo campos básicos para evitar recursión)
        if (historial.getTurno() != null) {
            TurnoDTO turnoDTO = new TurnoDTO();
            turnoDTO.setIdTurno(historial.getTurno().getIdTurno());
            // Agregar otros campos básicos del turno si es necesario
            dto.setTurno(turnoDTO);
        }
        
        return dto;
    }
}
