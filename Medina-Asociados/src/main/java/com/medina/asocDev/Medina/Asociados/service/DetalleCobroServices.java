package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.DetalleCobroDTO;
import com.medina.asocDev.Medina.Asociados.dto.TipoCobroDTO;
import com.medina.asocDev.Medina.Asociados.entity.DetalleCobro;
import com.medina.asocDev.Medina.Asociados.entity.TipoCobro;
import com.medina.asocDev.Medina.Asociados.entity.Cobro;
import com.medina.asocDev.Medina.Asociados.repo.DetalleCobroRepository;
import com.medina.asocDev.Medina.Asociados.repo.TipoCobroRepository;
import com.medina.asocDev.Medina.Asociados.repo.CobroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class DetalleCobroServices {

    @Autowired
    private DetalleCobroRepository detalleCobroRepository;
    @Autowired
    private TipoCobroRepository tipoCobroRepository;
    @Autowired
    private CobroRepository cobroRepository;

    public DetalleCobroDTO createDetalleCobro(DetalleCobroDTO dto) {
        DetalleCobro detalle = new DetalleCobro();
        detalle.setFecha(dto.getFecha());
        detalle.setDescripcionCobro(dto.getDescripcionCobro());
        detalle.setSubTotal(dto.getSubTotal());
        if (dto.getTipoCobro() != null) {
            TipoCobro tipo = tipoCobroRepository.findByNombreTipoCobro(dto.getTipoCobro().getNombreTipoCobro());
            detalle.setTipoCobro(tipo);
        }
        if (dto.getIdCobro() != null) {
            Cobro cobro = cobroRepository.findById(dto.getIdCobro()).orElse(null);
            detalle.setCobro(cobro);
        }
        DetalleCobro guardado = detalleCobroRepository.save(detalle);
        return mapToDTO(guardado);
    }

    public List<DetalleCobroDTO> getDetallesPorCobro(Long cobroId) {
        List<DetalleCobro> detalles = detalleCobroRepository.findByCobro_Turno_IdTurno(cobroId);
        List<DetalleCobroDTO> dtos = new ArrayList<>();
        for (DetalleCobro d : detalles) {
            dtos.add(mapToDTO(d));
        }
        return dtos;
    }

    public DetalleCobroDTO getDetalleCobroById(Long id) {
        return detalleCobroRepository.findById(id).map(this::mapToDTO).orElse(null);
    }

    public DetalleCobroDTO updateDetalleCobro(Long id, DetalleCobroDTO dto) {
        return detalleCobroRepository.findById(id).map(detalle -> {
            detalle.setFecha(dto.getFecha());
            detalle.setDescripcionCobro(dto.getDescripcionCobro());
            detalle.setSubTotal(dto.getSubTotal());
            if (dto.getTipoCobro() != null) {
                TipoCobro tipo = tipoCobroRepository.findByNombreTipoCobro(dto.getTipoCobro().getNombreTipoCobro());
                detalle.setTipoCobro(tipo);
            }
            DetalleCobro actualizado = detalleCobroRepository.save(detalle);
            return mapToDTO(actualizado);
        }).orElse(null);
    }

    public boolean deleteDetalleCobro(Long id) {
        if (detalleCobroRepository.existsById(id)) {
            detalleCobroRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private DetalleCobroDTO mapToDTO(DetalleCobro detalle) {
        DetalleCobroDTO dto = new DetalleCobroDTO();
        dto.setIdDetalleCobro(detalle.getIdDetalleCobro());
        dto.setFecha(detalle.getFecha());
        dto.setDescripcionCobro(detalle.getDescripcionCobro());
        dto.setSubTotal(detalle.getSubTotal());
        if (detalle.getTipoCobro() != null) {
            TipoCobroDTO tipoDTO = new TipoCobroDTO();
            tipoDTO.setNombreTipoCobro(detalle.getTipoCobro().getNombreTipoCobro());
            tipoDTO.setDescripcionTipoCobro(detalle.getTipoCobro().getDescTipoCobro());
            dto.setTipoCobro(tipoDTO);
        }
        return dto;
    }
}
