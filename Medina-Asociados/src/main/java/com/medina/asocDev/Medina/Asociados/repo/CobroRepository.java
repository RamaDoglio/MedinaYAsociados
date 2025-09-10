package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.Cobro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CobroRepository extends JpaRepository<Cobro, Long> {
	List<Cobro> findByTurno_IdTurno(Long turnoId);
}
