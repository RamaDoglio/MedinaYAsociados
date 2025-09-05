package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.Localidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocalidadRepository extends JpaRepository<Localidad, Long> {

    @Query("SELECT l FROM Localidad l ORDER BY l.nombreLocalidad ASC")
    List<Localidad> findAllLocalidades();

    // Filtrado dinámico por nombre
    @Query("SELECT l FROM Localidad l WHERE LOWER(l.nombreLocalidad) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY l.nombreLocalidad ASC")
    List<Localidad> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);

    List<Localidad> findByCodigoPostalContaining(String codigoPostal);
}
