package com.medina.asocDev.Medina.Asociados.repo;

import com.medina.asocDev.Medina.Asociados.entity.TokenBlacklisted;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface TokenBlacklistedRepository extends JpaRepository<TokenBlacklisted, Long> {
    Optional<TokenBlacklisted> findByToken(String token);
    void deleteByFechaExpiracionBefore(Date fecha);
}
