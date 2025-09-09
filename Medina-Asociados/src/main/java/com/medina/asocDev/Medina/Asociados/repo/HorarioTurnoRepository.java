package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.HorarioTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface HorarioTurnoRepository extends JpaRepository<HorarioTurno, Long> {

    // Buscar los horarios de un abogado en un día específico que estén ocupados
    @Query("SELECT h FROM HorarioTurno h JOIN h.turno t " +
            "WHERE t.abogadoTurno.id = :idAbogado " +
            "AND DATE(h.fechaHoraInicio) = :fecha " +
            "AND t.estadoActual.nombreEstado <> 'CANCELADO'")
    List<HorarioTurno> findHorariosOcupadosPorAbogadoEnFecha(
            @Param("idAbogado") Long idAbogado,
            @Param("fecha") LocalDateTime fecha);
}