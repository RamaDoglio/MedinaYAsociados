package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.entity.TipoCobro;
import com.medina.asocDev.Medina.Asociados.repo.TipoCobroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoCobroService {

    @Autowired
    private TipoCobroRepository tipoCobroRepository;

    @Cacheable("catalogos")
    public List<TipoCobro> listarTodos() {
        return tipoCobroRepository.findAll();
    }

    @Cacheable(value = "catalogos", key = "'tipocobro-' + #id")
    public TipoCobro buscarPorId(Long id) {
        return tipoCobroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TipoCobro no encontrado con id " + id));
    }

    @Cacheable(value = "catalogos", key = "'tipocobro-nombre-' + #nombre")
    public TipoCobro buscarPorNombre(String nombre) {
        return tipoCobroRepository.findByNombreTipoCobro(nombre);
    }

    @CacheEvict(value = "catalogos", allEntries = true)
    public TipoCobro crear(TipoCobro tipoCobro) {
        return tipoCobroRepository.save(tipoCobro);
    }

    @CacheEvict(value = "catalogos", allEntries = true)
    public TipoCobro actualizar(Long id, TipoCobro datos) {
        TipoCobro existente = buscarPorId(id);
        existente.setNombreTipoCobro(datos.getNombreTipoCobro());
        existente.setDescTipoCobro(datos.getDescTipoCobro());
        return tipoCobroRepository.save(existente);
    }

    @CacheEvict(value = "catalogos", allEntries = true)
    public void eliminar(Long id) {
        tipoCobroRepository.deleteById(id);
    }
}

