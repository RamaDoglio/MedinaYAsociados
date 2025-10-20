package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.HistorialTurno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HistorialTurnoRepository extends JpaRepository<HistorialTurno,Long> {

    List<HistorialTurno> findByTurno_IdTurno(Long iDTurno);

    HistorialTurno findByTurno_IdTurnoAndFechaHoraFinIsNull(Long iDTurno);
}
