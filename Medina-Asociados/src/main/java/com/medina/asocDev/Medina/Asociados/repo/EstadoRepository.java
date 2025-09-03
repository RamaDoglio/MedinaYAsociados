package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface EstadoRepository extends JpaRepository<Estado, Long>{

    @Query("SELECT e.id FROM Estado e WHERE e.nombreEstado = :nombreEstado AND e.ambito = :ambito")
    Long findIdByNombreAndAmbito(@Param("nombre") String nombreEstado, @Param("ambito") String ambito);


}
