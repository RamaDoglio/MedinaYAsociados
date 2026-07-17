package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.EstadoDTO;
import com.medina.asocDev.Medina.Asociados.dto.HistorialTurnoDTO;
import com.medina.asocDev.Medina.Asociados.dto.TurnoDTO;
import com.medina.asocDev.Medina.Asociados.entity.Estado;
import com.medina.asocDev.Medina.Asociados.entity.HistorialTurno;
import com.medina.asocDev.Medina.Asociados.entity.Turno;
import com.medina.asocDev.Medina.Asociados.repo.HistorialTurnoRepository;
import com.medina.asocDev.Medina.Asociados.repo.TurnoRepository;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistorialTurnoService {

    @Autowired
    private HistorialTurnoRepository historialTurnoRepository;

    @Autowired
    private TurnoRepository turnoRepository;

    // Obtener todo el historial de un turno específico
    public List<HistorialTurnoDTO> getHistorialByTurnoId(Long idTurno) {
        List<HistorialTurno> historial = historialTurnoRepository.findByTurno_IdTurno(idTurno);
        return historial.stream()
                .map(Utils::mapHistorialTurnoEntityToDTO)
                .collect(Collectors.toList());
    }

    public EstadoDTO getEstadoActualByTurnoId(Long idTurno) {
        return turnoRepository.findById(idTurno)
                .map(Turno::getEstadoActual)              // obtengo el Estado directamente
                .map(Utils::mapEstadoEntityToDTO)  // lo paso a DTO con tu mapper
                .orElse(null);
    }

    // Obtener historial específico por ID
    public HistorialTurnoDTO getHistorialById(Long idHistorial) {
        Optional<HistorialTurno> historial = historialTurnoRepository.findById(idHistorial);
        return historial.map(Utils::mapHistorialTurnoEntityToDTO).orElse(null);
    }



    public void registrarCambio(Turno turno, Estado estadoAnterior, Estado estadoNuevo) {
        // 👉 Cerrar todos los historiales abiertos anteriores
        List<HistorialTurno> abiertos = historialTurnoRepository
                .findByTurno_IdTurnoAndFechaHoraFinIsNull(turno.getIdTurno());

        // Idempotencia: si el historial abierto ya tiene este estado, no duplicar
        boolean yaRegistrado = abiertos.stream()
                .anyMatch(h -> h.getEstadoHistorial().getIdEstado().equals(estadoNuevo.getIdEstado()));
        if (yaRegistrado) return;

        for (HistorialTurno h : abiertos) {
            if (h.getFechaHoraFin() == null) {
                h.setFechaHoraFin(LocalDateTime.now());
                historialTurnoRepository.save(h);
            }
        }

        // 👉 Crear el nuevo historial con fechaHoraFin = null
        HistorialTurno historial = new HistorialTurno();
        historial.setTurno(turno);
        historial.setFechaHoraInicio(LocalDateTime.now());
        historial.setEstadoHistorial(estadoNuevo);
        historial.setFechaHoraFin(null);

        historialTurnoRepository.save(historial);
    }
}
