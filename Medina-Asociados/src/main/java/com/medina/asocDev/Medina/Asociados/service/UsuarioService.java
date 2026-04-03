package com.medina.asocDev.Medina.Asociados.service;


import java.util.List;
import java.util.stream.Collectors;

import com.medina.asocDev.Medina.Asociados.dto.*;
import com.medina.asocDev.Medina.Asociados.entity.Direccion;
import com.medina.asocDev.Medina.Asociados.entity.Localidad;
import com.medina.asocDev.Medina.Asociados.entity.Rol;
import com.medina.asocDev.Medina.Asociados.repo.DireccionRepository;
import com.medina.asocDev.Medina.Asociados.repo.RolRepository;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import com.medina.asocDev.Medina.Asociados.utils.JWTUtils;
import com.medina.asocDev.Medina.Asociados.service.interfac.IUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.medina.asocDev.Medina.Asociados.entity.Usuario;
import com.medina.asocDev.Medina.Asociados.repo.UsuarioRepository;

@Service
public class UsuarioService implements IUserService {

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

		usuarioRepository.save(usuario);

		// Retornamos DTO completo
		return new MensajeResponse("Registro completado, redirigiendo al inicio de sesión");
	}

	@Override
	public Response register(Usuario user) {
		if (user.getEmail() == null || user.getEmail().isBlank()) {
			Response response = new Response();
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("El email es obligatorio");
			return response;
		}

		if (usuarioRepository.findByEmail(user.getEmail()).isPresent()) {
			Response response = new Response();
			response.setStatusCode(HttpStatus.CONFLICT.value());
			response.setMessage("Ya existe un usuario con ese email");
			return response;
		}

		Rol rol = user.getRol();
		if (rol == null || rol.getIdRol() == null) {
			rol = rolRepository.findByNombre("CLIENTE").orElseGet(() -> {
				Rol nuevoRol = new Rol();
				nuevoRol.setNombre("CLIENTE");
				nuevoRol.setDescripcion("Rol de cliente");
				return rolRepository.save(nuevoRol);
			});
		} else {
			Long rolId = rol.getIdRol();
			rol = rolRepository.findById(rolId)
					.orElseThrow(() -> new RuntimeException("Rol no encontrado con id " + rolId));
		}

		user.setRol(rol);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		Usuario usuarioGuardado = usuarioRepository.save(user);

		Response response = new Response();
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Usuario registrado correctamente");
		response.setUser(Utils.mapUserEntityToUserDTO(usuarioGuardado));
		return response;
	}

	@Override
	public Response login(LogInRequest loginRequest) {
		Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
				.orElse(null);

		if (usuario == null || usuario.getPassword() == null || !passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
			Response response = new Response();
			response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
			response.setMessage("Credenciales inválidas");
			return response;
		}

		User securityUser = new User(
				usuario.getEmail(),
				usuario.getPassword(),
				usuario.getRol() != null && usuario.getRol().getNombre() != null
						? List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre()))
						: List.of()
		);

		String token = jwtUtils.generateToken(securityUser);

		Response response = new Response();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Login exitoso");
		response.setToken(token);
		response.setRole(usuario.getRol() != null ? usuario.getRol().getNombre() : null);
		response.setExpirationTime(java.time.LocalDateTime.now().plusHours(3).toString());
		response.setUser(Utils.mapUserEntityToUserDTO(usuario));
		return response;
	}

	@Override
	public Response getAllUsersResponse() {
		Response response = new Response();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Listado de usuarios");
		response.setUserList(usuarioRepository.findAll().stream().map(Utils::mapUserEntityToUserDTO).collect(Collectors.toList()));
		return response;
	}

	@Override
	public Response getUserBookingHistory(String userId) {
		Response response = new Response();
		try {
			Long id = Long.parseLong(userId);
			Usuario usuario = usuarioRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Historial de turnos del usuario");
			response.setUser(Utils.mapUsuarioEntityToDTOxTurnos(usuario));
			return response;
		} catch (NumberFormatException e) {
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("El id de usuario debe ser numérico");
			return response;
		}
	}

	@Override
	public Response deleteUser(String userId) {
		Response response = new Response();
		try {
			Long id = Long.parseLong(userId);
			if (usuarioRepository.existsById(id)) {
				usuarioRepository.deleteById(id);
				response.setStatusCode(HttpStatus.OK.value());
				response.setMessage("Usuario eliminado correctamente");
				return response;
			}
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			response.setMessage("Usuario no encontrado");
			return response;
		} catch (NumberFormatException e) {
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("El id de usuario debe ser numérico");
			return response;
		}
	}

	@Override
	public Response getUserById(String userId) {
		Response response = new Response();
		try {
			Long id = Long.parseLong(userId);
			Usuario usuario = usuarioRepository.findById(id).orElse(null);
			if (usuario == null) {
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setMessage("Usuario no encontrado");
				return response;
			}
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Usuario encontrado");
			response.setUser(Utils.mapUserEntityToUserDTO(usuario));
			return response;
		} catch (NumberFormatException e) {
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("El id de usuario debe ser numérico");
			return response;
		}
	}

	@Override
	public Response getMyInfo(String email) {
		Response response = new Response();
		Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
		if (usuario == null) {
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			response.setMessage("Usuario no encontrado");
			return response;
		}
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Información del usuario");
		response.setUser(Utils.mapUserEntityToUserDTO(usuario));
		return response;
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

	public List<UsuarioDTO> getAllUsersList() {
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
			usuario.setDni(usuarioDTO.getDni());
			usuario.setTelefono(usuarioDTO.getTelefono() != null ? usuarioDTO.getTelefono() : null);
			usuario.setEmail(usuarioDTO.getEmail());
			Usuario actualizado = usuarioRepository.save(usuario);
			return Utils.mapUserEntityToUserDTO(actualizado);
		}).orElse(null);
	}
}

