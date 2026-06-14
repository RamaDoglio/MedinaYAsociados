package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.MensajeResponse;
import com.medina.asocDev.Medina.Asociados.dto.RegisterDTO;
import com.medina.asocDev.Medina.Asociados.dto.Response;
import com.medina.asocDev.Medina.Asociados.dto.UsuarioDTO;
import com.medina.asocDev.Medina.Asociados.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
	@PreAuthorize("@securityService.canAccessAbogadoTurnos(authentication, #idAbogado)")
	public ResponseEntity<Response> getAllUsers() {  // ← Cambia a Response
		Response response = usuarioService.getAllUsers();
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}

	@GetMapping("/{id}")
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