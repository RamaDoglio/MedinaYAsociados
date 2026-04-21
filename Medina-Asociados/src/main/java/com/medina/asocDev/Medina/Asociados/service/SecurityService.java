package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service("securityService")
public class SecurityService {

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
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // 🔥 NUEVO: Verificar si es CLIENTE
    public boolean isCliente(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"));
    }

    // 🔥 NUEVO: Verificar si es ABOGADO
    public boolean isAbogado(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ABOGADO"));
    }

    // 🔥 NUEVO: Cliente puede ver sus propios turnos
    public boolean canAccessClienteTurnos(Authentication authentication, Long idCliente) {
        return isOwner(authentication, idCliente) || isAdmin(authentication);
    }

    // 🔥 NUEVO: Abogado puede ver sus propios turnos
    public boolean canAccessAbogadoTurnos(Authentication authentication, Long idAbogado) {
        return isOwner(authentication, idAbogado) || isAdmin(authentication) || isAbogado(authentication);
    }

    // 🔥 NUEVO: Cliente puede ver turnos de su abogado asignado
    public boolean isClienteDelAbogado(Authentication authentication, Long idAbogado) {
        // Lógica adicional si necesitas verificar relación cliente-abogado
        // Por ahora: cliente autenticado + abogado específico
        return isCliente(authentication);
    }

    // 🔥 UTIL: Verificar múltiples roles
    public boolean hasAnyRole(Authentication authentication, String... roles) {
        return Arrays.stream(roles).anyMatch(role ->
                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals(role))
        );
    }
}