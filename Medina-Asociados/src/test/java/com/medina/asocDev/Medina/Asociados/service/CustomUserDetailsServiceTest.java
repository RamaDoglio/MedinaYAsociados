package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.CustomUserDetails;
import com.medina.asocDev.Medina.Asociados.entity.Rol;
import com.medina.asocDev.Medina.Asociados.entity.Usuario;
import com.medina.asocDev.Medina.Asociados.repo.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_whenFoundWithRoles_shouldReturnUserDetails() {
        Rol rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombre("ADMIN");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setEmail("admin@test.com");
        usuario.setPassword("pass");
        usuario.getRolesUsuario().add(rol);

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario));

        UserDetails result = customUserDetailsService.loadUserByUsername("admin@test.com");

        assertNotNull(result);
        assertEquals("admin@test.com", result.getUsername());
        assertEquals("pass", result.getPassword());
        assertEquals(1, result.getAuthorities().size());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN")));
    }

    @Test
    void loadUserByUsername_whenFoundWithMultipleRoles_shouldReturnAllRoles() {
        Rol admin = new Rol();
        admin.setNombre("ADMIN");
        Rol cliente = new Rol();
        cliente.setNombre("CLIENTE");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(2L);
        usuario.setEmail("multi@test.com");
        usuario.setPassword("pass");
        usuario.getRolesUsuario().add(admin);
        usuario.getRolesUsuario().add(cliente);

        when(userRepository.findByEmail("multi@test.com")).thenReturn(Optional.of(usuario));

        UserDetails result = customUserDetailsService.loadUserByUsername("multi@test.com");

        assertEquals(2, result.getAuthorities().size());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN")));
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("CLIENTE")));
    }

    @Test
    void loadUserByUsername_whenFoundWithoutRoles_shouldReturnDefaultRole() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3L);
        usuario.setEmail("user@test.com");
        usuario.setPassword("pass");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(usuario));

        UserDetails result = customUserDetailsService.loadUserByUsername("user@test.com");

        assertNotNull(result);
        assertEquals(1, result.getAuthorities().size());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_whenNotFound_shouldThrow() {
        when(userRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("notfound@test.com"));
    }

    @Test
    void loadUserByUsername_shouldReturnCustomUserDetailsInstance() {
        Rol rol = new Rol();
        rol.setNombre("CLIENTE");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(4L);
        usuario.setEmail("cliente@test.com");
        usuario.setPassword("pass");
        usuario.getRolesUsuario().add(rol);

        when(userRepository.findByEmail("cliente@test.com")).thenReturn(Optional.of(usuario));

        UserDetails result = customUserDetailsService.loadUserByUsername("cliente@test.com");

        assertInstanceOf(CustomUserDetails.class, result);
        CustomUserDetails cud = (CustomUserDetails) result;
        assertEquals(4L, cud.getId());
    }

    @Test
    void loadUserByUsername_shouldBeCacheable() {
        Rol rol = new Rol();
        rol.setNombre("ADMIN");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(5L);
        usuario.setEmail("cache@test.com");
        usuario.setPassword("pass");
        usuario.getRolesUsuario().add(rol);

        when(userRepository.findByEmail("cache@test.com")).thenReturn(Optional.of(usuario));

        customUserDetailsService.loadUserByUsername("cache@test.com");
        customUserDetailsService.loadUserByUsername("cache@test.com");

        verify(userRepository, times(2)).findByEmail("cache@test.com");
    }
}
