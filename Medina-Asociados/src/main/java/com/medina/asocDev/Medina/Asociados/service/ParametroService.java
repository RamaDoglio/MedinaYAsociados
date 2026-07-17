package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.entity.Parametro;
import com.medina.asocDev.Medina.Asociados.repo.ParametroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ParametroService {

    @Autowired
    private ParametroRepository parametroRepository;

    @Cacheable(value = "parametros", key = "#clave")
    public String getValor(String clave) {
        return parametroRepository.findByClave(clave)
                .map(Parametro::getValor)
                .orElseThrow(() -> new RuntimeException("Parámetro no encontrado: " + clave));
    }

    @CacheEvict(value = "parametros", allEntries = true)
    public void setValor(String clave, String valor) {
        Parametro parametro = parametroRepository.findByClave(clave)
                .orElseGet(() -> {
                    Parametro nuevo = new Parametro();
                    nuevo.setClave(clave);
                    return nuevo;
                });
        parametro.setValor(valor);
        parametroRepository.save(parametro);
    }
}