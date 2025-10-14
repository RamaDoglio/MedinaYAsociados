package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.DetalleCobro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface DetalleCobroRepository extends JpaRepository<DetalleCobro, Long> {

    // Obtener todos los detalles de cobro de un turno
    List<DetalleCobro> findByIdCobro(Long idCobro);
}
