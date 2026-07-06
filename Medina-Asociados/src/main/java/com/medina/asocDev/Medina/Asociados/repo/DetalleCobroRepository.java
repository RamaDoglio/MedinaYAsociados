package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.DetalleCobro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface DetalleCobroRepository extends JpaRepository<DetalleCobro, Long> {

    // Obtener todos los detalles de cobro de un turno
    List<DetalleCobro> findByCobro_IdCobro(Long idCobro);
    Page<DetalleCobro> findByCobro_IdCobro(Long idCobro, Pageable pageable);
}
