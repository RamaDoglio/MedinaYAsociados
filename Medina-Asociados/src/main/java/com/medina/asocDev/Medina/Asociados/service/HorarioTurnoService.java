package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.EstadoDTO;
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
import java.util.stream.Collectors;

@Service
public class HorarioTurnoService {

    @Autowired
    private HorarioTurnoRepository horarioTurnoRepository;

    // Crear nuevo horario de turno
    public HorarioTurnoDTO createHorarioTurno(HorarioTurnoDTO horarioTurnoDTO) {
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

    // Generar horarios disponibles para un día específico (plantilla)
    public List<HorarioTurnoDTO> generateHorariosDisponiblesParaFecha(LocalDate fecha, Long idAbogado) {
        List<HorarioTurnoDTO> horariosDisponibles = new ArrayList<>();
        
        // Obtener horarios ya ocupados
        List<HorarioTurnoDTO> horariosOcupados = getHorariosOcupadosPorAbogadoEnFecha(idAbogado, fecha);
        List<LocalTime> horasOcupadas = horariosOcupados.stream()
                .map(h -> h.getFechaHoraInicio().toLocalTime())
                .collect(Collectors.toList());
        
        // Generar horarios desde las 9:00 hasta las 18:00 cada 30 minutos
        LocalTime horaInicio = LocalTime.of(9, 0);
        LocalTime horaFin = LocalTime.of(18, 0);
        
        LocalTime horaActual = horaInicio;
        while (horaActual.isBefore(horaFin)) {
            if (!horasOcupadas.contains(horaActual)) {
                HorarioTurnoDTO horarioDisponible = new HorarioTurnoDTO();
                horarioDisponible.setFechaHoraInicio(LocalDateTime.of(fecha, horaActual));
                // Estado disponible por defecto
                EstadoDTO estadoDisponible = new EstadoDTO();
                estadoDisponible.setNombreEstado("DISPONIBLE");
                horarioDisponible.setEstadoHorario(estadoDisponible);
                
                horariosDisponibles.add(horarioDisponible);
            }
            horaActual = horaActual.plusMinutes(30);
        }
        
        return horariosDisponibles;
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
