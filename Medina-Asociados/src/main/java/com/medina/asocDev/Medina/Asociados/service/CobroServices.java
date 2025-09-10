package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.CobroDTO;
import com.medina.asocDev.Medina.Asociados.dto.EstadoDTO;
import com.medina.asocDev.Medina.Asociados.entity.Cobro;
import com.medina.asocDev.Medina.Asociados.entity.Estado;
import com.medina.asocDev.Medina.Asociados.repo.CobroRepository;
import com.medina.asocDev.Medina.Asociados.repo.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CobroServices {


	@Autowired
	private CobroRepository cobroRepository;
	@Autowired
	private EstadoRepository estadoRepository;


	public CobroDTO createCobro(CobroDTO cobroDTO) {
		Cobro cobro = new Cobro();
		cobro.setImporteTotal(cobroDTO.getImporteTotal());
		if (cobroDTO.getEstadoCobro() != null && cobroDTO.getEstadoCobro().getIdEstado() != null) {
			Estado estado = estadoRepository.findById(cobroDTO.getEstadoCobro().getIdEstado()).orElse(null);
			cobro.setEstadoCobro(estado);
		}
		Cobro guardado = cobroRepository.save(cobro);
		return mapToDTO(guardado);
	}


	public List<CobroDTO> getCobrosPorTurno(Long turnoId) {
		List<Cobro> cobros = cobroRepository.findByTurno_IdTurno(turnoId);
		List<CobroDTO> dtos = new java.util.ArrayList<>();
		for (Cobro c : cobros) {
			dtos.add(mapToDTO(c));
		}
		return dtos;
	}


	public CobroDTO getCobroPorId(Long id) {
		return cobroRepository.findById(id).map(this::mapToDTO).orElse(null);
	}


	public CobroDTO updateCobro(Long id, CobroDTO cobroDTO) {
		return cobroRepository.findById(id).map(cobro -> {
			cobro.setImporteTotal(cobroDTO.getImporteTotal());
			if (cobroDTO.getEstadoCobro() != null && cobroDTO.getEstadoCobro().getIdEstado() != null) {
				Estado estado = estadoRepository.findById(cobroDTO.getEstadoCobro().getIdEstado()).orElse(null);
				cobro.setEstadoCobro(estado);
			}
			Cobro actualizado = cobroRepository.save(cobro);
			return mapToDTO(actualizado);
		}).orElse(null);
	}


	public boolean deleteCobro(Long id) {
		if (cobroRepository.existsById(id)) {
			cobroRepository.deleteById(id);
			return true;
		}
		return false;
	}

	private CobroDTO mapToDTO(Cobro cobro) {
		CobroDTO dto = new CobroDTO();
		dto.setIdCobro(cobro.getIdCobro());
		dto.setImporteTotal(cobro.getImporteTotal());
		if (cobro.getEstadoCobro() != null) {
			EstadoDTO estadoDTO = new EstadoDTO();
			estadoDTO.setIdEstado(cobro.getEstadoCobro().getIdEstado());
			estadoDTO.setValor(cobro.getEstadoCobro().getNombreEstado());
			estadoDTO.setAmbito(cobro.getEstadoCobro().getAmbito());
			dto.setEstadoCobro(estadoDTO);
		}
		return dto;
	}
}
