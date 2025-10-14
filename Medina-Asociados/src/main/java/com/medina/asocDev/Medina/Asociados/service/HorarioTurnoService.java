package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.HorarioTurnoDTO;
import com.medina.asocDev.Medina.Asociados.entity.HorarioTurno;
import com.medina.asocDev.Medina.Asociados.repo.HorarioTurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HorarioTurnoService {

    @Autowired
    private static HorarioTurnoRepository horarioTurnoRepository;

    // Crear nuevo horario de turno
    public static HorarioTurnoDTO createHorarioTurno(HorarioTurno horarioTurnoDTO) {
        HorarioTurno horarioTurno = new HorarioTurno();
        horarioTurno.setFechaHoraInicio(horarioTurnoDTO.getFechaHoraInicio());
        // El turno y estado se asignarán cuando se reserve el horario
        
        HorarioTurno horarioGuardado = horarioTurnoRepository.save(horarioTurno);
        
        HorarioTurnoDTO result = new HorarioTurnoDTO();
        result.setIdHorarioTurno(horarioGuardado.getIdHorarioTurno());
        result.setFechaHoraInicio(horarioGuardado.getFechaHoraInicio());
        // Asignar estado y turno si es necesario
        
        return result;
    }

    // Obtener todos los horarios disponibles
    public List<HorarioTurnoDTO> getAllHorarios() {
        List<HorarioTurno> horarios = horarioTurnoRepository.findAll();
        return horarios.stream()
                .map(horario -> {
                    HorarioTurnoDTO dto = new HorarioTurnoDTO();
                    dto.setIdHorarioTurno(horario.getIdHorarioTurno());
                    dto.setFechaHoraInicio(horario.getFechaHoraInicio());
                    // Asignar estado y turno si es necesario
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Obtener horario específico por ID
    public HorarioTurnoDTO getHorarioById(Long id) {
        Optional<HorarioTurno> horario = horarioTurnoRepository.findById(id);
        if (horario.isPresent()) {
            HorarioTurno h = horario.get();
            HorarioTurnoDTO dto = new HorarioTurnoDTO();
            dto.setIdHorarioTurno(h.getIdHorarioTurno());
            dto.setFechaHoraInicio(h.getFechaHoraInicio());
            // Asignar estado y turno si es necesario
            return dto;
        }
        return null;
    }

    // Obtener horarios ocupados de un abogado en una fecha específica
    public List<HorarioTurnoDTO> getHorariosOcupadosPorAbogadoEnFecha(Long idAbogado, LocalDate fecha) {
        LocalDateTime fechaDateTime = fecha.atStartOfDay();
        List<HorarioTurno> horariosOcupados = horarioTurnoRepository.findHorariosOcupadosPorAbogadoEnFecha(idAbogado, fechaDateTime);
        return horariosOcupados.stream()
                .map(horario -> {
                    HorarioTurnoDTO dto = new HorarioTurnoDTO();
                    dto.setIdHorarioTurno(horario.getIdHorarioTurno());
                    dto.setFechaHoraInicio(horario.getFechaHoraInicio());
                    // Asignar estado y turno si es necesario
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<LocalTime> obtenerHorariosDisponibles(Long idAbogado, LocalDateTime fecha) {
        // Generar todos los horarios posibles dinámicamente (12:00 → 16:30, cada 45 min)
        List<LocalTime> todosLosHorarios = new ArrayList<>();
        LocalTime horaInicio = LocalTime.of(12, 0);
        LocalTime horaFin = LocalTime.of(16, 30);

        LocalTime horaActual = horaInicio;
        while (!horaActual.isAfter(horaFin)) {
            todosLosHorarios.add(horaActual);
            horaActual = horaActual.plusMinutes(45);
        }

        // Horarios ocupados desde la BD
        List<HorarioTurno> ocupados = horarioTurnoRepository
                .findHorariosOcupadosPorAbogadoEnFecha(idAbogado, fecha);

        Set<LocalTime> horasOcupadas = ocupados.stream()
                .map(h -> h.getFechaHoraInicio().toLocalTime())
                .collect(Collectors.toSet());

        // Filtrar solo los horarios libres
        return todosLosHorarios.stream()
                .filter(hora -> !horasOcupadas.contains(hora))
                .toList();
    }

    // Verificar si un horario está disponible
    public boolean isHorarioDisponible(LocalDateTime fechaHora, Long idAbogado) {
        LocalDate fecha = fechaHora.toLocalDate();
        List<HorarioTurnoDTO> horariosOcupados = getHorariosOcupadosPorAbogadoEnFecha(idAbogado, fecha);
        
        return horariosOcupados.stream()
                .noneMatch(h -> h.getFechaHoraInicio().equals(fechaHora));
    }

    // Actualizar horario (para cambiar estado cuando se reserva)
    public HorarioTurnoDTO updateHorarioTurno(Long id, HorarioTurnoDTO horarioTurnoDTO) {
        Optional<HorarioTurno> horarioExistente = horarioTurnoRepository.findById(id);
        if (horarioExistente.isPresent()) {
            HorarioTurno horario = horarioExistente.get();
            horario.setFechaHoraInicio(horarioTurnoDTO.getFechaHoraInicio());
            // Actualizar otros campos según sea necesario
            
            HorarioTurno horarioActualizado = horarioTurnoRepository.save(horario);
            HorarioTurnoDTO result = new HorarioTurnoDTO();
            result.setIdHorarioTurno(horarioActualizado.getIdHorarioTurno());
            result.setFechaHoraInicio(horarioActualizado.getFechaHoraInicio());
            // Asignar estado y turno si es necesario
            
            return result;
        }
        return null;
    }

    // Eliminar horario
    public boolean deleteHorarioTurno(Long id) {
        if (horarioTurnoRepository.existsById(id)) {
            horarioTurnoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
