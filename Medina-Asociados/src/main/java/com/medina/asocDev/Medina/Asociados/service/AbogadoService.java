package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.AbogadoDTO;
import com.medina.asocDev.Medina.Asociados.dto.EspecialidadDTO;
import com.medina.asocDev.Medina.Asociados.entity.Abogado;
import com.medina.asocDev.Medina.Asociados.entity.Especialidad;
import com.medina.asocDev.Medina.Asociados.entity.Usuario;
import com.medina.asocDev.Medina.Asociados.repo.AbogadoRepository;
import com.medina.asocDev.Medina.Asociados.repo.EspecialidadRepository;
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
	private EspecialidadRepository especialidadRepository;

	public AbogadoDTO createAbogado(Long idUsuario, AbogadoDTO abogadoDTO) {
		Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
		if (usuarioOpt.isEmpty()) return null;

		Abogado abogado = new Abogado();
		abogado.setUsuario(usuarioOpt.get());
		abogado.setMatricula(abogadoDTO.getMatricula());

		// Mapear solo IDs de especialidades
		List<Especialidad> especialidades = new ArrayList<>();
		if (abogadoDTO.getEspecialidadesAbogado() != null) {
			for (Long espId : abogadoDTO.getEspecialidadesAbogado()) {
				especialidadRepository.findById(espId).ifPresent(especialidades::add);
			}
		}
		abogado.setEspecialidadesAbogado(especialidades);

		Abogado guardado = abogadoRepository.save(abogado);
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

	public AbogadoDTO updateAbogado(Long id, AbogadoDTO abogadoDTO) {
		return abogadoRepository.findById(id).map(abogado -> {
			abogado.setMatricula(abogadoDTO.getMatricula());

			// Mapear solo IDs de especialidades
			List<Especialidad> especialidades = new ArrayList<>();
			if (abogadoDTO.getEspecialidadesAbogado() != null) {
				for (Long espId : abogadoDTO.getEspecialidadesAbogado()) {
					especialidadRepository.findById(espId).ifPresent(especialidades::add);
				}
			}
			abogado.setEspecialidadesAbogado(especialidades);

			Abogado actualizado = abogadoRepository.save(abogado);
			return Utils.mapAbogadoEntityToDTOxUsuarioSinTurno(actualizado);
		}).orElse(null);
	}

	public List<AbogadoDTO> getAbogadosByEspecialidad(String nombreEspecialidad) {
		List<Abogado> abogados = abogadoRepository.findByEspecialidadNombre(nombreEspecialidad);
		List<AbogadoDTO> dtos = new ArrayList<>();
		for (Abogado ab : abogados) {
			dtos.add(Utils.mapAbogadoEntityToDTOxUsuarioSinTurno(ab));
		}
		return dtos;
	}
}