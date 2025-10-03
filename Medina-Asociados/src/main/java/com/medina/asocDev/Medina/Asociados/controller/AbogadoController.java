package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.AbogadoDTO;
import com.medina.asocDev.Medina.Asociados.dto.AbogadoEspecialidadesDTO;
import com.medina.asocDev.Medina.Asociados.dto.AbogadoMatriculaDTO;
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
	@PatchMapping("/{id}/matricula")
	public ResponseEntity<AbogadoDTO> updateMatricula(
			@PathVariable Long id,
			@RequestBody AbogadoMatriculaDTO dto) {
		AbogadoDTO actualizado = abogadoService.updateMatricula(id, dto.getMatricula());
		if (actualizado == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(actualizado);
	}

	@PutMapping("/{idAbogado}/especialidades")
	public ResponseEntity<AbogadoDTO> updateEspecialidades(
			@PathVariable Long idAbogado,
			@RequestBody AbogadoEspecialidadesDTO dto) {
		AbogadoDTO actualizado = abogadoService.updateEspecialidades(idAbogado, dto.getEspecialidadesAbogado());
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

	// Obtener abogados por id de especialidad
	@GetMapping("/especialidad/{idEspecialidad}")
	public ResponseEntity<List<AbogadoDTO>> getAbogadosByEspecialidad(@PathVariable Long idEspecialidad) {
		List<AbogadoDTO> abogados = abogadoService.getAbogadosByEspecialidad(idEspecialidad);
		return ResponseEntity.ok(abogados);
	}

}
