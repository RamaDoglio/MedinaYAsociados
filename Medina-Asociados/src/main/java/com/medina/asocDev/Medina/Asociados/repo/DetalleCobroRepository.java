package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.DetalleCobro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface DetalleCobroRepository extends JpaRepository<DetalleCobro, Long> {

    // Obtener todos los detalles de cobro de un turno
    @Query("SELECT dc FROM DetalleCobro dc " +
            "JOIN dc.cobro c " +
            "JOIN Turno t ON t.cobro = c " +
            "WHERE t.idTurno = :idTurno")
    List<DetalleCobro> findByTurnoId(@Param("idTurno") Long idTurno);
}
