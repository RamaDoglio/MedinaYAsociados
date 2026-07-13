package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.entity.Estado;
import com.medina.asocDev.Medina.Asociados.entity.HistorialTurno;
import com.medina.asocDev.Medina.Asociados.entity.Turno;
import com.medina.asocDev.Medina.Asociados.repo.EstadoRepository;
import com.medina.asocDev.Medina.Asociados.repo.HistorialTurnoRepository;
import com.medina.asocDev.Medina.Asociados.repo.TokenBlacklistedRepository;
import com.medina.asocDev.Medina.Asociados.repo.TurnoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SchedulerService {

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private TurnoService turnoService;

    @Autowired
    private HistorialTurnoService historialTurnoService;

    @Autowired
    private HistorialTurnoRepository historialTurnoRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private TokenBlacklistedRepository tokenBlacklistedRepository;

    // Expirar turnos reservados que no se pagaron en 15 minutos
    @Scheduled(fixedDelay = 60000) // espera 1 minuto después de terminar
    @Transactional
    public void expirarTurnosReservados() {
        Estado reservado = estadoRepository
                .findByNombreAndAmbito("RESERVADO","TURNO")
                .orElseThrow(() -> new RuntimeException("Estado RESERVADO no encontrado"));

        List<HistorialTurno> reservados = historialTurnoRepository.findByEstadoHistorial_IdEstadoAndFechaHoraFinIsNull(reservado.getIdEstado());
        LocalDateTime limite = LocalDateTime.now().minusMinutes(15);

        Estado expirado = estadoRepository
                .findByNombreAndAmbito("EXPIRO_PAGO","TURNO")
                .orElseThrow(() -> new RuntimeException("Estado EXPIRADO no encontrado"));

        for (HistorialTurno historial : reservados) {
            if (historial.getFechaHoraInicio().isBefore(limite)) {
                Turno turno = historial.getTurno();
                historialTurnoService.registrarCambio(turno, historial.getEstadoHistorial(), expirado);
                turno.setEstadoActual(expirado);
                turnoRepository.save(turno);
            }
        }
    }


    @Scheduled(cron = "0 0,45 12 * * *") // Ejecutar a 12:00 y 12:45
    @Scheduled(cron = "0 30 13 * * *") // Ejecutar a 13:30
    @Scheduled(cron = "0 15 14 * * *") // Ejecutar a 14:15
    @Scheduled(cron = "0 0,45 15 * * *")// Ejecutar a 15:00 y 15:45
    @Scheduled(cron = "0 30 16 * * *")// Ejecutar a 16:30
    @Transactional
    public void iniciarTurnosAutomaticamente() {
        LocalDateTime ahora = LocalDateTime.now();

        List<Turno> turnos = turnoRepository.findByEstadoActualNombreEstadoIn(
                List.of("PAGADO", "REPROGRAMADO")
        );

        for (Turno turno : turnos) {
            if (!turno.getHorarioTurno().isAfter(ahora)) {
                if (!turno.getEstadoActual().getNombreEstado().equals("EN_CURSO")) {
                    turnoService.marcarEnCurso(turno.getIdTurno());
                }
            }
        }
    }

    // Limpiar tokens expirados de la blacklist (todos los días a las 3 AM)
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void limpiarTokenBlacklist() {
        tokenBlacklistedRepository.deleteByFechaExpiracionBefore(new Date());
    }

}
