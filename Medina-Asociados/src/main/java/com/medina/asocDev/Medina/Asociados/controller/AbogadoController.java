package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.AbogadoDTO;
import com.medina.asocDev.Medina.Asociados.service.AbogadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/abogados")
public class AbogadoController {

	@Autowired
	private AbogadoService abogadoServices;

	@PostMapping("/usuario/{idUsuario}")
	public ResponseEntity<AbogadoDTO> createAbogado(@PathVariable Long idUsuario, @RequestBody AbogadoDTO abogadoDTO) {
		AbogadoDTO creado = abogadoServices.createAbogado(idUsuario, abogadoDTO);
		if (creado == null) return ResponseEntity.badRequest().build();
		return ResponseEntity.ok(creado);
	}

	@GetMapping
	public ResponseEntity<List<AbogadoDTO>> getAll() {
		return ResponseEntity.ok(abogadoServices.getAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<AbogadoDTO> getAbogadoById(@PathVariable Long id) {
		AbogadoDTO abogado = abogadoServices.getAbogadoById(id);
		if (abogado == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(abogado);
	}

	@PutMapping("/{id}")
	public ResponseEntity<AbogadoDTO> updateAbogado(@PathVariable Long id, @RequestBody AbogadoDTO abogadoDTO) {
		AbogadoDTO actualizado = abogadoServices.updateAbogado(id, abogadoDTO);
		if (actualizado == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(actualizado);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAbogado(@PathVariable Long id) {
		boolean borrado = abogadoServices.deleteAbogado(id);
		if (borrado) return ResponseEntity.noContent().build();
		else return ResponseEntity.notFound().build();
	}
}
