package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.service.ParametroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Autowired
    private ParametroService parametroService;

    public ConfigController(ParametroService parametroService) {
        this.parametroService = parametroService;
    }

    @GetMapping("/precio-turno")
    @PreAuthorize("@securityService.hasAnyRole(authentication, 'ADMIN', 'ABOGADO', 'CLIENTE')")
    public ResponseEntity<Double> getPrecioTurno() {
        Double precio = Double.valueOf(parametroService.getValor("PRECIO_TURNO"));
        return ResponseEntity.ok(precio);
    }

    @PutMapping("/precio-turno")
    @PreAuthorize("@securityService.isAdmin(authentication)")
    public ResponseEntity<String> updatePrecioTurno(@RequestBody Double nuevoPrecio) {
        parametroService.setValor("PRECIO_TURNO", nuevoPrecio.toString());
        return ResponseEntity.ok("Precio de turno actualizado a: " + nuevoPrecio);
    }
}
