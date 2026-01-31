package com.medina.asocDev.Medina.Asociados.controller;


import com.medina.asocDev.Medina.Asociados.dto.*;
import com.medina.asocDev.Medina.Asociados.entity.Turno;
import com.medina.asocDev.Medina.Asociados.service.TurnoService;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<TurnoDTO> crearTurno(@RequestBody TurnoCreateRequest turnoDTO) {
        return ResponseEntity.ok(Utils.mapTurnoEntityToDTO(turnoService.crearTurno(turnoDTO)));
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<String> pagarTurno(@PathVariable Long id) {
        String initPoint = turnoService.pagarTurno(id);
        return ResponseEntity.ok(initPoint);
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

    // Listado de turnos de un cliente
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<TurnoListadoDTO>> listarTurnosPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(turnoService.listarTurnosPorCliente(idCliente));
    }

    // Listado de turnos de un abogado
    @GetMapping("/abogado/{idAbogado}")
    public ResponseEntity<List<TurnoListadoDTO>> listarTurnosPorAbogado(@PathVariable Long idAbogado) {
        return ResponseEntity.ok(turnoService.listarTurnosPorAbogado(idAbogado));
    }


    // Detalle para cliente
    @GetMapping("/{id}/detalle-cliente")
    public ResponseEntity<TurnoDetalleDTO> obtenerDetalleTurnoCliente(@PathVariable Long id) {
        Turno turno = turnoService.obtenerPorId(id);
        return ResponseEntity.ok(Utils.mapTurnoToDetalleDTOParaCliente(turno));
    }

    // Detalle para abogado
    @GetMapping("/{id}/detalle-abogado")
    public ResponseEntity<TurnoDetalleDTO> obtenerDetalleTurnoAbogado(@PathVariable Long id) {
        Turno turno = turnoService.obtenerPorId(id);
        return ResponseEntity.ok(Utils.mapTurnoToDetalleDTOParaAbogado(turno));
    }
    @PutMapping("/{id}/observaciones-abogado")
    public ResponseEntity<TurnoDTO> agregarObservacionesAbogado(
            @PathVariable Long id,
            @RequestBody String observaciones) {

        return ResponseEntity.ok(turnoService.agregarObservacionesAbogado(id, observaciones));
    }

    @PostMapping("/offline")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('ABOGADO')")
    public ResponseEntity<TurnoDTO> createTurnoOffline(@RequestBody TurnoOfflineRequest request) {
        try {
            TurnoDTO turno = turnoService.createTurnoOffline(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(turno);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}