package com.medina.asocDev.Medina.Asociados.service.interfac;

import com.medina.asocDev.Medina.Asociados.dto.EstadisticaDTO;

import java.util.List;
import java.util.Map;

public interface IEstadisticaService {
    Map<String, List<EstadisticaDTO>> obtenerResumenDashboard();
}
