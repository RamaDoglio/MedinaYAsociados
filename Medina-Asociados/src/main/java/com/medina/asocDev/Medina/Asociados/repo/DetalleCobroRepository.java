package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.DetalleCobro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface DetalleCobroRepository extends JpaRepository<DetalleCobro, Long> {

    // Obtener todos los detalles de cobro de un turno
    @Query("SELECT dc FROM DetalleCobro dc WHERE dc.cobro.turno.iDTurno = :iDTurno")
    List<DetalleCobro> findByTurnoId(@Param("iDTurno") Long iDTurno);
}
