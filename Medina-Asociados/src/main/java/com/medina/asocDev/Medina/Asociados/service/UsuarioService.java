package com.medina.asocDev.Medina.Asociados.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.medina.asocDev.Medina.Asociados.entity.Direccion;
import com.medina.asocDev.Medina.Asociados.entity.Localidad;
import com.medina.asocDev.Medina.Asociados.entity.Rol;
import com.medina.asocDev.Medina.Asociados.repo.RolRepository;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medina.asocDev.Medina.Asociados.dto.UsuarioDTO;
import com.medina.asocDev.Medina.Asociados.entity.Usuario;
import com.medina.asocDev.Medina.Asociados.repo.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	private RolRepository rolRepository;


	public UsuarioDTO createUsuario(UsuarioDTO usuarioDTO) {
		// Buscamos el rol por id, lanzando excepción si no existe
		Rol rol = rolRepository.findById(usuarioDTO.getRol().getIdRol())
				.orElseThrow(() -> new RuntimeException(
						"Rol no encontrado con id " + usuarioDTO.getRol().getIdRol()));

		// Si viene dirección, la mapeamos; si no, dejamos null
		Direccion direccion = null;
		if (usuarioDTO.getDireccion() != null) {
			direccion = new Direccion();
			direccion.setCalle(usuarioDTO.getDireccion().getCalle());
			direccion.setNumeroCalle(usuarioDTO.getDireccion().getNumeroCalle());

			// Localidad
			if (usuarioDTO.getDireccion().getLocalidad() != null) {
				Localidad localidad = new Localidad();
				localidad.setIdLocalidad(usuarioDTO.getDireccion().getLocalidad().getIdLocalidad());
				localidad.setNombreLocalidad(usuarioDTO.getDireccion().getLocalidad().getNombreLocalidad());
				localidad.setCodigoPostal(usuarioDTO.getDireccion().getLocalidad().getCodigoPostal());
				direccion.setLocalidad(localidad);
			}
		}

		// Creamos la entidad Usuario usando el mapper
		Usuario usuario = Utils.mapUsuarioDTOToEntity(usuarioDTO, rol, direccion);

		// Guardamos en la DB
		Usuario usuarioGuardado = usuarioRepository.save(usuario);

		// Retornamos DTO completo con mapeos incluidos
		return Utils.mapUsuarioEntityToDTOxTurnos(usuarioGuardado);
	}



	public List<UsuarioDTO> getAllUsers() {
		List<Usuario> usuarios = usuarioRepository.findAll();
		return usuarios.stream()
				.map(Utils::mapUserEntityToUserDTO)
				.collect(Collectors.toList());
	}

	public UsuarioDTO getUserById(Long id) {
		return usuarioRepository.findById(id)
				.map(Utils::mapUserEntityToUserDTO)
				.orElse(null);
	}

	public boolean deleteUser(Long id) {
		if (usuarioRepository.existsById(id)) {
			usuarioRepository.deleteById(id);
			return true;
		}
		return false;
	}

	public UsuarioDTO updateUser(Long id, UsuarioDTO usuarioDTO) {
		return usuarioRepository.findById(id).map(usuario -> {
			usuario.setNombre(usuarioDTO.getNombre());
			usuario.setApellido(usuarioDTO.getApellido());
			usuario.setDni(usuarioDTO.getDni() != null ? usuarioDTO.getDni().toString() : null);
			usuario.setTelefono(usuarioDTO.getTelefono() != null ? usuarioDTO.getTelefono() : null);
			usuario.setEmail(usuarioDTO.getEmail());
			Usuario actualizado = usuarioRepository.save(usuario);
			return Utils.mapUserEntityToUserDTO(actualizado);
		}).orElse(null);
	}
}

