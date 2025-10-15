package com.medina.asocDev.Medina.Asociados.controller;


import com.medina.asocDev.Medina.Asociados.dto.TurnoCreateRequest;
import com.medina.asocDev.Medina.Asociados.dto.TurnoDTO;
import com.medina.asocDev.Medina.Asociados.entity.Turno;
import com.medina.asocDev.Medina.Asociados.service.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    @Autowired
    private TurnoService turnoService;

    // ✅ Crear turno (reserva)
    @PostMapping
    public ResponseEntity<Turno> crearTurno(@RequestBody TurnoCreateRequest turnoDTO) {
        return ResponseEntity.ok(turnoService.crearTurno(turnoDTO));
    }

    // ✅ Listar todos
    @GetMapping
    public ResponseEntity<List<Turno>> listarTurnos() {
        return ResponseEntity.ok(turnoService.listarTurnos());
    }

    // ✅ Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<Turno> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.obtenerPorId(id));
    }

    // ✅ Actualizar observaciones
    @PutMapping("/{id}")
    public ResponseEntity<Turno> actualizarTurno(@PathVariable Long id, @RequestBody Turno datos) {
        return ResponseEntity.ok(turnoService.actualizarTurno(id, datos));
    }

    // ✅ Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTurno(@PathVariable Long id) {
        turnoService.eliminarTurno(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Reprogramar turno
    @PutMapping("/{id}/reprogramar")
    public ResponseEntity<Turno> reprogramarTurno(
            @PathVariable Long id,
            @RequestParam("fecha")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fecha) {

        return ResponseEntity.ok(turnoService.reprogramarTurno(id, fecha));
    }

    // ✅ Cancelar turno
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Turno> cancelarTurno(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.cancelarTurno(id));
    }

    // ✅ Obtener horarios disponibles para un abogado en una fecha
    @GetMapping("/abogado/{idAbogado}/disponibles")
    public ResponseEntity<List<LocalTime>> obtenerHorariosDisponibles(
            @PathVariable Long idAbogado,
            @RequestParam("fecha")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha) {

        return ResponseEntity.ok(turnoService.obtenerHorariosDisponibles(idAbogado, fecha));
    }

    // ✅ Verificar si un horario específico está disponible
    @GetMapping("/abogado/{idAbogado}/disponible")
    public ResponseEntity<Boolean> verificarDisponibilidad(
            @PathVariable Long idAbogado,
            @RequestParam("fechaHora")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaHora) {

        return ResponseEntity.ok(turnoService.isHorarioDisponible(idAbogado, fechaHora));
    }
}