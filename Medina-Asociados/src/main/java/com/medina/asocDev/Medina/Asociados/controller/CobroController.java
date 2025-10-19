package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.CobroDTO;
import com.medina.asocDev.Medina.Asociados.service.CobroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cobros")
public class CobroController {

	@Autowired
	private CobroService cobroService;

	@PostMapping
	public ResponseEntity<CobroDTO> createCobro(@RequestBody CobroDTO cobroDTO) {
		CobroDTO creado = cobroService.createCobro(cobroDTO);
		return creado == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(creado);
	}

	@GetMapping("/turno/{turnoId}")
	public ResponseEntity<CobroDTO> getCobroPorTurno(@PathVariable Long turnoId) {
		return ResponseEntity.ok(cobroService.getCobroPorTurno(turnoId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<CobroDTO> getCobroPorId(@PathVariable Long id) {
		CobroDTO cobro = cobroService.getCobroPorId(id);
		return cobro == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(cobro);
	}

	@PutMapping("/{id}")
	public ResponseEntity<CobroDTO> updateCobro(@PathVariable Long id, @RequestBody CobroDTO cobroDTO) {
		CobroDTO actualizado = cobroService.updateCobro(id, cobroDTO);
		return actualizado == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(actualizado);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCobro(@PathVariable Long id) {
		return cobroService.deleteCobro(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}
}

