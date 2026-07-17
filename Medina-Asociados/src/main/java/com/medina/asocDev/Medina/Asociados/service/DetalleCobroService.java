package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.DetalleCobroDTO;
import com.medina.asocDev.Medina.Asociados.entity.DetalleCobro;
import com.medina.asocDev.Medina.Asociados.entity.TipoCobro;
import com.medina.asocDev.Medina.Asociados.entity.Cobro;
import com.medina.asocDev.Medina.Asociados.repo.DetalleCobroRepository;
import com.medina.asocDev.Medina.Asociados.repo.TipoCobroRepository;
import com.medina.asocDev.Medina.Asociados.repo.CobroRepository;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;


@Service
public class DetalleCobroService {

    @Autowired
    private DetalleCobroRepository detalleCobroRepository;
    @Autowired
    private TipoCobroRepository tipoCobroRepository;
    @Autowired
    private CobroRepository cobroRepository;

    @Transactional
	public DetalleCobroDTO crearDetalleCobro(Long idCobro,Long tipoDetalle) {
        Cobro cobro = cobroRepository.findByIdWithLock(idCobro)
                .orElseThrow(() -> new RuntimeException("Cobro no encontrado"));

        String nombreTipo = tipoDetalle == 1L ? "PAGO" : "REEMBOLSO";
        TipoCobro tipoCobro = tipoCobroRepository.findByNombreTipoCobro(nombreTipo);

        // Idempotencia: si ya existe un detalle de este tipo para este cobro, no duplicar
        boolean yaExiste = detalleCobroRepository.findByCobro_IdCobro(idCobro).stream()
                .anyMatch(d -> d.getTipoCobro().getNombreTipoCobro().equals(nombreTipo));
        if (yaExiste) return null;

        DetalleCobro detalle = new DetalleCobro();
        detalle.setCobro(cobro);
        detalle.setFecha(LocalDateTime.now());
        detalle.setSubTotal(cobro.getImporteTotal());
        detalle.setTipoCobro(tipoCobro);

        detalleCobroRepository.save(detalle);

        return Utils.mapDetalleCobroEntityToDTO(detalle);
    }

    public Page<DetalleCobroDTO> getDetallesPorCobro(Long idCobro, Pageable pageable) {
        return detalleCobroRepository.findByCobro_IdCobro(idCobro, pageable)
                .map(Utils::mapDetalleCobroEntityToDTO);
    }

    public DetalleCobroDTO getDetalleCobroById(Long id) {
        return detalleCobroRepository.findById(id)
                .map(Utils::mapDetalleCobroEntityToDTO)
                .orElse(null);
    }

    @Transactional
	public DetalleCobroDTO updateDetalleCobro(Long id, DetalleCobroDTO dto) {
        return detalleCobroRepository.findById(id).map(detalle -> {
            detalle.setFecha(dto.getFecha());
            detalle.setDescripcionCobro(dto.getDescripcionCobro());
            detalle.setSubTotal(dto.getSubTotal());

            if (dto.getIdCobro() != null) {
                Cobro cobro = cobroRepository.findById(dto.getIdCobro()).orElse(null);
                detalle.setCobro(cobro);
            }

            if (dto.getIdTipoCobro() != null) {
                TipoCobro tipo = tipoCobroRepository.findById(dto.getIdTipoCobro()).orElse(null);
                detalle.setTipoCobro(tipo);
            }

            DetalleCobro actualizado = detalleCobroRepository.save(detalle);
            return Utils.mapDetalleCobroEntityToDTO(actualizado);
        }).orElse(null);
    }

    @Transactional
	public boolean deleteDetalleCobro(Long id) {
        if (detalleCobroRepository.existsById(id)) {
            detalleCobroRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
