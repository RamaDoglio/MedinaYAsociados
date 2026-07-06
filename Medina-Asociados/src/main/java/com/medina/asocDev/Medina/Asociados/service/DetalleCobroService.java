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

    public DetalleCobroDTO crearDetalleCobro(Long idCobro,Long tipoDetalle) {
        Cobro cobro = cobroRepository.findById(idCobro)
                .orElseThrow(() -> new RuntimeException("Cobro no encontrado"));

        DetalleCobro detalle = new DetalleCobro();
        detalle.setCobro(cobro);
        detalle.setFecha(LocalDateTime.now());
        detalle.setSubTotal(cobro.getImporteTotal());

        if (tipoDetalle==1L){
            TipoCobro tipoPago = tipoCobroRepository.findByNombreTipoCobro("PAGO");

            detalle.setTipoCobro(tipoPago);

            detalleCobroRepository.save(detalle);
        } else if (tipoDetalle==2L) {
            TipoCobro tipoPago = tipoCobroRepository.findByNombreTipoCobro("REEMBOLSO");


            detalle.setTipoCobro(tipoPago);

            detalleCobroRepository.save(detalle);
        }

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

    public boolean deleteDetalleCobro(Long id) {
        if (detalleCobroRepository.existsById(id)) {
            detalleCobroRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
