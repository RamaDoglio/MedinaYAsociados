package com.medina.asocDev.Medina.Asociados.controller;


import com.medina.asocDev.Medina.Asociados.dto.TurnoCreateRequest;
import com.medina.asocDev.Medina.Asociados.dto.TurnoDTO;
import com.medina.asocDev.Medina.Asociados.entity.Turno;
import com.medina.asocDev.Medina.Asociados.service.TurnoService;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @PostMapping("/{id}/pagar")
    public ResponseEntity<TurnoDTO> pagarTurno(@PathVariable Long id) {
        Turno turno = turnoService.pagarTurno(id);
        return ResponseEntity.ok(Utils.mapTurnoEntityToDTO(turno));
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
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<TurnoDTO> cancelarTurno(@PathVariable Long id) {
        Turno turno = turnoService.cancelarTurno(id);
        return ResponseEntity.ok(Utils.mapTurnoEntityToDTO(turno));
    }
}