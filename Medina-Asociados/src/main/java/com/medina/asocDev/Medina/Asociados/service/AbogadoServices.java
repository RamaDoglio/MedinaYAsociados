package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.AbogadoDTO;
import com.medina.asocDev.Medina.Asociados.dto.EspecialidadDTO;
import com.medina.asocDev.Medina.Asociados.entity.Abogado;
import com.medina.asocDev.Medina.Asociados.entity.Especialidad;
import com.medina.asocDev.Medina.Asociados.entity.Usuario;
import com.medina.asocDev.Medina.Asociados.repo.AbogadoRepository;
import com.medina.asocDev.Medina.Asociados.repo.UsuarioRepository;
import com.medina.asocDev.Medina.Asociados.repo.EspecialidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AbogadoServices {

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
		List<Especialidad> especialidades = new ArrayList<>();
		if (abogadoDTO.getEspecialidadesAbogado() != null) {
			for (EspecialidadDTO espDTO : abogadoDTO.getEspecialidadesAbogado()) {
				Especialidad esp = especialidadRepository.findByNombreEspecialidad(espDTO.getNombreEspecialidad());
				if (esp != null) especialidades.add(esp);
			}
		}
		abogado.setEspecialidadesAbogado(especialidades);
		Abogado guardado = abogadoRepository.save(abogado);
		return mapToDTO(guardado);
	}

	public List<AbogadoDTO> getAll() {
		List<Abogado> abogados = abogadoRepository.findAll();
		List<AbogadoDTO> dtos = new ArrayList<>();
		for (Abogado ab : abogados) {
			dtos.add(mapToDTO(ab));
		}
		return dtos;
	}

	public AbogadoDTO getAbogadoById(Long id) {
		return abogadoRepository.findById(id).map(this::mapToDTO).orElse(null);
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
			List<Especialidad> especialidades = new ArrayList<>();
			if (abogadoDTO.getEspecialidadesAbogado() != null) {
				for (EspecialidadDTO espDTO : abogadoDTO.getEspecialidadesAbogado()) {
					Especialidad esp = especialidadRepository.findByNombreEspecialidad(espDTO.getNombreEspecialidad());
					if (esp != null) especialidades.add(esp);
				}
			}
			abogado.setEspecialidadesAbogado(especialidades);
			Abogado actualizado = abogadoRepository.save(abogado);
			return mapToDTO(actualizado);
		}).orElse(null);
	}

	private AbogadoDTO mapToDTO(Abogado abogado) {
		AbogadoDTO dto = new AbogadoDTO();
		dto.setMatricula(abogado.getMatricula());
		List<EspecialidadDTO> especialidadDTOS = new ArrayList<>();
		if (abogado.getEspecialidadesAbogado() != null) {
			for (Especialidad esp : abogado.getEspecialidadesAbogado()) {
				EspecialidadDTO espDTO = new EspecialidadDTO();
				espDTO.setNombreEspecialidad(esp.getNombreEspecialidad());
				espDTO.setDescripcionEspecialidad(esp.getDescripcionEspecialidad());
				especialidadDTOS.add(espDTO);
			}
		}
		dto.setEspecialidadesAbogado(especialidadDTOS);
		return dto;
	}
}
