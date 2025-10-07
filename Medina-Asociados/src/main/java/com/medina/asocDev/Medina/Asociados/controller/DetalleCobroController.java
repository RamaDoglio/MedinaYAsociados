package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.DetalleCobroDTO;
import com.medina.asocDev.Medina.Asociados.service.DetalleCobroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/detalle-cobros")
public class DetalleCobroController {

    @Autowired
    private DetalleCobroService detalleCobroService;

    @PostMapping
    public ResponseEntity<DetalleCobroDTO> createDetalleCobro(@RequestBody DetalleCobroDTO dto) {
        DetalleCobroDTO creado = detalleCobroService.createDetalleCobro(dto);
        return creado == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(creado);
    }

    @GetMapping("/cobro/{cobroId}")
    public ResponseEntity<List<DetalleCobroDTO>> getDetallesPorCobro(@PathVariable Long cobroId) {
        return ResponseEntity.ok(detalleCobroService.getDetallesPorCobro(cobroId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleCobroDTO> getDetalleCobroById(@PathVariable Long id) {
        DetalleCobroDTO detalle = detalleCobroService.getDetalleCobroById(id);
        return detalle == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(detalle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleCobroDTO> updateDetalleCobro(@PathVariable Long id, @RequestBody DetalleCobroDTO dto) {
        DetalleCobroDTO actualizado = detalleCobroService.updateDetalleCobro(id, dto);
        return actualizado == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetalleCobro(@PathVariable Long id) {
        return detalleCobroService.deleteDetalleCobro(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

