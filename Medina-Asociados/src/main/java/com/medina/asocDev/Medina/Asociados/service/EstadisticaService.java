package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.EstadisticaDTO;
import com.medina.asocDev.Medina.Asociados.repo.TurnoRepository;
import com.medina.asocDev.Medina.Asociados.service.interfac.IEstadisticaService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EstadisticaService implements IEstadisticaService {

    private final TurnoRepository turnoRepository;

    public EstadisticaService(TurnoRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    @Override
    public Map<String, List<EstadisticaDTO>> obtenerResumenDashboard() {
        Map<String, List<EstadisticaDTO>> mapa = new HashMap<>();

        List<EstadisticaDTO> volumenPorEstado = turnoRepository.getVolumenTurnosPorEstado();
        Map<String, Long> volumenIndexado = volumenPorEstado.stream()
                .collect(Collectors.toMap(EstadisticaDTO::getNombre, EstadisticaDTO::getCantidad, Long::sum));

        List<EstadisticaDTO> resumenEstados = new ArrayList<>();
        resumenEstados.add(new EstadisticaDTO("COMPLETADOS", volumenIndexado.getOrDefault("COMPLETADOS", 0L)));
        resumenEstados.add(new EstadisticaDTO("CANCELADOS", volumenIndexado.getOrDefault("CANCELADOS", 0L)));
        resumenEstados.add(new EstadisticaDTO("INASISTENCIAS", volumenIndexado.getOrDefault("INASISTENCIAS", 0L)));

        mapa.put("porEstado", resumenEstados);
        mapa.put("porEspecialidad", turnoRepository.getTurnosPorEspecialidad());
        return mapa;
    }
}
