package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.entity.Turno;
import com.medina.asocDev.Medina.Asociados.repo.TurnoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SchedulerService {

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired TurnoService turnoService;

    // Expirar turnos reservados que no se pagaron en 15 minutos
    @Scheduled(fixedRate = 60000) // cada 1 minuto
    @Transactional
    public void expirarTurnosReservados() {
        List<Turno> reservados = turnoRepository.findByEstadoActualNombreEstado("RESERVADO");
        LocalDateTime limite = LocalDateTime.now().minusMinutes(15);

        for (Turno turno : reservados) {
            if (turno.getHorarioTurno().isBefore(limite)) {
                // En tu caso decidiste no registrar historial ni marcar como cancelado
                turnoRepository.delete(turno);
            }
        }
    }

    // Iniciar automáticamente turnos pagados o reprogramados cuya hora ya llegó
    @Scheduled(fixedRate = 60000) // cada 1 minuto
    @Transactional
    public void iniciarTurnosAutomaticamente() {
        LocalDateTime ahora = LocalDateTime.now();

        List<Turno> turnos = turnoRepository.findByEstadoActualNombreEstadoIn(
                List.of("PAGADO", "REPROGRAMADO")
        );

        for (Turno turno : turnos) {
            if (!turno.getHorarioTurno().isAfter(ahora)) {
                if (!turno.getEstadoActual().getNombreEstado().equals("EN_CURSO")) {
                    // Reutilizamos la lógica de transición ya validada en el service
                    turnoService.marcarEnCurso(turno.getIdTurno());
                }
            }
        }
    }
}
