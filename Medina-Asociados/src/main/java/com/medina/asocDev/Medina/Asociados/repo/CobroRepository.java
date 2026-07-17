package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.Cobro;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CobroRepository extends JpaRepository<Cobro, Long> {
	List<Cobro> findByTurno_IdTurno(Long turnoId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT c FROM Cobro c WHERE c.idCobro = :id")
	Optional<Cobro> findByIdWithLock(@Param("id") Long id);
}
