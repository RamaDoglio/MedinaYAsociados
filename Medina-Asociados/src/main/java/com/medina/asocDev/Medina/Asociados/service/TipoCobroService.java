package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.entity.TipoCobro;
import com.medina.asocDev.Medina.Asociados.repo.TipoCobroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoCobroService {

    @Autowired
    private TipoCobroRepository tipoCobroRepository;

    // Listar todos
    public List<TipoCobro> listarTodos() {
        return tipoCobroRepository.findAll();
    }

    // Buscar por id
    public TipoCobro buscarPorId(Long id) {
        return tipoCobroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TipoCobro no encontrado con id " + id));
    }

    // Buscar por nombre
    public TipoCobro buscarPorNombre(String nombre) {
        return tipoCobroRepository.findByNombreTipoCobro(nombre);
    }

    // Crear
    public TipoCobro crear(TipoCobro tipoCobro) {
        return tipoCobroRepository.save(tipoCobro);
    }

    // Actualizar
    public TipoCobro actualizar(Long id, TipoCobro datos) {
        TipoCobro existente = buscarPorId(id);
        existente.setNombreTipoCobro(datos.getNombreTipoCobro());
        existente.setDescTipoCobro(datos.getDescTipoCobro());
        return tipoCobroRepository.save(existente);
    }

    // Eliminar
    public void eliminar(Long id) {
        tipoCobroRepository.deleteById(id);
    }
}

