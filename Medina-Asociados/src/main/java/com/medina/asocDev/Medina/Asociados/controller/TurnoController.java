package com.medina.asocDev.Medina.Asociados.controller;


import com.medina.asocDev.Medina.Asociados.dto.PagarTurnoResponse;
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
import java.util.Map;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    @Autowired
    private TurnoService turnoService;

    // ✅ Crear turno (reserva)
    @PostMapping
    public ResponseEntity<TurnoDTO> crearTurno(@RequestBody TurnoCreateRequest turnoDTO) {
        return ResponseEntity.ok(Utils.mapTurnoEntityToDTO(turnoService.crearTurno(turnoDTO)));
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<PagarTurnoResponse> pagarTurno(@PathVariable Long id) {
        Map<String, Object> resp = turnoService.pagarTurno(id);
        return ResponseEntity.ok(new PagarTurnoResponse(
                (TurnoDTO) resp.get("turno"),
                (String) resp.get("init_point")
        ));
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
    public ResponseEntity<TurnoDTO> actualizarTurno(@PathVariable Long id, @RequestBody Turno datos) {
        return ResponseEntity.ok(Utils.mapTurnoEntityToDTO(turnoService.actualizarTurno(id, datos)));
    }

    // ✅ Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTurno(@PathVariable Long id) {
        turnoService.eliminarTurno(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Reprogramar turno
    @PutMapping("/{id}/reprogramar")
    public ResponseEntity<TurnoDTO> reprogramarTurno(
            @PathVariable Long id,
            @RequestParam("fecha")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fecha) {

        return ResponseEntity.ok(turnoService.reprogramarTurno(id, fecha));
    }

    // ✅ Cancelar turno
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<TurnoDTO> cancelarTurno(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.cancelarTurno(id));
    }

    // 🔥 Marcar no asistió
    @PostMapping("/{id}/noAsistio")
    public ResponseEntity<TurnoDTO> marcarNoAsistio(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.marcarNoAsistio(id));
    }

    @PostMapping("/{id}/enCurso")
    public ResponseEntity<TurnoDTO> marcarEnCurso(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.marcarEnCurso(id));
    }

    // 🔥 Finalizar turno
    @PostMapping("/{id}/finalizar")
    public ResponseEntity<TurnoDTO> finalizarTurno(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.finalizarTurno(id));
    }
}