package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.entity.Turno;
import com.medina.asocDev.Medina.Asociados.repo.TurnoRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class NotificacionTurnoService {

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TemplateEngine templateEngine;

    private static final String DIRECCION_ESTUDIO = "1256 América, Villa María, Córdoba";
    private static final Logger log = LoggerFactory.getLogger(NotificacionTurnoService.class);

    // =========================
    // MÉTODOS PRIVADOS DE APOYO
    // =========================

    private String formatearFecha(LocalDateTime fechaHora) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEEE d 'de' MMMM 'de' yyyy, HH:mm 'hs'", new Locale("es", "AR"));
        String texto = fechaHora.format(formatter);
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    private Context buildContext(Turno turno) {
        Context context = new Context(new Locale("es", "AR"));
        context.setVariable("nombreCliente", turno.getClienteTurno().getNombre());
        context.setVariable("fechaHora", formatearFecha(turno.getHorarioTurno()));
        context.setVariable("abogado", turno.getAbogadoTurno().getNombre() + " " + turno.getAbogadoTurno().getApellido());
        context.setVariable("direccion", DIRECCION_ESTUDIO);
        context.setVariable("direccionEncoded",
                URLEncoder.encode(DIRECCION_ESTUDIO, StandardCharsets.UTF_8));
        return context;
    }

    private void enviarCorreo(Turno turno, String plantilla, String asunto) throws MessagingException {
        String cuerpoHtml = templateEngine.process(plantilla, buildContext(turno));
        emailService.enviarNotificacionHtml(turno.getClienteTurno().getEmail(), asunto, cuerpoHtml);
    }

    // =========================
    // NOTIFICACIONES PUNTUALES
    // =========================

    public void enviarConfirmacionReserva(Turno turno) throws MessagingException {
        enviarCorreo(turno, "confirmacion", "✅ Confirmación de turno reservado");
    }

    public void enviarCancelacion(Turno turno) throws MessagingException {
        enviarCorreo(turno, "cancelacion", "❌ Cancelación de turno");
    }

    public void enviarReprogramacion(Turno turno) throws MessagingException {
        enviarCorreo(turno, "reprogramacion", "🔄 Reprogramación de turno");
    }

    // =========================
    // RECORDATORIOS AUTOMÁTICOS
    // =========================

    @Scheduled(cron = "0 0,45 12 * * *") // Ejecutar a 12:00 y 12:45
    @Scheduled(cron = "0 30 13 * * *") // Ejecutar a 13:30
    @Scheduled(cron = "0 15 14 * * *") // Ejecutar a 14:15
    @Scheduled(cron = "0 0,45 15 * * *")// Ejecutar a 15:00 y 15:45
    @Scheduled(cron = "0 30 16 * * *")// Ejecutar a 16:30
    public void enviarRecordatorios() throws MessagingException {
        log.info("Ejecutando tarea: enviarRecordatorios a {}", LocalDateTime.now());
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime dentroDe24h = ahora.plusHours(24);

        List<Turno> turnos = turnoRepository.findByHorarioTurnoBetween(ahora, dentroDe24h);

        for (Turno turno : turnos) {
            enviarCorreo(turno, "recordatorio", "📅 Recordatorio de turno");
        }
    }
}
