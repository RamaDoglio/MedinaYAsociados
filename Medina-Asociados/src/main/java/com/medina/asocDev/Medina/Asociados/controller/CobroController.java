package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.CobroDTO;
import com.medina.asocDev.Medina.Asociados.service.CobroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cobros")
public class CobroController {

	@PostMapping
	public ResponseEntity<CobroDTO> createCobro(@RequestBody CobroDTO cobroDTO) {
		CobroDTO creado = CobroService.createCobro(cobroDTO);
		return creado == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(creado);
	}

	@GetMapping("/turno/{turnoId}")
	public ResponseEntity<CobroDTO> getCobroPorTurno(@PathVariable Long turnoId) {
		return ResponseEntity.ok(CobroService.getCobroPorTurno(turnoId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<CobroDTO> getCobroPorId(@PathVariable Long id) {
		CobroDTO cobro = CobroService.getCobroPorId(id);
		return cobro == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(cobro);
	}

	@PutMapping("/{id}")
	public ResponseEntity<CobroDTO> updateCobro(@PathVariable Long id, @RequestBody CobroDTO cobroDTO) {
		CobroDTO actualizado = CobroService.updateCobro(id, cobroDTO);
		return actualizado == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(actualizado);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCobro(@PathVariable Long id) {
		return CobroService.deleteCobro(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}
}

