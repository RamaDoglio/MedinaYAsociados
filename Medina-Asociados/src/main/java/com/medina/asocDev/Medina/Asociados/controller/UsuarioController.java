package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.entity.Usuario;
import com.medina.asocDev.Medina.Asociados.entity.Direccion;
import com.medina.asocDev.Medina.Asociados.entity.Localidad;
import com.medina.asocDev.Medina.Asociados.dto.UsuarioDTO;
import com.medina.asocDev.Medina.Asociados.dto.DireccionDTO;
import com.medina.asocDev.Medina.Asociados.dto.LocalidadDTO;
import com.medina.asocDev.Medina.Asociados.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	// Obtener todos los clientes
	@GetMapping
	public List<Usuario> getAllUsuarios() {
		return usuarioService.findAll();
	}

	// Obtener cliente por ID
	@GetMapping("/{id}")
	public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
		Optional<Usuario> usuario = usuarioService.findById(id);
		return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	// Crear cliente
	@PostMapping
	public Usuario createUsuario(@RequestBody UsuarioDTO usuarioDTO) {
		Usuario usuario = new Usuario();
		usuario.setNombre(usuarioDTO.getNombre());
		usuario.setApellido(usuarioDTO.getApellido());
		usuario.setDni(usuarioDTO.getDni() != null ? usuarioDTO.getDni().toString() : null);
		usuario.setTelefono(usuarioDTO.getTelefono() != null ? usuarioDTO.getTelefono().toString() : null);
		usuario.setEmail(usuarioDTO.getEmail());
		usuario.setPassword(usuarioDTO.getPassword());

		// Mapear dirección si viene en el DTO
		if (usuarioDTO.getDireccion() != null) {
			DireccionDTO dirDTO = usuarioDTO.getDireccion();
			Direccion direccion = new Direccion();
			direccion.setCalle(dirDTO.getCalle());
			direccion.setNumeroCalle(dirDTO.getNumeroCalle());
			direccion.setDpto(dirDTO.getDpto());
			direccion.setPiso(dirDTO.getPiso());
			direccion.setProvincia("CORDOBA");
			// Mapear localidad si viene en el DTO
			if (dirDTO.getLocalidad() != null) {
				LocalidadDTO locDTO = dirDTO.getLocalidad();
				Localidad localidad = new Localidad();
				localidad.setNombreLocalidad(locDTO.getNombreLocalidad());
				localidad.setCodigoPostal(locDTO.getCodigoPostal());
				direccion.setLocalidad(localidad);
			}
			// Aquí deberías guardar la dirección y localidad si usas repositorios separados
			// usuario.setDireccion(direccion); // Descomentar si Usuario tiene relación con Direccion
		}

		return usuarioService.save(usuario);
	}

	// Actualizar cliente
	@PutMapping("/{id}")
	public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDTO) {
		Usuario usuarioDetails = new Usuario();
		usuarioDetails.setNombre(usuarioDTO.getNombre());
		usuarioDetails.setApellido(usuarioDTO.getApellido());
		usuarioDetails.setDni(usuarioDTO.getDni() != null ? usuarioDTO.getDni().toString() : null);
		usuarioDetails.setTelefono(usuarioDTO.getTelefono() != null ? usuarioDTO.getTelefono().toString() : null);
		usuarioDetails.setEmail(usuarioDTO.getEmail());
		usuarioDetails.setPassword(usuarioDTO.getPassword());
		// Si necesitas actualizar dirección, deberías mapearla aquí
		try {
			Usuario actualizado = usuarioService.updateUsuario(id, usuarioDetails);
			return ResponseEntity.ok(actualizado);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	// Eliminar cliente
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
		if (usuarioService.findById(id).isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		usuarioService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
