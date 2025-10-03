package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.AbogadoDTO;
import com.medina.asocDev.Medina.Asociados.dto.EspecialidadDTO;
import com.medina.asocDev.Medina.Asociados.entity.Abogado;
import com.medina.asocDev.Medina.Asociados.entity.Especialidad;
import com.medina.asocDev.Medina.Asociados.entity.Rol;
import com.medina.asocDev.Medina.Asociados.entity.Usuario;
import com.medina.asocDev.Medina.Asociados.repo.AbogadoRepository;
import com.medina.asocDev.Medina.Asociados.repo.EspecialidadRepository;
import com.medina.asocDev.Medina.Asociados.repo.RolRepository;
import com.medina.asocDev.Medina.Asociados.repo.UsuarioRepository;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AbogadoService {

	@Autowired
	private AbogadoRepository abogadoRepository;
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private RolRepository rolRepository;
	@Autowired
	private EspecialidadRepository especialidadRepository;

	public AbogadoDTO createAbogado(Long idUsuario, AbogadoDTO abogadoDTO) {
		// 1. Buscar usuario
		Usuario usuario = usuarioRepository.findById(idUsuario)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado con id " + idUsuario));

		// 2. Cambiar rol a ABOGADO
		Rol rol = rolRepository.findById(2L)
				.orElseThrow(() -> new RuntimeException("Rol no encontrado con id " + 2));

		usuario.setRol(rol);
		usuarioRepository.save(usuario);

		// 3. Resolver especialidades (puede ser lista vacía)
		List<Especialidad> especialidades = new ArrayList<>();
		if (abogadoDTO.getEspecialidadesAbogado() != null && !abogadoDTO.getEspecialidadesAbogado().isEmpty()) {
			for (Long espId : abogadoDTO.getEspecialidadesAbogado()) {
				especialidadRepository.findById(espId).ifPresent(especialidades::add);
			}
		}

		// 4. Mapear y guardar abogado
		Abogado abogado = Utils.mapAbogadoDTOToEntity(abogadoDTO, usuario, especialidades);
		Abogado guardado = abogadoRepository.save(abogado);

		// 5. Retornar DTO
		return Utils.mapAbogadoEntityToDTOxUsuarioSinTurno(guardado);
	}

	public List<AbogadoDTO> getAll() {
		List<Abogado> abogados = abogadoRepository.findAll();
		List<AbogadoDTO> dtos = new ArrayList<>();
		for (Abogado ab : abogados) {
			dtos.add(Utils.mapAbogadoEntityToDTOxUsuarioSinTurno(ab));
		}
		return dtos;
	}

	public AbogadoDTO getAbogadoById(Long id) {
		return abogadoRepository.findById(id)
				.map(Utils::mapAbogadoEntityToDTOxUsuarioSinTurno)
				.orElse(null);
	}

	public boolean deleteAbogado(Long id) {
		if (abogadoRepository.existsById(id)) {
			abogadoRepository.deleteById(id);
			return true;
		}
		return false;
	}

	public AbogadoDTO updateMatricula(Long idAbogado, String nuevaMatricula) {
		return abogadoRepository.findById(idAbogado).map(abogado -> {
			abogado.setMatricula(nuevaMatricula);
			Abogado actualizado = abogadoRepository.save(abogado);
			return Utils.mapAbogadoEntityToDTOxUsuarioSinTurno(actualizado);
		}).orElse(null);
	}

	public AbogadoDTO updateEspecialidades(Long idAbogado, List<Long> especialidadesIds) {
		return abogadoRepository.findById(idAbogado).map(abogado -> {
			List<Especialidad> especialidades = new ArrayList<>();
			if (especialidadesIds != null) {
				for (Long espId : especialidadesIds) {
					especialidadRepository.findById(espId).ifPresent(especialidades::add);
				}
			}
			abogado.setEspecialidadesAbogado(especialidades);
			Abogado actualizado = abogadoRepository.save(abogado);
			return Utils.mapAbogadoEntityToDTOxUsuarioSinTurno(actualizado);
		}).orElse(null);
	}

	public List<AbogadoDTO> getAbogadosByEspecialidad(Long idEspecialidad) {
		List<Abogado> abogados = abogadoRepository.findByEspecialidadesAbogado_IdEspecialidad(idEspecialidad);
		List<AbogadoDTO> dtos = new ArrayList<>();
		for (Abogado ab : abogados) {
			dtos.add(Utils.mapAbogadoEntityToDTOxUsuarioSinTurno(ab));
		}
		return dtos;
	}

}