package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.EspecialidadDTO;
import com.medina.asocDev.Medina.Asociados.service.EspecialidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {

    @Autowired
    private EspecialidadService especialidadService;

    // Endpoint para obtener todas las especialidades
    @GetMapping
    public ResponseEntity<List<EspecialidadDTO>> getAllEspecialidades() {
        List<EspecialidadDTO> especialidades = especialidadService.getAllEspecialidades();
        return ResponseEntity.ok(especialidades);
    }

    // Endpoint para obtener una especialidad por ID
    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadDTO> getEspecialidadById(@PathVariable Long id) {
        EspecialidadDTO especialidad = especialidadService.getEspecialidadById(id);
        if (especialidad != null) {
            return ResponseEntity.ok(especialidad);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
