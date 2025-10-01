package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.CobroDTO;
import com.medina.asocDev.Medina.Asociados.dto.EstadoDTO;
import com.medina.asocDev.Medina.Asociados.entity.Cobro;
import com.medina.asocDev.Medina.Asociados.entity.Estado;
import com.medina.asocDev.Medina.Asociados.repo.CobroRepository;
import com.medina.asocDev.Medina.Asociados.repo.EstadoRepository;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import static com.medina.asocDev.Medina.Asociados.utils.Utils.mapCobroEntityToDTO;

@Service
public class CobroService {


	@Autowired
	private CobroRepository cobroRepository;
	@Autowired
	private EstadoRepository estadoRepository;


	public CobroDTO createCobro(CobroDTO cobroDTO) {
		Cobro cobro = new Cobro();
		cobro.setImporteTotal(cobroDTO.getImporteTotal());
		if (cobroDTO.getIdEstado() != null) {
			Estado estado = estadoRepository.findById(cobroDTO.getIdEstado()).orElse(null);
			cobro.setEstadoCobro(estado);
		}
		Cobro guardado = cobroRepository.save(cobro);
		return mapCobroEntityToDTO(guardado);
	}


	public List<CobroDTO> getCobrosPorTurno(Long turnoId) {
		List<Cobro> cobros = cobroRepository.findByTurno_IdTurno(turnoId);
		List<CobroDTO> dtos = new java.util.ArrayList<>();
		for (Cobro c : cobros) {
			dtos.add(mapCobroEntityToDTO(c));
		}
		return dtos;
	}


	public CobroDTO getCobroPorId(Long id) {
		return cobroRepository.findById(id)
				.map(Utils::mapCobroEntityToDTO) // ahora sí compila y es claro
				.orElse(null);
	}


	public CobroDTO updateCobro(Long id, CobroDTO cobroDTO) {
		return cobroRepository.findById(id).map(cobro -> {
			cobro.setImporteTotal(cobroDTO.getImporteTotal());
			if (cobroDTO.getIdCobro() != null) {
				Estado estado = estadoRepository.findById(cobroDTO.getIdEstado()).orElse(null);
				cobro.setEstadoCobro(estado);
			}
			Cobro actualizado = cobroRepository.save(cobro);
			return mapCobroEntityToDTO(actualizado);
		}).orElse(null);
	}


	public boolean deleteCobro(Long id) {
		if (cobroRepository.existsById(id)) {
			cobroRepository.deleteById(id);
			return true;
		}
		return false;
	}
}
