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

    // 🔥 NUEVO: Cliente puede ver sus propios turnos
    public boolean canAccessClienteTurnos(Authentication authentication, Long idCliente) {
        return isOwner(authentication, idCliente) || isAdmin(authentication);
    }

    // 🔥 NUEVO: Abogado puede ver sus propios turnos
    public boolean canAccessAbogadoTurnos(Authentication authentication, Long idAbogado) {
        return isOwner(authentication, idAbogado) || isAdmin(authentication) || isAbogado(authentication);
    }

    // Cliente puede ver su propio detalle; abogados y admins pueden ver cualquier detalle
    public boolean canAccessClienteDetalle(Authentication authentication, Long idCliente) {
        return isAdmin(authentication) || isAbogado(authentication) || isOwner(authentication, idCliente);
    }

    // 🔥 UTIL: Verificar múltiples roles
    public boolean hasAnyRole(Authentication authentication, String... roles) {
        return Arrays.stream(roles).anyMatch(role ->
                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals(role))
        );
    }
}