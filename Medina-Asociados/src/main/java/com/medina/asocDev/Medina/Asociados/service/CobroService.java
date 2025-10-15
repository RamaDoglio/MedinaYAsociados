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

import java.util.ArrayList;
import java.util.List;

import static com.medina.asocDev.Medina.Asociados.utils.Utils.mapCobroEntityToDTO;

@Service
public class CobroService {

	@Autowired
	private static CobroRepository cobroRepository;
	@Autowired
	private static EstadoRepository estadoRepository;
	@Autowired
	private static DetalleCobroService detalleCobroService;

	public static CobroDTO createCobro(CobroDTO cobroDTO) {
		Cobro cobro = new Cobro();
		cobro.setImporteTotal(cobroDTO.getImporteTotal());

		if (cobroDTO.getIdEstado() != null) {
			Estado estado = estadoRepository.findById(cobroDTO.getIdEstado()).orElse(null);
			cobro.setEstadoCobro(estado);
		}

		Cobro guardado = cobroRepository.save(cobro);
		return Utils.mapCobroEntityToDTO(guardado);
	}

	public static CobroDTO getCobroPorTurno(Long turnoId) {
		Cobro cobro = (Cobro) cobroRepository.findByTurno_IdTurno(turnoId);
		return Utils.mapCobroEntityToDTO(cobro);
	}


	public static CobroDTO getCobroPorId(Long id) {
		return cobroRepository.findById(id)
				.map(Utils::mapCobroEntityToDTO)
				.orElse(null);
	}

	public static CobroDTO updateCobro(Long id, CobroDTO cobroDTO) {
		return cobroRepository.findById(id).map(cobro -> {
			cobro.setImporteTotal(cobroDTO.getImporteTotal());
			if (cobroDTO.getIdEstado() != null) {
				Estado estado = estadoRepository.findById(cobroDTO.getIdEstado()).orElse(null);
				cobro.setEstadoCobro(estado);
			}
			Cobro actualizado = cobroRepository.save(cobro);
			return Utils.mapCobroEntityToDTO(actualizado);
		}).orElse(null);
	}

	public static boolean deleteCobro(Long id) {
		if (cobroRepository.existsById(id)) {
			cobroRepository.deleteById(id);
			return true;
		}
		return false;
	}

	public static CobroDTO reembolsar(Long id) {
		Cobro cobro = cobroRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Cobro no encontrado"));

		Estado estadoReembolsado = estadoRepository.findByNombreAndAmbito("REEMBOLSADO", "COBRO")
				.orElseThrow(() -> new RuntimeException("Estado REEMBOLSADO no encontrado"));;
		cobro.setEstadoCobro(estadoReembolsado);

		Cobro cobroActualizado = cobroRepository.save(cobro);

		// 👉 delega la creación del detalle
		detalleCobroService.crearDetalleReembolso(cobroActualizado);

		return Utils.mapCobroEntityToDTO(cobroActualizado);
	}
}

