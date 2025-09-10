package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.TipoCobro;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TipoCobroRepository extends JpaRepository<TipoCobro, Long> {

    TipoCobro findByNombreTipoCobro(String nombreTipoCobro);
}
