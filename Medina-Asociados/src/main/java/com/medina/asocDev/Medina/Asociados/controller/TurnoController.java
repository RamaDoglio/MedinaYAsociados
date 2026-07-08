package com.medina.asocDev.Medina.Asociados.controller;


import com.medina.asocDev.Medina.Asociados.dto.*;
import com.medina.asocDev.Medina.Asociados.entity.Turno;
import com.medina.asocDev.Medina.Asociados.service.TurnoService;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    @Autowired
    private TurnoService turnoService;

    // ✅ Crear turno (reserva)
    @PostMapping
    @PreAuthorize("@securityService.isCliente(authentication)")
    public ResponseEntity<TurnoDTO> crearTurno(@RequestBody TurnoCreateRequest turnoDTO) {
        return ResponseEntity.ok(Utils.mapTurnoEntityToDTO(turnoService.crearTurno(turnoDTO)));
    }

    @PostMapping("/{id}/pagar")
    @PreAuthorize("@securityService.canAccessTurno(authentication, #id)")
    public ResponseEntity<String> pagarTurno(@PathVariable Long id) {
        String initPoint = turnoService.pagarTurno(id);
        return ResponseEntity.ok(initPoint);
    }

    // ✅ Listar todos (paginado, max 10 por pagina)
    @GetMapping
    @PreAuthorize("@securityService.isAdmin(authentication)")
    public ResponseEntity<Page<Turno>> listarTurnos(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(turnoService.listarTurnos(pageable));
    }

    // ✅ Obtener por ID
    @GetMapping("/{id}")
    @PreAuthorize("@securityService.isAdmin(authentication)")
    public ResponseEntity<Turno> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.obtenerPorId(id));
    }

    // ✅ Actualizar observaciones
    @PutMapping("/{id}")
    @PreAuthorize("@securityService.canAccessAbogadoTurnos(authentication, #id)")
    public ResponseEntity<TurnoDTO> actualizarTurno(@PathVariable Long id, @RequestBody Turno datos) {
        return ResponseEntity.ok(Utils.mapTurnoEntityToDTO(turnoService.actualizarTurno(id, datos)));
    }

    // ✅ Eliminar
    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.isAdmin(authentication)")
    public ResponseEntity<Void> eliminarTurno(@PathVariable Long id) {
        turnoService.eliminarTurno(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Reprogramar turno
    @PutMapping("/{id}/reprogramar")
    @PreAuthorize("@securityService.canAccessTurno(authentication, #id)")
    public ResponseEntity<TurnoDTO> reprogramarTurno(
            @PathVariable Long id,
            @RequestParam("fecha")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fecha) {

        return ResponseEntity.ok(turnoService.reprogramarTurno(id, fecha));
    }

    // ✅ Cancelar turno
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("@securityService.canAccessTurno(authentication, #id)")
    public ResponseEntity<TurnoDTO> cancelarTurno(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.cancelarTurno(id));
    }

    // 🔥 Marcar no asistió
    @PostMapping("/{id}/noAsistio")
    @PreAuthorize("@securityService.canAccessTurno(authentication, #id)")
    public ResponseEntity<TurnoDTO> marcarNoAsistio(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.marcarNoAsistio(id));
    }

    @PostMapping("/{id}/enCurso")
    @PreAuthorize("@securityService.canAccessTurno(authentication, #id)")
    public ResponseEntity<TurnoDTO> marcarEnCurso(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.marcarEnCurso(id));
    }

    // 🔥 Finalizar turno
    @PostMapping("/{id}/finalizar")
    @PreAuthorize("@securityService.canAccessTurno(authentication, #id)")
    public ResponseEntity<TurnoDTO> finalizarTurno(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.finalizarTurno(id));
    }

    // Listado de turnos de un cliente (paginado, max 10 por pagina)
    @GetMapping("/cliente/{idCliente}")
    @PreAuthorize("@securityService.canAccessClienteTurnos(authentication, #idCliente)")
    public ResponseEntity<Page<TurnoListadoDTO>> listarTurnosPorCliente(
            @PathVariable Long idCliente,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(turnoService.listarTurnosPorCliente(idCliente, pageable));
    }

    // Listado de turnos de un abogado (paginado, max 10 por pagina)
    @GetMapping("/abogado/{idAbogado}")
    @PreAuthorize("@securityService.canAccessAbogadoTurnos(authentication, #idAbogado)")
    public ResponseEntity<Page<TurnoListadoDTO>> listarTurnosPorAbogado(
            @PathVariable Long idAbogado,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(turnoService.listarTurnosPorAbogado(idAbogado, pageable));
    }


    // Detalle para cliente
    @GetMapping("/{id}/detalle-cliente")
    @PreAuthorize("@securityService.canAccessTurno(authentication, #id)")
    public ResponseEntity<TurnoDetalleDTO> obtenerDetalleTurnoCliente(@PathVariable Long id) {
        Turno turno = turnoService.obtenerPorId(id);
        return ResponseEntity.ok(Utils.mapTurnoToDetalleDTOParaCliente(turno));
    }

    // Detalle para abogado
    @GetMapping("/{id}/detalle-abogado")
    @PreAuthorize("@securityService.canAccessTurno(authentication, #id)")
    public ResponseEntity<TurnoDetalleDTO> obtenerDetalleTurnoAbogado(@PathVariable Long id) {
        Turno turno = turnoService.obtenerPorId(id);
        return ResponseEntity.ok(Utils.mapTurnoToDetalleDTOParaAbogado(turno));
    }
    @PutMapping("/{id}/observaciones-abogado")
    @PreAuthorize("@securityService.canAccessTurno(authentication, #id)")
    public ResponseEntity<TurnoDTO> agregarObservacionesAbogado(
            @PathVariable Long id,
            @RequestBody String observaciones) {

        return ResponseEntity.ok(turnoService.agregarObservacionesAbogado(id, observaciones));
    }

    @PostMapping("/offline")
    @PreAuthorize("@securityService.isAbogado(authentication)")
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
    @PostMapping("/{id}/marcar-pagado")
    @PreAuthorize("@securityService.canAccessClienteTurnos(authentication, #id)")
    public ResponseEntity<TurnoDTO> marcarPagado(@PathVariable("id") Long id) {
        TurnoDTO turnoDTO = turnoService.marcarPagado(id);
        return ResponseEntity.ok(turnoDTO);
    }

    @GetMapping("/{id}/historial")
    @PreAuthorize("@securityService.canAccessTurno(authentication, #id)")
    public ResponseEntity<TurnoConHistorialDTO> obtenerTurnoConHistorial(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.getTurnoConHistorial(id));
    }

}