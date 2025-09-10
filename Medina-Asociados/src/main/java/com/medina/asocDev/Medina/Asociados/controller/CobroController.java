package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.CobroDTO;
import com.medina.asocDev.Medina.Asociados.service.CobroServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cobros")
public class CobroController {

	@Autowired
	private CobroServices cobroServices;

	@PostMapping
	public ResponseEntity<CobroDTO> createCobro(@RequestBody CobroDTO cobroDTO) {
		CobroDTO creado = cobroServices.createCobro(cobroDTO);
		if (creado == null) return ResponseEntity.badRequest().build();
		return ResponseEntity.ok(creado);
	}

	@GetMapping("/turno/{turnoId}")
	public ResponseEntity<List<CobroDTO>> getCobrosPorTurno(@PathVariable Long turnoId) {
		return ResponseEntity.ok(cobroServices.getCobrosPorTurno(turnoId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<CobroDTO> getCobroPorId(@PathVariable Long id) {
		CobroDTO cobro = cobroServices.getCobroPorId(id);
		if (cobro == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(cobro);
	}

	@PutMapping("/{id}")
	public ResponseEntity<CobroDTO> updateCobro(@PathVariable Long id, @RequestBody CobroDTO cobroDTO) {
		CobroDTO actualizado = cobroServices.updateCobro(id, cobroDTO);
		if (actualizado == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(actualizado);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCobro(@PathVariable Long id) {
		boolean borrado = cobroServices.deleteCobro(id);
		if (borrado) return ResponseEntity.noContent().build();
		else return ResponseEntity.notFound().build();
	}
}
