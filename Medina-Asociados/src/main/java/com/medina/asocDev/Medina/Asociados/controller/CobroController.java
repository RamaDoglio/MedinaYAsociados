package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.CobroConDetallesDTO;
import com.medina.asocDev.Medina.Asociados.dto.CobroDTO;
import com.medina.asocDev.Medina.Asociados.service.CobroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cobros")
public class CobroController {

	@Autowired
	private CobroService cobroService;

	@PostMapping
	@PreAuthorize("@securityService.hasAnyRole(authentication, 'ADMIN', 'ABOGADO')")
	public ResponseEntity<CobroDTO> createCobro(@RequestBody CobroDTO cobroDTO) {
		CobroDTO creado = cobroService.createCobro(cobroDTO);
		return creado == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(creado);
	}

	@GetMapping("/turno/{turnoId}")
	@PreAuthorize("@securityService.hasAnyRole(authentication, 'ABOGADO', 'ADMIN', 'CLIENTE')")
	public ResponseEntity<CobroDTO> getCobroPorTurno(@PathVariable Long turnoId) {
		return ResponseEntity.ok(cobroService.getCobroPorTurno(turnoId));
	}

	@GetMapping("/{id}")
	@PreAuthorize("@securityService.isAdmin(authentication)")
	public ResponseEntity<CobroDTO> getCobroPorId(@PathVariable Long id) {
		CobroDTO cobro = cobroService.getCobroPorId(id);
		return cobro == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(cobro);
	}

	@PutMapping("/{id}")
	@PreAuthorize("@securityService.isAdmin(authentication)")
	public ResponseEntity<CobroDTO> updateCobro(@PathVariable Long id, @RequestBody CobroDTO cobroDTO) {
		CobroDTO actualizado = cobroService.updateCobro(id, cobroDTO);
		return actualizado == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(actualizado);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("@securityService.isAdmin(authentication)")
	public ResponseEntity<Void> deleteCobro(@PathVariable Long id) {
		return cobroService.deleteCobro(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}

	@GetMapping("/{id}/detalles")
	@PreAuthorize("@securityService.hasAnyRole(authentication, 'ADMIN', 'ABOGADO')")
	public ResponseEntity<CobroConDetallesDTO> getCobroConDetalles(@PathVariable Long id) {
		CobroConDetallesDTO dto = cobroService.getCobroConDetalles(id);
		return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
	}
}

