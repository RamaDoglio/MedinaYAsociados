package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.CustomUserDetails;
import com.medina.asocDev.Medina.Asociados.entity.Turno;
import com.medina.asocDev.Medina.Asociados.repo.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service("securityService")
public class SecurityService {

    @Autowired
    private TurnoRepository turnoRepository;

    public boolean isOwner(Authentication authentication, Long targetId) {
        if (authentication == null || !authentication.isAuthenticated()) return false;

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            Long userId = ((CustomUserDetails) principal).getId();
            return userId.equals(targetId);
        }
        return false;
    }

    public boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }

    public boolean isCliente(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("CLIENTE"));
    }

    public boolean isAbogado(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ABOGADO"));
    }


    public boolean canAccessClienteTurnos(Authentication authentication, Long idCliente) {
        return isOwner(authentication, idCliente) || isAdmin(authentication);
    }


    public boolean canAccessAbogadoTurnos(Authentication authentication, Long idAbogado) {
        return isOwner(authentication, idAbogado) || isAdmin(authentication) || isAbogado(authentication);
    }


    public boolean canAccessClienteDetalle(Authentication authentication, Long idCliente) {
        return isAdmin(authentication) || isAbogado(authentication) || isOwner(authentication, idCliente);
    }


    public boolean hasAnyRole(Authentication authentication, String... roles) {
        return Arrays.stream(roles).anyMatch(role ->
                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals(role))
        );
    }

    public boolean canAccessTurno(Authentication authentication, Long idTurno) {
        if (authentication == null || !authentication.isAuthenticated()) return false;
        if (isAdmin(authentication)) return true;

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) return false;

        Long userId = ((CustomUserDetails) principal).getId();

        Turno turno = turnoRepository.findById(idTurno).orElse(null);
        if (turno == null) return false;

        Long clienteId = turno.getClienteTurno() != null ? turno.getClienteTurno().getIdUsuario() : null;
        Long abogadoId = turno.getAbogadoTurno() != null ? turno.getAbogadoTurno().getIdUsuario() : null;

        return userId.equals(clienteId) || userId.equals(abogadoId);
    }
    
}