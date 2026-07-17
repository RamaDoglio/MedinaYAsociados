package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.CustomUserDetails;
import com.medina.asocDev.Medina.Asociados.entity.Turno;
import com.medina.asocDev.Medina.Asociados.entity.Usuario;
import com.medina.asocDev.Medina.Asociados.repo.TurnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock
    private TurnoRepository turnoRepository;

    @InjectMocks
    private SecurityService securityService;

    private Authentication adminAuth;
    private Authentication clienteAuth;
    private Authentication abogadoAuth;
    private Authentication noAuth;

    @BeforeEach
    void setUp() {
        adminAuth = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(1L, "admin", "pass",
                        List.of(new SimpleGrantedAuthority("ADMIN"))),
                null,
                List.of(new SimpleGrantedAuthority("ADMIN"))
        );

        clienteAuth = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(2L, "cliente", "pass",
                        List.of(new SimpleGrantedAuthority("CLIENTE"))),
                null,
                List.of(new SimpleGrantedAuthority("CLIENTE"))
        );

        abogadoAuth = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(3L, "abogado", "pass",
                        List.of(new SimpleGrantedAuthority("ABOGADO"))),
                null,
                List.of(new SimpleGrantedAuthority("ABOGADO"))
        );

        noAuth = new UsernamePasswordAuthenticationToken("unknown", "pass");
    }

    @Test
    void isAdmin_withAdminRole_returnsTrue() {
        assertTrue(securityService.isAdmin(adminAuth));
    }

    @Test
    void isAdmin_withoutAdminRole_returnsFalse() {
        assertFalse(securityService.isAdmin(clienteAuth));
        assertFalse(securityService.isAdmin(abogadoAuth));
    }

    @Test
    void isCliente_withClienteRole_returnsTrue() {
        assertTrue(securityService.isCliente(clienteAuth));
    }

    @Test
    void isCliente_withoutClienteRole_returnsFalse() {
        assertFalse(securityService.isCliente(adminAuth));
        assertFalse(securityService.isCliente(abogadoAuth));
    }

    @Test
    void isAbogado_withAbogadoRole_returnsTrue() {
        assertTrue(securityService.isAbogado(abogadoAuth));
    }

    @Test
    void isAbogado_withoutAbogadoRole_returnsFalse() {
        assertFalse(securityService.isAbogado(adminAuth));
        assertFalse(securityService.isAbogado(clienteAuth));
    }

    @Test
    void isOwner_matchingId_returnsTrue() {
        assertTrue(securityService.isOwner(clienteAuth, 2L));
    }

    @Test
    void isOwner_nonMatchingId_returnsFalse() {
        assertFalse(securityService.isOwner(clienteAuth, 99L));
    }

    @Test
    void isOwner_nullAuthentication_returnsFalse() {
        assertFalse(securityService.isOwner(null, 1L));
    }

    @Test
    void isOwner_notAuthenticated_returnsFalse() {
        assertFalse(securityService.isOwner(noAuth, 1L));
    }

    @Test
    void canAccessClienteTurnos_owner_returnsTrue() {
        assertTrue(securityService.canAccessClienteTurnos(clienteAuth, 2L));
    }

    @Test
    void canAccessClienteTurnos_admin_returnsTrue() {
        assertTrue(securityService.canAccessClienteTurnos(adminAuth, 99L));
    }

    @Test
    void canAccessClienteTurnos_nonOwnerNonAdmin_returnsFalse() {
        assertFalse(securityService.canAccessClienteTurnos(abogadoAuth, 2L));
    }

    @Test
    void canAccessAbogadoTurnos_owner_returnsTrue() {
        assertTrue(securityService.canAccessAbogadoTurnos(abogadoAuth, 3L));
    }

    @Test
    void canAccessAbogadoTurnos_admin_returnsTrue() {
        assertTrue(securityService.canAccessAbogadoTurnos(adminAuth, 99L));
    }

    @Test
    void canAccessAbogadoTurnos_abogado_returnsTrue() {
        assertTrue(securityService.canAccessAbogadoTurnos(abogadoAuth, 99L));
    }

    @Test
    void canAccessAbogadoTurnos_nonOwner_returnsFalse() {
        assertFalse(securityService.canAccessAbogadoTurnos(clienteAuth, 99L));
    }

    @Test
    void canAccessClienteDetalle_admin_returnsTrue() {
        assertTrue(securityService.canAccessClienteDetalle(adminAuth, 1L));
    }

    @Test
    void canAccessClienteDetalle_abogado_returnsTrue() {
        assertTrue(securityService.canAccessClienteDetalle(abogadoAuth, 1L));
    }

    @Test
    void canAccessClienteDetalle_clienteOwner_returnsTrue() {
        assertTrue(securityService.canAccessClienteDetalle(clienteAuth, 2L));
    }

    @Test
    void canAccessClienteDetalle_nonOwner_returnsFalse() {
        assertFalse(securityService.canAccessClienteDetalle(clienteAuth, 99L));
    }

    @Test
    void hasAnyRole_matching_returnsTrue() {
        assertTrue(securityService.hasAnyRole(adminAuth, "ADMIN"));
        assertTrue(securityService.hasAnyRole(adminAuth, "ADMIN", "CLIENTE"));
        assertTrue(securityService.hasAnyRole(clienteAuth, "ADMIN", "CLIENTE"));
    }

    @Test
    void hasAnyRole_nonMatching_returnsFalse() {
        assertFalse(securityService.hasAnyRole(clienteAuth, "ADMIN"));
        assertFalse(securityService.hasAnyRole(clienteAuth, "ADMIN", "ABOGADO"));
    }

    @Test
    void canAccessTurno_admin_returnsTrue() {
        assertTrue(securityService.canAccessTurno(adminAuth, 1L));
        verify(turnoRepository, never()).findById(any());
    }

    @Test
    void canAccessTurno_clienteOwner_returnsTrue() {
        Usuario cliente = new Usuario();
        cliente.setIdUsuario(2L);
        Usuario abogado = new Usuario();
        abogado.setIdUsuario(3L);
        Turno turno = Turno.builder()
                .idTurno(1L)
                .clienteTurno(cliente)
                .abogadoTurno(abogado)
                .build();
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        assertTrue(securityService.canAccessTurno(clienteAuth, 1L));
    }

    @Test
    void canAccessTurno_abogadoOwner_returnsTrue() {
        Usuario cliente = new Usuario();
        cliente.setIdUsuario(2L);
        Usuario abogado = new Usuario();
        abogado.setIdUsuario(3L);
        Turno turno = Turno.builder()
                .idTurno(1L)
                .clienteTurno(cliente)
                .abogadoTurno(abogado)
                .build();
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        assertTrue(securityService.canAccessTurno(abogadoAuth, 1L));
    }

    @Test
    void canAccessTurno_nonOwner_returnsFalse() {
        Usuario cliente = new Usuario();
        cliente.setIdUsuario(99L);
        Usuario abogado = new Usuario();
        abogado.setIdUsuario(98L);
        Turno turno = Turno.builder()
                .idTurno(1L)
                .clienteTurno(cliente)
                .abogadoTurno(abogado)
                .build();
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        assertFalse(securityService.canAccessTurno(clienteAuth, 1L));
    }

    @Test
    void canAccessTurno_nullAuthentication_returnsFalse() {
        assertFalse(securityService.canAccessTurno(null, 1L));
    }

    @Test
    void canAccessTurno_notAuthenticated_returnsFalse() {
        assertFalse(securityService.canAccessTurno(noAuth, 1L));
    }

    @Test
    void canAccessTurno_turnoNotFound_returnsFalse() {
        when(turnoRepository.findById(99L)).thenReturn(Optional.empty());

        assertFalse(securityService.canAccessTurno(clienteAuth, 99L));
    }
}
