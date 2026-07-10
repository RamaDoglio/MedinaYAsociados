package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.HistorialTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HistorialTurnoRepository extends JpaRepository<HistorialTurno,Long> {

    List<HistorialTurno> findByTurno_IdTurno(Long iDTurno);

    List<HistorialTurno> findByTurno_IdTurnoAndFechaHoraFinIsNull(Long iDTurno);

    List<HistorialTurno> findByEstadoHistorial_IdEstadoAndFechaHoraFinIsNull(Long idEstado);
}
