package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.TipoCobro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TipoCobroRepository extends JpaRepository<TipoCobro, Long> {

    @Query("SELECT DISTINCT tc.nombreTipoCobro FROM TipoCobro tc")
    List<String> encontrarTiposCobro();
}
