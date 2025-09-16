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
    private DetalleCobroService detalleCobroServices;

    @PostMapping
    public ResponseEntity<DetalleCobroDTO> createDetalleCobro(@RequestBody DetalleCobroDTO dto) {
        DetalleCobroDTO creado = detalleCobroServices.createDetalleCobro(dto);
        if (creado == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(creado);
    }

    @GetMapping("/cobro/{cobroId}")
    public ResponseEntity<List<DetalleCobroDTO>> getDetallesPorCobro(@PathVariable Long cobroId) {
        return ResponseEntity.ok(detalleCobroServices.getDetallesPorCobro(cobroId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleCobroDTO> getDetalleCobroById(@PathVariable Long id) {
        DetalleCobroDTO detalle = detalleCobroServices.getDetalleCobroById(id);
        if (detalle == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(detalle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleCobroDTO> updateDetalleCobro(@PathVariable Long id, @RequestBody DetalleCobroDTO dto) {
        DetalleCobroDTO actualizado = detalleCobroServices.updateDetalleCobro(id, dto);
        if (actualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetalleCobro(@PathVariable Long id) {
        boolean borrado = detalleCobroServices.deleteDetalleCobro(id);
        if (borrado) return ResponseEntity.noContent().build();
        else return ResponseEntity.notFound().build();
    }
}
