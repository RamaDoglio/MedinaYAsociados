package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.CobroConDetallesDTO;
import com.medina.asocDev.Medina.Asociados.dto.CobroDTO;
import com.medina.asocDev.Medina.Asociados.dto.EstadoDTO;
import com.medina.asocDev.Medina.Asociados.entity.Cobro;
import com.medina.asocDev.Medina.Asociados.entity.DetalleCobro;
import com.medina.asocDev.Medina.Asociados.entity.Estado;
import com.medina.asocDev.Medina.Asociados.entity.Turno;
import com.medina.asocDev.Medina.Asociados.repo.CobroRepository;
import com.medina.asocDev.Medina.Asociados.repo.DetalleCobroRepository;
import com.medina.asocDev.Medina.Asociados.repo.EstadoRepository;
import com.medina.asocDev.Medina.Asociados.repo.TurnoRepository;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.medina.asocDev.Medina.Asociados.utils.Utils.mapCobroEntityToDTO;

@Service
public class CobroService {

	@Autowired
	private CobroRepository cobroRepository;
	@Autowired
	private EstadoRepository estadoRepository;
	@Autowired
	private DetalleCobroService detalleCobroService;
	@Autowired
	private DetalleCobroRepository detalleCobroRepository;
	@Autowired
	private TurnoRepository turnoRepository;
	@Autowired
	private HistorialTurnoService historialTurnoService;
	@Autowired
	private NotificacionTurnoService notificacionTurnoService;
	@Autowired
	private EmailQueueService emailQueueService;

	@Transactional
	public CobroDTO createCobro(CobroDTO cobroDTO) {
		Cobro cobro = new Cobro();
		cobro.setImporteTotal(cobroDTO.getImporteTotal());

		if (cobroDTO.getIdEstado() != null) {
			Estado estado = estadoRepository.findById(cobroDTO.getIdEstado()).orElse(null);
			cobro.setEstadoCobro(estado);
		}

		Cobro guardado = cobroRepository.save(cobro);
		return Utils.mapCobroEntityToDTO(guardado);
	}

	public CobroDTO getCobroPorTurno(Long turnoId) {
		Cobro cobro = cobroRepository.findByTurno_IdTurno(turnoId).stream().findFirst().orElse(null);
		return Utils.mapCobroEntityToDTO(cobro);
	}


	public CobroDTO getCobroPorId(Long id) {
		return cobroRepository.findById(id)
				.map(Utils::mapCobroEntityToDTO)
				.orElse(null);
	}

	@Transactional
	public CobroDTO updateCobro(Long id, CobroDTO cobroDTO) {
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

	@Transactional
	public boolean deleteCobro(Long id) {
		if (cobroRepository.existsById(id)) {
			cobroRepository.deleteById(id);
			return true;
		}
		return false;
	}

	@Transactional
	public CobroDTO reembolsar(Cobro cobro) {
		Estado estadoReembolsado = estadoRepository.findByNombreAndAmbito("REEMBOLSADO", "COBRO")
				.orElseThrow(() -> new RuntimeException("Estado REEMBOLSADO no encontrado"));
		cobro.setEstadoCobro(estadoReembolsado);

		Cobro cobroActualizado = cobroRepository.save(cobro);

		detalleCobroService.crearDetalleCobro(cobroActualizado.getIdCobro(), 2L);

		return Utils.mapCobroEntityToDTO(cobroActualizado);
	}

	@Transactional
	public CobroDTO marcarComoPagado(Cobro cobro) {
		// Idempotencia: si ya está PAGADO, no reprocesar
		if (cobro.getEstadoCobro() != null && "PAGADO".equals(cobro.getEstadoCobro().getNombreEstado())) {
			return Utils.mapCobroEntityToDTO(cobro);
		}

		// 1. Actualizar estado del cobro
		Estado estadoPagado = estadoRepository.findByNombreAndAmbito("PAGADO", "COBRO")
				.orElseThrow(() -> new RuntimeException("Estado PAGADO no encontrado"));
		cobro.setEstadoCobro(estadoPagado);
		Cobro cobroActualizado = cobroRepository.save(cobro);

		detalleCobroService.crearDetalleCobro(cobroActualizado.getIdCobro(), 1L);

		// 2. Actualizar también el turno asociado
		Turno turno = cobroActualizado.getTurno();
		if (turno != null) {
			Estado estadoTurnoPagado = estadoRepository.findByNombreAndAmbito("PAGADO", "TURNO")
					.orElseThrow(() -> new RuntimeException("Estado PAGADO de TURNO no encontrado"));

			Estado anterior = turno.getEstadoActual();
			turno.setEstadoActual(estadoTurnoPagado);

			historialTurnoService.registrarCambio(turno, anterior, estadoTurnoPagado);
			turnoRepository.save(turno);
			try {
				emailQueueService.enviarConDelay(() -> {
			    try {
			        notificacionTurnoService.enviarConfirmacionReserva(turno);
			    } catch (Exception ex) {
			        ex.printStackTrace();
			    }
			});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return Utils.mapCobroEntityToDTO(cobroActualizado);
	}

	@Transactional
	public CobroDTO marcarComoPagadoEfectivoTransferencia(Cobro cobro) {
		// Idempotencia: si ya está marcado, devolver sin cambios o lanzar excepción según prefieras
		if (cobro.getEstadoCobro() != null &&
				"PAGADO EFECTIVO/TRANSFERENCIA".equals(cobro.getEstadoCobro().getNombreEstado())) {
			return Utils.mapCobroEntityToDTO(cobro);
		}

		Estado estadoPagadoEfectivo = estadoRepository
				.findByNombreAndAmbito("PAGADO EFECTIVO/TRANSFERENCIA", "COBRO")
				.orElseThrow(() -> new RuntimeException("Estado PAGADO EFECTIVO/TRANSFERENCIA no encontrado"));

		cobro.setEstadoCobro(estadoPagadoEfectivo);
		Cobro cobroActualizado = cobroRepository.save(cobro);

		// Crear detalle de cobro. Reemplazá 3L por el id correcto para EFECTIVO/TRANSFERENCIA
		detalleCobroService.crearDetalleCobro(cobroActualizado.getIdCobro(), 3L);

		return Utils.mapCobroEntityToDTO(cobroActualizado);
	}

	public CobroConDetallesDTO getCobroConDetalles(Long idCobro) {
		Cobro cobro = cobroRepository.findById(idCobro).orElse(null);
		if (cobro == null) return null;

		List<DetalleCobro> detalles = detalleCobroRepository.findByCobro_IdCobro(idCobro);
		return Utils.mapCobroToConDetallesDTO(cobro, detalles);
	}

}

