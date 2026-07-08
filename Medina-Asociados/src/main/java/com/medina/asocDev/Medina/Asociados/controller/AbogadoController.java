package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.AbogadoDTO;
import com.medina.asocDev.Medina.Asociados.dto.AbogadoEspecialidadesDTO;
import com.medina.asocDev.Medina.Asociados.dto.AbogadoMatriculaDTO;
import com.medina.asocDev.Medina.Asociados.service.AbogadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/abogados")
public class AbogadoController {

	@Autowired
	private AbogadoService abogadoService;

	// Crear un abogado para un usuario existente
	@PostMapping("/{idUsuario}")
	@PreAuthorize("@securityService.isAdmin(authentication)")
	public ResponseEntity<AbogadoDTO> createAbogado(@PathVariable Long idUsuario,
													@RequestBody AbogadoDTO abogadoDTO) {
		AbogadoDTO creado = abogadoService.createAbogado(idUsuario, abogadoDTO);
		if (creado == null) return ResponseEntity.badRequest().build();
		return ResponseEntity.ok(creado);
	}

	// Listar todos los abogados (paginado, max 10 por pagina)
	@GetMapping
	@PreAuthorize("@securityService.hasAnyRole(authentication, 'ABOGADO', 'ADMIN', 'CLIENTE')")
	public ResponseEntity<Page<AbogadoDTO>> getAll(@PageableDefault(size = 10) Pageable pageable) {
		return ResponseEntity.ok(abogadoService.getAll(pageable));
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
	@PreAuthorize("@securityService.isAdmin(authentication)")
	public ResponseEntity<AbogadoDTO> updateMatricula(
			@PathVariable Long id,
			@RequestBody AbogadoMatriculaDTO dto) {
		AbogadoDTO actualizado = abogadoService.updateMatricula(id, dto.getMatricula());
		if (actualizado == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(actualizado);
	}

	@PutMapping("/{idAbogado}/especialidades")
	@PreAuthorize("@securityService.isAdmin(authentication)")
	public ResponseEntity<AbogadoDTO> updateEspecialidades(
			@PathVariable Long idAbogado,
			@RequestBody AbogadoEspecialidadesDTO dto) {
		AbogadoDTO actualizado = abogadoService.updateEspecialidades(idAbogado, dto.getEspecialidadesAbogado());
		if (actualizado == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(actualizado);
	}

	// Eliminar un abogado
	@DeleteMapping("/{id}")
	@PreAuthorize("@securityService.isAdmin(authentication)")
	public ResponseEntity<Void> deleteAbogado(@PathVariable Long id) {
		boolean borrado = abogadoService.deleteAbogado(id);
		if (borrado) return ResponseEntity.noContent().build();
		else return ResponseEntity.notFound().build();
	}

	// Obtener abogados por id de especialidad (paginado, max 10 por pagina)
	@GetMapping("/especialidad/{idEspecialidad}")
	@PreAuthorize("@securityService.hasAnyRole(authentication, 'ABOGADO', 'ADMIN', 'CLIENTE')")
	public ResponseEntity<Page<AbogadoDTO>> getAbogadosByEspecialidad(
			@PathVariable Long idEspecialidad,
			@PageableDefault(size = 10) Pageable pageable) {
		return ResponseEntity.ok(abogadoService.getAbogadosByEspecialidad(idEspecialidad, pageable));
	}

	@GetMapping("/{idAbogado}/horarios-disponibles")
	@PreAuthorize("@securityService.hasAnyRole(authentication, 'ABOGADO', 'ADMIN', 'CLIENTE')")
	public ResponseEntity<List<LocalTime>> obtenerHorariosDisponibles(
			@PathVariable Long idAbogado,
			@RequestParam("fecha")
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
			LocalDate fecha) {

		return ResponseEntity.ok(abogadoService.obtenerHorariosDisponibles(idAbogado, fecha));
	}

	@GetMapping("/{idAbogado}/disponibilidad")
	@PreAuthorize("@securityService.hasAnyRole(authentication, 'ABOGADO', 'ADMIN', 'CLIENTE')")
	public ResponseEntity<Boolean> verificarDisponibilidad(
			@PathVariable Long idAbogado,
			@RequestParam("fechaHora")
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
			LocalDateTime fechaHora) {

		return ResponseEntity.ok(abogadoService.verificarDisponibilidad(idAbogado, fechaHora));
	}

}
