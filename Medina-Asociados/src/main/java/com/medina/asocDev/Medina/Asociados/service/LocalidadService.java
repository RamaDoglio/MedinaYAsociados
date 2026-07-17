package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.LocalidadDTO;
import com.medina.asocDev.Medina.Asociados.repo.LocalidadRepository;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocalidadService {

    @Autowired
    private LocalidadRepository localidadRepository;

    @Cacheable("catalogos")
    public List<LocalidadDTO> getAllLocalidades() {
        return localidadRepository.findAllLocalidades().stream()
                .map(Utils::mapLocalidadEntityToDTO)
                .collect(Collectors.toList());
    }
}
