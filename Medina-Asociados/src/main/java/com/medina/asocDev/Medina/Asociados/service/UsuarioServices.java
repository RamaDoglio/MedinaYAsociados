package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.UsuarioDTO;
import com.medina.asocDev.Medina.Asociados.entity.Usuario;
import com.medina.asocDev.Medina.Asociados.repo.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioServices {

	@Autowired
	private UsuarioRepository usuarioRepository;

	public UsuarioDTO crearUsuarioCliente(UsuarioDTO usuarioDTO) {
		Usuario usuario = new Usuario();
		usuario.setNombre(usuarioDTO.getNombre());
		usuario.setApellido(usuarioDTO.getApellido());
		usuario.setDni(usuarioDTO.getDni() != null ? usuarioDTO.getDni().toString() : null);
		usuario.setTelefono(usuarioDTO.getTelefono() != null ? usuarioDTO.getTelefono().toString() : null);
		usuario.setEmail(usuarioDTO.getEmail());
		usuario.setPassword(usuarioDTO.getPassword());
		Usuario usuarioGuardado = usuarioRepository.save(usuario);
		UsuarioDTO dto = new UsuarioDTO();
		dto.setIdUsuario(usuarioGuardado.getIdUsuario());
		dto.setNombre(usuarioGuardado.getNombre());
		dto.setApellido(usuarioGuardado.getApellido());
		dto.setDni(usuarioGuardado.getDni() != null ? Integer.valueOf(usuarioGuardado.getDni()) : null);
		dto.setTelefono(usuarioGuardado.getTelefono() != null ? Integer.valueOf(usuarioGuardado.getTelefono()) : null);
		dto.setEmail(usuarioGuardado.getEmail());
		return dto;
	}

	public List<UsuarioDTO> obtenerTodos() {
		List<Usuario> usuarios = usuarioRepository.findAll();
		List<UsuarioDTO> dtos = new ArrayList<>();
		for (Usuario usuario : usuarios) {
			UsuarioDTO dto = new UsuarioDTO();
			dto.setIdUsuario(usuario.getIdUsuario());
			dto.setNombre(usuario.getNombre());
			dto.setApellido(usuario.getApellido());
			dto.setDni(usuario.getDni() != null ? Integer.valueOf(usuario.getDni()) : null);
			dto.setTelefono(usuario.getTelefono() != null ? Integer.valueOf(usuario.getTelefono()) : null);
			dto.setEmail(usuario.getEmail());
			dtos.add(dto);
		}
		return dtos;
	}

	public UsuarioDTO obtenerPorId(Long id) {
		return usuarioRepository.findById(id).map(usuario -> {
			UsuarioDTO dto = new UsuarioDTO();
			dto.setIdUsuario(usuario.getIdUsuario());
			dto.setNombre(usuario.getNombre());
			dto.setApellido(usuario.getApellido());
			dto.setDni(usuario.getDni() != null ? Integer.valueOf(usuario.getDni()) : null);
			dto.setTelefono(usuario.getTelefono() != null ? Integer.valueOf(usuario.getTelefono()) : null);
			dto.setEmail(usuario.getEmail());
			return dto;
		}).orElse(null);
	}

	public boolean borrarUsuario(Long id) {
		if (usuarioRepository.existsById(id)) {
			usuarioRepository.deleteById(id);
			return true;
		}
		return false;
	}

	public UsuarioDTO modificarUsuario(Long id, UsuarioDTO usuarioDTO) {
		return usuarioRepository.findById(id).map(usuario -> {
			usuario.setNombre(usuarioDTO.getNombre());
			usuario.setApellido(usuarioDTO.getApellido());
			usuario.setDni(usuarioDTO.getDni() != null ? usuarioDTO.getDni().toString() : null);
			usuario.setTelefono(usuarioDTO.getTelefono() != null ? usuarioDTO.getTelefono().toString() : null);
			usuario.setEmail(usuarioDTO.getEmail());
			Usuario actualizado = usuarioRepository.save(usuario);
			UsuarioDTO dto = new UsuarioDTO();
			dto.setIdUsuario(actualizado.getIdUsuario());
			dto.setNombre(actualizado.getNombre());
			dto.setApellido(actualizado.getApellido());
			dto.setDni(actualizado.getDni() != null ? Integer.valueOf(actualizado.getDni()) : null);
			dto.setTelefono(actualizado.getTelefono() != null ? Integer.valueOf(actualizado.getTelefono()) : null);
			dto.setEmail(actualizado.getEmail());
			return dto;
		}).orElse(null);
	}
}
