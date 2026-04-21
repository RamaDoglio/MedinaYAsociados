package com.medina.asocDev.Medina.Asociados.service;


import java.util.List;
import java.util.stream.Collectors;

import com.medina.asocDev.Medina.Asociados.dto.*;
import com.medina.asocDev.Medina.Asociados.entity.Direccion;
import com.medina.asocDev.Medina.Asociados.entity.Localidad;
import com.medina.asocDev.Medina.Asociados.entity.Rol;
import com.medina.asocDev.Medina.Asociados.repo.DireccionRepository;
import com.medina.asocDev.Medina.Asociados.repo.LocalidadRepository;
import com.medina.asocDev.Medina.Asociados.repo.RolRepository;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.medina.asocDev.Medina.Asociados.entity.Usuario;
import com.medina.asocDev.Medina.Asociados.repo.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private DireccionRepository direccionRepository;
	@Autowired
	private RolRepository rolRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JWTUtils jwtUtils;
	@Autowired
	private JWTUtils jwtUtils;
	@Autowired
	private AuthenticationManager authenticationManager;


	public MensajeResponse createUsuario(RegisterDTO registerDTO) {
		// Rol por defecto (ej: cliente)
		Rol rol = rolRepository.findById(registerDTO.getIdRol())
				.orElseThrow(() -> new RuntimeException(
						"Rol no encontrado con id " + registerDTO.getIdRol()));

		// Dirección: si viene en el DTO, la mapeamos con Utils
		Direccion direccion = null;
		if (registerDTO.getDireccion() != null) {
			DireccionDTO direccionDTO = registerDTO.getDireccion();

			// Caso 1: la dirección ya existe (viene con id)
			if (direccionDTO.getIdDireccion() != null) {
				direccion = direccionRepository.findById(direccionDTO.getIdDireccion())
						.orElseThrow(() -> new EntityNotFoundException("Dirección no encontrada"));
			}
			// Caso 2: dirección nueva
			else {
				direccion = Utils.mapDireccionDTOToEntity(direccionDTO);

				// Localidad: solo seteamos el id si viene
				if (direccionDTO.getLocalidad() != null) {
					Localidad localidad = new Localidad();
					localidad.setIdLocalidad(direccionDTO.getLocalidad());
					direccion.setLocalidad(localidad);
				}

				direccion = direccionRepository.save(direccion);
			}
		}

		registerDTO.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

		// Usuario: delegamos al mapper
		Usuario usuario = Utils.mapRegistroDTOToEntity(registerDTO, rol, direccion);

		Usuario usuarioGuardado = usuarioRepository.save(usuario);

		// Retornamos DTO completo
		return new MensajeResponse("Registro completado, redirigiendo al inicio de sesión");
	}

	@Transactional
	public Usuario getOrCreateUsuario(ClienteOfflineRequest clienteRequest) {
		// Buscar por email o teléfono (únicos)
		Usuario usuarioExistente = usuarioRepository.findByEmail(clienteRequest.getEmail())
				.orElse(usuarioRepository.findByTelefono(clienteRequest.getTelefono()).orElse(null));

		if (usuarioExistente != null) {
			return usuarioExistente;  // Reutilizar si existe
		}

		// Crear nuevo usuario
		Usuario nuevoUsuario = new Usuario();
		nuevoUsuario.setNombre(clienteRequest.getNombre());
		nuevoUsuario.setApellido(clienteRequest.getApellido());
		nuevoUsuario.setDni(clienteRequest.getDni());
		nuevoUsuario.setTelefono(clienteRequest.getTelefono());
		nuevoUsuario.setEmail(clienteRequest.getEmail());

		// Manejo de dirección (basado en createUsuario)
		Direccion direccion = null;
		if (clienteRequest.getDireccion() != null) {
			DireccionDTO direccionDTO = clienteRequest.getDireccion();  // Asumiendo que DireccionRequest es compatible o mapea a DireccionDTO

			// Caso 1: la dirección ya existe (viene con id)
			if (direccionDTO.getIdDireccion() != null) {
				direccion = direccionRepository.findById(direccionDTO.getIdDireccion())
						.orElseThrow(() -> new EntityNotFoundException("Dirección no encontrada"));
			}
			// Caso 2: dirección nueva
			else {
				direccion = Utils.mapDireccionDTOToEntity(direccionDTO);

				// Localidad: solo seteamos el id si viene
				if (direccionDTO.getLocalidad() != null) {
					Localidad localidad = new Localidad();
					localidad.setIdLocalidad(direccionDTO.getLocalidad());
					direccion.setLocalidad(localidad);
				}

				direccion = direccionRepository.save(direccion);
			}
		}
		nuevoUsuario.setDireccion(direccion);

		// Generar contraseña automática (ej. teléfono + nombre, luego hashearla)
		String passwordPlana = clienteRequest.getTelefono() + clienteRequest.getNombre().toLowerCase();
		nuevoUsuario.setPassword(passwordEncoder.encode(passwordPlana));

		// Asignar rol CLIENTE
		Rol rolCliente = rolRepository.findByNombre("CLIENTE").orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));
		nuevoUsuario.setRol(rolCliente);

		return usuarioRepository.save(nuevoUsuario);
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

