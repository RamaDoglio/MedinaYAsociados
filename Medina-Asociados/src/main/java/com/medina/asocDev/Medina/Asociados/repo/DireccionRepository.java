package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    Optional<Direccion> findByCalleAndNumeroCalle(String calle, Integer numeroCalle);
}
