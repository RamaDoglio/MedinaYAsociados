package com.medina.asocDev.Medina.Asociados.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.medina.asocDev.Medina.Asociados.entity.Abogado;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface AbogadoRepository extends JpaRepository<Abogado, Long> {
    Optional<Abogado> findByUsuario_IdUsuario(Long idUsuario);
    Optional<Abogado> findByMatricula(String matricula);
    @Query("SELECT a FROM Abogado a JOIN a.especialidadesAbogado e WHERE e.nombreEspecialidad = :nombreEspecialidad")
    List<Abogado> findByEspecialidadNombre(@Param("nombreEspecialidad") String nombreEspecialidad);

    List<Abogado> findByEspecialidadesAbogado_IdEspecialidad(Long idEspecialidad);
    Page<Abogado> findByEspecialidadesAbogado_IdEspecialidad(Long idEspecialidad, Pageable pageable);
}
