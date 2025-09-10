package com.medina.asocDev.Medina.Asociados.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medina.asocDev.Medina.Asociados.entity.Abogado;


public interface AbogadoRepository extends JpaRepository<Abogado, Long> {
    Optional<Abogado> findByUsuario_IdUsuario(Long idUsuario);
    Optional<Abogado> findByMatricula(String matricula);
    Optional<Abogado> findByEspecialidad(String especialidad);
}
