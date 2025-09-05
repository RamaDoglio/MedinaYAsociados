package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.HistorialTurno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HistorialTurnoRepository extends JpaRepository<HistorialTurno,Long> {

    List<HistorialTurno> findByTurno(Long idTurno);

    Optional<HistorialTurno> findByTurnoAndFechaHoraFinIsNull(Long idTurno);
}
