package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.AbogadoDTO;
import com.medina.asocDev.Medina.Asociados.entity.*;
import com.medina.asocDev.Medina.Asociados.repo.*;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.time.DayOfWeek;

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
	@Autowired
	private TurnoRepository turnoRepository;

	public AbogadoDTO createAbogado(Long idUsuario, AbogadoDTO abogadoDTO) {
		// 1. Buscar usuario
		Usuario usuario = usuarioRepository.findById(idUsuario)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado con id " + idUsuario));

		// 2. Cambiar rol a ABOGADO
		Rol rolAbogado = rolRepository.findByNombre("ABOGADO")
				.orElseThrow(() -> new RuntimeException("Rol ABOGADO no encontrado"));

		usuario.getRolesUsuario().removeIf(r -> r.getNombre().equals("CLIENTE"));
		usuario.getRolesUsuario().add(rolAbogado);
		usuarioRepository.save(usuario);

		// 3. Resolver especialidades (el resto igual)
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

	public Page<AbogadoDTO> getAll(Pageable pageable) {
		return abogadoRepository.findAll(pageable)
				.map(Utils::mapAbogadoEntityToDTOxUsuarioSinTurno);
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

	public Page<AbogadoDTO> getAbogadosByEspecialidad(Long idEspecialidad, Pageable pageable) {
		return abogadoRepository.findByEspecialidadesAbogado_IdEspecialidad(idEspecialidad, pageable)
				.map(Utils::mapAbogadoEntityToDTOxUsuarioSinTurno);
	}

	public boolean esFinDeSemana(LocalDate fecha) {
		DayOfWeek diaSemana = fecha.getDayOfWeek();
		return diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY;
	}

	// Obtener los horarios ocupados de un abogado en una fecha específica
	public List<LocalTime> obtenerHorariosOcupados(Long idAbogado, LocalDate fecha) {
		List<Turno> turnos = turnoRepository.findTurnosOcupadosPorAbogadoEnFecha(idAbogado, fecha);
		return turnos.stream()
				.map(t -> t.getHorarioTurno().toLocalTime())
				.toList();
	}

	// Generar horarios disponibles entre 12:00 y 16:30 (intervalos de 45 minutos)
	public List<LocalTime> obtenerHorariosDisponibles(Long idAbogado, LocalDate fecha) {
		if (esFinDeSemana(fecha)) {
			return new ArrayList<>(); // Retorna lista vacía = NO hay horarios disponibles
		}

		List<LocalTime> horariosTotales = new ArrayList<>();
		LocalTime inicio = LocalTime.of(12, 0);
		LocalTime fin = LocalTime.of(16, 30);
		for (LocalTime actual = inicio; !actual.isAfter(fin); actual = actual.plusMinutes(45)) {
			horariosTotales.add(actual);
		}

		List<LocalTime> ocupados = obtenerHorariosOcupados(idAbogado, fecha);
		return horariosTotales.stream()
				.filter(h -> !ocupados.contains(h))
				.toList();
	}

	// Verificar si un horario específico está disponible
	public boolean verificarDisponibilidad(Long idAbogado, LocalDateTime fechaHora) {
		LocalDate fecha = fechaHora.toLocalDate();

		if (esFinDeSemana(fecha)) {
			return false;
		}

		List<LocalTime> ocupados = obtenerHorariosOcupados(idAbogado, fecha);
		return !ocupados.contains(fechaHora.toLocalTime());
	}

}