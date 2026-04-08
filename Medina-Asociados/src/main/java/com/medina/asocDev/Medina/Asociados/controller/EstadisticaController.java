package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.EstadisticaDTO;
import com.medina.asocDev.Medina.Asociados.service.interfac.IEstadisticaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/estadisticas")
public class EstadisticaController {

    private final IEstadisticaService estadisticaService;

    public EstadisticaController(IEstadisticaService estadisticaService) {
        this.estadisticaService = estadisticaService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, List<EstadisticaDTO>>> getDashboard() {
        return ResponseEntity.ok(estadisticaService.obtenerResumenDashboard());
    }
}
