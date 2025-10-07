package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.entity.Turno;
import com.medina.asocDev.Medina.Asociados.repo.CobroRepository;
import com.medina.asocDev.Medina.Asociados.repo.EstadoRepository;
import com.medina.asocDev.Medina.Asociados.repo.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TurnoService {

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private CobroRepository cobroRepository;

    // Crear turno (reserva)
    public Turno crearTurno(Turno turno) {
        LocalDateTime fechaTurno = turno.getHorarioTurno().getFechaHora();

        if (fechaTurno.isBefore(LocalDateTime.now().plusHours(24))) {
            throw new IllegalArgumentException("La reserva debe hacerse con al menos 24 horas de antelación");
        }

        turno.setEstadoActual(estadoRepository.findByNombre("RESERVADO")
                .orElseThrow(() -> new RuntimeException("Estado RESERVADO no encontrado")));

        return turnoRepository.save(turno);
    }

    // Listar todos
    public List<Turno> listarTurnos() {
        return turnoRepository.findAll();
    }

    // Obtener por id
    public Turno obtenerPorId(Long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
    }

    // Actualizar observaciones
    public Turno actualizarTurno(Long id, Turno datos) {
        Turno turno = obtenerPorId(id);
        turno.setObservacionesCliente(datos.getObservacionesCliente());
        turno.setObservacionesAbogado(datos.getObservacionesAbogado());
        return turnoRepository.save(turno);
    }

    // Eliminar
    public void eliminarTurno(Long id) {
        turnoRepository.deleteById(id);
    }

    // Reprogramar
    public Turno reprogramarTurno(Long id, LocalDateTime nuevaFechaHora) {
        Turno turno = obtenerPorId(id);
        LocalDateTime fechaActual = turno.getHorarioTurno().getFechaHora();

        if (fechaActual.isBefore(LocalDateTime.now().plusHours(24))) {
            throw new IllegalArgumentException("No se puede reprogramar con menos de 24 horas de anticipación");
        }
        if (nuevaFechaHora.isBefore(LocalDateTime.now().plusHours(24))) {
            throw new IllegalArgumentException("La nueva fecha debe tener al menos 24 horas de antelación");
        }

        turno.getHorarioTurno().setFechaHora(nuevaFechaHora);
        turno.setEstadoActual(estadoRepository.findByNombre("REPROGRAMADO")
                .orElseThrow(() -> new RuntimeException("Estado REPROGRAMADO no encontrado")));

        return turnoRepository.save(turno);
    }

    // Cancelar
    public Turno cancelarTurno(Long id) {
        Turno turno = obtenerPorId(id);
        LocalDateTime fechaTurno = turno.getHorarioTurno().getFechaHora();

        if (fechaTurno.isBefore(LocalDateTime.now().plusHours(24))) {
            // No hay reembolso
            turno.setEstadoActual(estadoRepository.findByNombre("CANCELADO_SIN_REEMBOLSO")
                    .orElseThrow(() -> new RuntimeException("Estado CANCELADO_SIN_REEMBOLSO no encontrado")));
        } else {
            // Con reembolso
            turno.setEstadoActual(estadoRepository.findByNombre("CANCELADO_CON_REEMBOLSO")
                    .orElseThrow(() -> new RuntimeException("Estado CANCELADO_CON_REEMBOLSO no encontrado")));

            if (turno.getCobro() != null) {
                turno.getCobro().setReembolsado(true);
                cobroRepository.save(turno.getCobro());
            }
        }

        return turnoRepository.save(turno);
    }
}


