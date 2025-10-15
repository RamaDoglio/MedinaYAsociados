package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EstadoRepository extends JpaRepository<Estado, Long>{

    @Query("SELECT e.id FROM Estado e WHERE e.nombreEstado = :nombreEstado AND e.ambito = :ambito")
    Long findIdByNombreAndAmbito(@Param("nombreEstado") String nombreEstado, @Param("ambito") String ambito);

    @Query("SELECT e FROM Estado e WHERE e.nombreEstado = :nombre AND e.ambito = :ambito")
    Optional<Estado> findByNombreAndAmbito(@Param("nombre") String nombre, @Param("ambito") String ambito);
}
