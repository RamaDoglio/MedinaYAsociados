package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.EspecialidadDTO;
import com.medina.asocDev.Medina.Asociados.service.EspecialidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {

    @Autowired
    private EspecialidadService especialidadService;

    // Endpoint para obtener todas las especialidades (paginado, max 10 por pagina)
    @GetMapping
    public ResponseEntity<Page<EspecialidadDTO>> getAllEspecialidades(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(especialidadService.getAllEspecialidades(pageable));
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
