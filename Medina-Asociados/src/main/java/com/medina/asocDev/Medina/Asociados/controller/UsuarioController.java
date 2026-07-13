package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.MensajeResponse;
import com.medina.asocDev.Medina.Asociados.dto.RegisterDTO;
import com.medina.asocDev.Medina.Asociados.dto.Response;
import com.medina.asocDev.Medina.Asociados.dto.UsuarioDTO;
import com.medina.asocDev.Medina.Asociados.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@PostMapping
	public ResponseEntity<MensajeResponse> createUsuario(@RequestBody RegisterDTO registerDTO) {
		MensajeResponse response = usuarioService.createUsuario(registerDTO);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	@PreAuthorize("@securityService.hasAnyRole(authentication, 'ABOGADO', 'ADMIN')")
	public ResponseEntity<Page<UsuarioDTO>> getAllUsers(@PageableDefault(size = 10) Pageable pageable) {
		return ResponseEntity.ok(usuarioService.getAllUsers(pageable));
	}

	@GetMapping("/{id}")
	@PreAuthorize("@securityService.canAccessClienteDetalle(authentication, #id)")
	public ResponseEntity<UsuarioDTO> getUserById(@PathVariable Long id) {
		UsuarioDTO usuario = usuarioService.getUserByIdInternal(id);
		if (usuario == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(usuario);
	}

	@PutMapping("/{id}")
	@PreAuthorize("@securityService.isAdmin(authentication)")
	public ResponseEntity<UsuarioDTO> updateUser(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDTO) {
		UsuarioDTO actualizado = usuarioService.updateUser(id, usuarioDTO);
		if (actualizado == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(actualizado);
	}

	@GetMapping("/buscar-por-dni")
	@PreAuthorize("@securityService.hasAnyRole(authentication, 'ABOGADO', 'ADMIN')")
	public ResponseEntity<Page<UsuarioDTO>> buscarPorDni(
			@RequestParam String dni,
			@PageableDefault(size = 10) Pageable pageable) {
		return ResponseEntity.ok(usuarioService.buscarPorDni(dni, pageable));
	}

	@GetMapping("/{id}/detalle")
	@PreAuthorize("@securityService.canAccessClienteDetalle(authentication, #id)")
	public ResponseEntity<Response> getClienteDetalle(@PathVariable Long id) {
		Response response = usuarioService.getClienteDetalle(id);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("@securityService.isAdmin(authentication)")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		boolean borrado = usuarioService.deleteUserInternal(id);
		if (borrado) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}