package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.DetalleCobroDTO;
import com.medina.asocDev.Medina.Asociados.dto.TipoCobroDTO;
import com.medina.asocDev.Medina.Asociados.entity.DetalleCobro;
import com.medina.asocDev.Medina.Asociados.entity.TipoCobro;
import com.medina.asocDev.Medina.Asociados.entity.Cobro;
import com.medina.asocDev.Medina.Asociados.repo.DetalleCobroRepository;
import com.medina.asocDev.Medina.Asociados.repo.TipoCobroRepository;
import com.medina.asocDev.Medina.Asociados.repo.CobroRepository;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

import static com.medina.asocDev.Medina.Asociados.utils.Utils.mapDetalleCobroEntityToDTO;

@Service
public class DetalleCobroService {

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

        if (dto.getIdCobro() != null) {
            Cobro cobro = cobroRepository.findById(dto.getIdCobro()).orElse(null);
            detalle.setCobro(cobro);
        }

        if (dto.getIdTipoCobro() != null) {
            TipoCobro tipo = tipoCobroRepository.findById(dto.getIdTipoCobro()).orElse(null);
            detalle.setTipoCobro(tipo);
        }

        DetalleCobro guardado = detalleCobroRepository.save(detalle);
        return Utils.mapDetalleCobroEntityToDTO(guardado);
    }

    public List<DetalleCobroDTO> getDetallesPorCobro(Long cobroId) {
        List<DetalleCobro> detalles = detalleCobroRepository.findByCobro_IdCobro(cobroId);
        List<DetalleCobroDTO> dtos = new ArrayList<>();
        for (DetalleCobro d : detalles) {
            dtos.add(Utils.mapDetalleCobroEntityToDTO(d));
        }
        return dtos;
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
