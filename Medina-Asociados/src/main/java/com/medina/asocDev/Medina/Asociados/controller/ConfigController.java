package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.utils.TurnoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private TurnoProperties turnoProperties;


    public ConfigController(TurnoProperties turnoProperties) {
        this.turnoProperties = turnoProperties;
    }

    // Obtener el precio actual
    @GetMapping("/precio-turno")
    public ResponseEntity<Float> getPrecioTurno() {
        return ResponseEntity.ok(turnoProperties.getPrecioBase());
    }

    // Modificar el precio (ej: desde un panel de administración)
    @PutMapping("/precio-turno")
    public ResponseEntity<String> updatePrecioTurno(@RequestBody Float nuevoPrecio) {
        turnoProperties.setPrecioBase(nuevoPrecio);
        return ResponseEntity.ok("Precio de turno actualizado a: " + nuevoPrecio);
    }
}