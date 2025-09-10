package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.UsuarioDTO;
import com.medina.asocDev.Medina.Asociados.service.UsuarioServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	@Autowired
	private UsuarioServices usuarioService;

	@PostMapping
	public ResponseEntity<UsuarioDTO> crearUsuario(@RequestBody UsuarioDTO usuarioDTO) {
		UsuarioDTO usuarioCreado = usuarioService.crearUsuarioCliente(usuarioDTO);
		return ResponseEntity.ok(usuarioCreado);
	}

	@GetMapping
	public ResponseEntity<List<UsuarioDTO>> obtenerTodos() {
		return ResponseEntity.ok(usuarioService.obtenerTodos());
	}

	@GetMapping("/{id}")
	public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable Long id) {
		UsuarioDTO usuario = usuarioService.obtenerPorId(id);
		if (usuario == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(usuario);
	}

	@PutMapping("/{id}")
	public ResponseEntity<UsuarioDTO> modificarUsuario(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDTO) {
		UsuarioDTO actualizado = usuarioService.modificarUsuario(id, usuarioDTO);
		if (actualizado == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(actualizado);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> borrarUsuario(@PathVariable Long id) {
		boolean borrado = usuarioService.borrarUsuario(id);
		if (borrado) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
