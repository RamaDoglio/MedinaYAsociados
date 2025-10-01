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
	private AbogadoService abogadoService;

	// Crear un abogado para un usuario existente
	@PostMapping("/{idUsuario}")
	public ResponseEntity<AbogadoDTO> createAbogado(@PathVariable Long idUsuario,
													@RequestBody AbogadoDTO abogadoDTO) {
		AbogadoDTO creado = abogadoService.createAbogado(idUsuario, abogadoDTO);
		if (creado == null) return ResponseEntity.badRequest().build();
		return ResponseEntity.ok(creado);
	}

	// Listar todos los abogados
	@GetMapping
	public ResponseEntity<List<AbogadoDTO>> getAll() {
		return ResponseEntity.ok(abogadoService.getAll());
	}

	// Obtener abogado por ID
	@GetMapping("/{id}")
	public ResponseEntity<AbogadoDTO> getAbogadoById(@PathVariable Long id) {
		AbogadoDTO abogado = abogadoService.getAbogadoById(id);
		if (abogado == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(abogado);
	}

	// Actualizar un abogado
	@PutMapping("/{id}")
	public ResponseEntity<AbogadoDTO> updateAbogado(@PathVariable Long id,
													@RequestBody AbogadoDTO abogadoDTO) {
		AbogadoDTO actualizado = abogadoService.updateAbogado(id, abogadoDTO);
		if (actualizado == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(actualizado);
	}

	// Eliminar un abogado
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAbogado(@PathVariable Long id) {
		boolean borrado = abogadoService.deleteAbogado(id);
		if (borrado) return ResponseEntity.noContent().build();
		else return ResponseEntity.notFound().build();
	}

	// Obtener abogados por especialidad
	@GetMapping("/especialidad/{nombreEspecialidad}")
	public ResponseEntity<List<AbogadoDTO>> getAbogadosByEspecialidad(@PathVariable String nombreEspecialidad) {
		List<AbogadoDTO> abogados = abogadoService.getAbogadosByEspecialidad(nombreEspecialidad);
		return ResponseEntity.ok(abogados);
	}
}
