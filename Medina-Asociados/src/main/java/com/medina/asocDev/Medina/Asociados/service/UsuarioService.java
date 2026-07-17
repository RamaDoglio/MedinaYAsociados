package com.medina.asocDev.Medina.Asociados.service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.medina.asocDev.Medina.Asociados.dto.*;
import com.medina.asocDev.Medina.Asociados.entity.Direccion;
import com.medina.asocDev.Medina.Asociados.entity.Localidad;
import com.medina.asocDev.Medina.Asociados.entity.Rol;
import com.medina.asocDev.Medina.Asociados.excepetion.OurException;
import com.medina.asocDev.Medina.Asociados.repo.DireccionRepository;
import com.medina.asocDev.Medina.Asociados.repo.LocalidadRepository;
import com.medina.asocDev.Medina.Asociados.repo.RolRepository;
import com.medina.asocDev.Medina.Asociados.service.interfac.IUserService;
import com.medina.asocDev.Medina.Asociados.utils.JWTUtils;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.medina.asocDev.Medina.Asociados.entity.TokenBlacklisted;
import com.medina.asocDev.Medina.Asociados.entity.Usuario;
import com.medina.asocDev.Medina.Asociados.repo.TokenBlacklistedRepository;
import com.medina.asocDev.Medina.Asociados.repo.TurnoRepository;
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
	private LocalidadRepository localidadRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JWTUtils jwtUtils;
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenBlacklistedRepository tokenBlacklistedRepository;

	@Autowired
	private TurnoRepository turnoRepository;


	public MensajeResponse createUsuario(RegisterDTO registerDTO) {
		Rol rol = rolRepository.findByNombre("CLIENTE")
				.orElseThrow(() -> new RuntimeException(
						"Rol no encontrado el rol"));

		Direccion direccion = null;
		if (registerDTO.getDireccion() != null) {
			DireccionDTO direccionDTO = registerDTO.getDireccion();

			if (direccionDTO.getIdDireccion() != null) {
				direccion = direccionRepository.findById(direccionDTO.getIdDireccion())
						.orElseThrow(() -> new EntityNotFoundException("Dirección no encontrada"));
			}
			else {
				direccion = Utils.mapDireccionDTOToEntity(direccionDTO);

				if (direccionDTO.getLocalidad() != null) {
					Localidad localidad = new Localidad();
					localidad.setIdLocalidad(direccionDTO.getLocalidad());
					direccion.setLocalidad(localidad);
				}

				direccion = direccionRepository.save(direccion);
			}
		}

		registerDTO.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

		Usuario usuario = Utils.mapRegistroDTOToEntity(registerDTO, rol, direccion);

		Usuario usuarioGuardado = usuarioRepository.save(usuario);

		return new MensajeResponse("Registro completado, redirigiendo al inicio de sesión");
	}

	@Transactional
	public Usuario getOrCreateUsuario(ClienteOfflineRequest clienteRequest) {
		Usuario usuarioExistente = usuarioRepository.findByEmail(clienteRequest.getEmail())
				.orElse(usuarioRepository.findByTelefono(clienteRequest.getTelefono()).orElse(null));

		if (usuarioExistente != null) {
			return usuarioExistente;
		}

		Usuario nuevoUsuario = new Usuario();
		nuevoUsuario.setNombre(clienteRequest.getNombre());
		nuevoUsuario.setApellido(clienteRequest.getApellido());
		nuevoUsuario.setDni(clienteRequest.getDni());
		nuevoUsuario.setTelefono(clienteRequest.getTelefono());
		nuevoUsuario.setEmail(clienteRequest.getEmail());

		Direccion direccion = null;
		if (clienteRequest.getDireccion() != null) {
			DireccionDTO direccionDTO = clienteRequest.getDireccion();

			if (direccionDTO.getIdDireccion() != null) {
				direccion = direccionRepository.findById(direccionDTO.getIdDireccion())
						.orElseThrow(() -> new EntityNotFoundException("Dirección no encontrada"));
			} else {
				direccion = Utils.mapDireccionDTOToEntity(direccionDTO);

				if (direccionDTO.getLocalidad() != null) {
					Localidad localidad = new Localidad();
					localidad.setIdLocalidad(direccionDTO.getLocalidad());
					direccion.setLocalidad(localidad);
				}

				direccion = direccionRepository.save(direccion);
			}
		}
		nuevoUsuario.setDireccion(direccion);

		String passwordPlana = clienteRequest.getTelefono() + clienteRequest.getNombre().toLowerCase();
		nuevoUsuario.setPassword(passwordEncoder.encode(passwordPlana));

		Rol rolCliente = rolRepository.findByNombre("CLIENTE")
				.orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));
		nuevoUsuario.getRolesUsuario().add(rolCliente);

		return usuarioRepository.save(nuevoUsuario);
	}

	public Response login(LogInRequest loginRequest) {
		Response response = new Response();

		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							loginRequest.getEmail(),
							loginRequest.getPassword()
					)
			);

			var usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
					.orElseThrow(() -> new OurException("user Not found"));

			List<String> roles = usuario.getRolesUsuario()
					.stream()
					.map(Rol::getNombre)
					.collect(Collectors.toList());

			UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
					.username(usuario.getEmail())
					.password(usuario.getPassword())
					.authorities(roles.toArray(new String[0]))
					.build();

			var token = jwtUtils.generateToken(userDetails);

			response.setStatusCode(200);
			response.setToken(token);
			response.setRoles(roles);
			response.setExpirationTime("7 Days");
			response.setMessage("successful");

			UsuarioDTO userDTO = new UsuarioDTO();
			userDTO.setIdUsuario(usuario.getIdUsuario());
			userDTO.setNombre(usuario.getNombre());
			userDTO.setApellido(usuario.getApellido());
			response.setUser(userDTO);

		} catch (OurException e) {
			response.setStatusCode(404);
			response.setMessage(e.getMessage());
		} catch (BadCredentialsException e) {
			response.setStatusCode(401);
			response.setMessage("Credenciales inválidas");
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error en login: " + e.getMessage());
		}
		return response;
	}


	@Override
	public Response getMyInfo(String email) {
		Response response = new Response();
		try {
			Usuario user = usuarioRepository.findByEmail(email)
					.orElseThrow(() -> new OurException("Usuario no encontrado"));

			UsuarioDTO userDTO = Utils.mapUserEntityToUserDTO(user);
			response.setStatusCode(200);
			response.setData(userDTO);
			response.setMessage("Información del usuario");
		} catch (OurException | EntityNotFoundException e) {
			response.setStatusCode(404);
			response.setMessage(e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error al obtener información: " + e.getMessage());
		}
		return response;
	}

	@Override
	public Response getUserTurnoHistory(String userId) {
		Response response = new Response();
		try {
			Long id = Long.parseLong(userId);
			response.setStatusCode(200);
			response.setData(new ArrayList<>());
			response.setMessage("Historial de turnos del usuario");
		} catch (NumberFormatException e) {
			response.setStatusCode(400);
			response.setMessage("ID de usuario inválido");
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error al obtener historial: " + e.getMessage());
		}
		return response;
	}

	@Override
	public Response getAllUsers() {
		Response response = new Response();
		try {
			List<Usuario> usuarios = usuarioRepository.findAll();
			List<UsuarioDTO> usuariosDTO = usuarios.stream()
					.map(Utils::mapUserEntityToUserDTO)
					.collect(Collectors.toList());

			response.setStatusCode(200);
			response.setData((UsuarioDTO) usuariosDTO);
			response.setMessage("Usuarios obtenidos correctamente");
			return response;
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error al obtener usuarios: " + e.getMessage());
			return response;
		}
	}

	public Page<UsuarioDTO> getAllUsers(Pageable pageable) {
		return usuarioRepository.findAll(pageable)
				.map(Utils::mapUserEntityToUserDTO);
	}

	@Override
	public Response register(Usuario user) {
		RegisterDTO dto = Utils.mapUsuarioToRegisterDTO(user);
		MensajeResponse mensaje = createUsuario(dto);
		Response response = new Response();
		response.setStatusCode(200);
		response.setMessage(mensaje.getMessage());
		return response;
	}

	@Override
	public Response getUserById(String userId) {
		Response response = new Response();
		try {
			Long id = Long.parseLong(userId);
			UsuarioDTO user = usuarioRepository.findById(id)
					.map(Utils::mapUserEntityToUserDTO)
					.orElse(null);

			if (user != null) {
				response.setStatusCode(200);
				response.setData(user);
				response.setMessage("Usuario encontrado");
			} else {
				response.setStatusCode(404);
				response.setMessage("Usuario no encontrado");
			}
		} catch (NumberFormatException e) {
			response.setStatusCode(400);
			response.setMessage("ID de usuario inválido");
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error al obtener usuario: " + e.getMessage());
		}
		return response;
	}

	@Override
	@CacheEvict(value = "users", allEntries = true)
	public Response deleteUser(String userId) {
		Response response = new Response();
		try {
			Long id = Long.parseLong(userId);
			if (usuarioRepository.existsById(id)) {
				usuarioRepository.deleteById(id);
				response.setStatusCode(200);
				response.setMessage("Usuario eliminado correctamente");
			} else {
				response.setStatusCode(404);
				response.setMessage("Usuario no encontrado");
			}
		} catch (NumberFormatException e) {
			response.setStatusCode(400);
			response.setMessage("ID de usuario inválido");
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error al eliminar usuario: " + e.getMessage());
		}
		return response;
	}

    @CacheEvict(value = "users", allEntries = true)
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

    @CacheEvict(value = "users", allEntries = true)
    public boolean deleteUserInternal(Long id) {
        if (usuarioRepository.existsById(id)) {
			usuarioRepository.deleteById(id);
			return true;
		}
		return false;
	}

	public UsuarioDTO getUserByIdInternal(Long id) {
		return usuarioRepository.findById(id)
				.map(Utils::mapUserEntityToUserDTO)
				.orElse(null);
	}

	public Response buscarPorDni(String dni) {
		Response response = new Response();
		try {
			List<Usuario> usuarios = usuarioRepository.findByDniStartingWithAndRolCliente(dni);
			List<UsuarioDTO> usuariosDTO = usuarios.stream()
					.map(u -> {
						UsuarioDTO dto = Utils.mapUserEntityToUserDTO(u);
						dto.setPassword(null);
						dto.setIdRol(null);
						return dto;
					})
					.collect(Collectors.toList());
			response.setStatusCode(200);
			response.setData(usuariosDTO);
			response.setMessage("Usuarios encontrados: " + usuariosDTO.size());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error al buscar usuarios por DNI: " + e.getMessage());
		}
		return response;
	}

	public Page<UsuarioDTO> buscarPorDni(String dni, Pageable pageable) {
		return usuarioRepository.findByDniStartingWithAndRolCliente(dni, pageable)
				.map(u -> {
					UsuarioDTO dto = Utils.mapUserEntityToUserDTO(u);
					dto.setPassword(null);
					dto.setIdRol(null);
					return dto;
				});
	}

	public Response getClienteDetalle(Long id) {
		Response response = new Response();
		try {
			Usuario usuario = usuarioRepository.findById(id)
					.orElseThrow(() -> new OurException("Cliente no encontrado"));

			ClienteDetalleDTO detalle = new ClienteDetalleDTO();
			detalle.setIdUsuario(usuario.getIdUsuario());
			detalle.setNombre(usuario.getNombre());
			detalle.setApellido(usuario.getApellido());
			detalle.setDni(usuario.getDni());
			detalle.setTelefono(usuario.getTelefono());
			detalle.setEmail(usuario.getEmail());
			if (usuario.getDireccion() != null) {
				detalle.setDireccion(Utils.mapDireccionEntityToDTO(usuario.getDireccion()));
				if (usuario.getDireccion().getLocalidad() != null) {
					detalle.setLocalidad(Utils.mapLocalidadEntityToDTO(usuario.getDireccion().getLocalidad()));
				}
			}

			List<EstadisticaDTO> turnosPorEstado = turnoRepository.countTurnosByClienteAndEstados(id);
			detalle.setTurnosPorEstado(turnosPorEstado);

			response.setStatusCode(200);
			response.setData(detalle);
			response.setMessage("Detalle del cliente obtenido correctamente");
		} catch (OurException e) {
			response.setStatusCode(404);
			response.setMessage(e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error al obtener detalle del cliente: " + e.getMessage());
		}
		return response;
	}

	@Transactional
	public MensajeResponse logout(String token) {
		if (tokenBlacklistedRepository.findByToken(token).isPresent()) {
			return new MensajeResponse("La sesión ya fue cerrada");
		}

		Date expiracion = jwtUtils.extractExpiration(token);

		TokenBlacklisted tokenBlacklisted = new TokenBlacklisted();
		tokenBlacklisted.setToken(token);
		tokenBlacklisted.setFechaExpiracion(expiracion);
		tokenBlacklistedRepository.save(tokenBlacklisted);

		return new MensajeResponse("Sesión cerrada exitosamente");
	}
}

