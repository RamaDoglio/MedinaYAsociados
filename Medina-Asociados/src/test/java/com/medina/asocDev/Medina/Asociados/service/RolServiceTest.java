package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.RolDTO;
import com.medina.asocDev.Medina.Asociados.entity.Rol;
import com.medina.asocDev.Medina.Asociados.entity.Usuario;
import com.medina.asocDev.Medina.Asociados.repo.RolRepository;
import com.medina.asocDev.Medina.Asociados.repo.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private RolService rolService;

    @Test
    void asignarRol_datosValidos_asignaRol() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRolesUsuario(new ArrayList<>());

        Rol rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombre("ABOGADO");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));

        rolService.asignarRol(1L, 1L);

        assertTrue(usuario.getRolesUsuario().contains(rol));
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void asignarRol_usuarioNoExiste_throwsEntityNotFoundException() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> rolService.asignarRol(99L, 1L));
        verify(rolRepository, never()).findById(anyLong());
    }

    @Test
    void asignarRol_rolNoExiste_throwsEntityNotFoundException() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> rolService.asignarRol(1L, 99L));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void asignarRol_yaTieneRol_noDuplica() {
        Rol rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombre("ABOGADO");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRolesUsuario(new ArrayList<>(List.of(rol)));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));

        rolService.asignarRol(1L, 1L);

        assertEquals(1, usuario.getRolesUsuario().size());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void removerRol_datosValidos_remueveRol() {
        Rol rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombre("ABOGADO");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRolesUsuario(new ArrayList<>(List.of(rol)));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));

        rolService.removerRol(1L, 1L);

        assertFalse(usuario.getRolesUsuario().contains(rol));
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void removerRol_usuarioNoExiste_throwsEntityNotFoundException() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> rolService.removerRol(99L, 1L));
    }

    @Test
    void removerRol_rolNoExiste_throwsEntityNotFoundException() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> rolService.removerRol(1L, 99L));
    }

    @Test
    void removerRol_noTieneRol_noHaceNada() {
        Rol rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombre("ABOGADO");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRolesUsuario(new ArrayList<>());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));

        rolService.removerRol(1L, 1L);

        assertTrue(usuario.getRolesUsuario().isEmpty());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void getRolesByUserId_usuarioExiste_returnsRoleNames() {
        Rol rol1 = new Rol();
        rol1.setNombre("ABOGADO");
        Rol rol2 = new Rol();
        rol2.setNombre("CLIENTE");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRolesUsuario(List.of(rol1, rol2));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        List<String> roles = rolService.getRolesByUserId(1L);

        assertEquals(2, roles.size());
        assertTrue(roles.contains("ABOGADO"));
        assertTrue(roles.contains("CLIENTE"));
    }

    @Test
    void getRolesByUserId_usuarioNoExiste_throwsEntityNotFoundException() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> rolService.getRolesByUserId(99L));
    }

    @Test
    void createRol_datosValidos_returnsRolDTO() {
        RolDTO dto = new RolDTO();
        dto.setNombre("ABOGADO");
        dto.setDescripcion("Rol de abogado");

        Rol savedRol = new Rol();
        savedRol.setIdRol(1L);
        savedRol.setNombre("ABOGADO");
        savedRol.setDescripcion("Rol de abogado");

        when(rolRepository.save(any(Rol.class))).thenReturn(savedRol);

        RolDTO resultado = rolService.createRol(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdRol());
        assertEquals("ABOGADO", resultado.getNombre());
        assertEquals("Rol de abogado", resultado.getDescripcion());
        verify(rolRepository).save(any(Rol.class));
    }

    @Test
    void getAllRoles_returnsListOfRolDTO() {
        Rol rol1 = new Rol();
        rol1.setIdRol(1L);
        rol1.setNombre("ABOGADO");
        rol1.setDescripcion("Rol abogado");

        Rol rol2 = new Rol();
        rol2.setIdRol(2L);
        rol2.setNombre("CLIENTE");
        rol2.setDescripcion("Rol cliente");

        when(rolRepository.findAll()).thenReturn(List.of(rol1, rol2));

        List<RolDTO> resultados = rolService.getAllRoles();

        assertEquals(2, resultados.size());
        assertEquals("ABOGADO", resultados.get(0).getNombre());
        assertEquals("CLIENTE", resultados.get(1).getNombre());
    }

    @Test
    void getRolById_idExiste_returnsRolDTO() {
        Rol rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombre("ABOGADO");
        rol.setDescripcion("Rol de abogado");

        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));

        RolDTO resultado = rolService.getRolById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdRol());
        assertEquals("ABOGADO", resultado.getNombre());
    }

    @Test
    void getRolById_idNoExiste_returnsNull() {
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(rolService.getRolById(99L));
    }

    @Test
    void getRolByNombre_nombreExiste_returnsRolDTO() {
        Rol rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombre("ABOGADO");

        when(rolRepository.findByNombre("ABOGADO")).thenReturn(Optional.of(rol));

        RolDTO resultado = rolService.getRolByNombre("ABOGADO");

        assertNotNull(resultado);
        assertEquals("ABOGADO", resultado.getNombre());
    }

    @Test
    void getRolByNombre_nombreNoExiste_returnsNull() {
        when(rolRepository.findByNombre("INEXISTENTE")).thenReturn(Optional.empty());

        assertNull(rolService.getRolByNombre("INEXISTENTE"));
    }

    @Test
    void updateRol_idExiste_updatesAndReturnsDTO() {
        Rol existingRol = new Rol();
        existingRol.setIdRol(1L);
        existingRol.setNombre("ABOGADO");
        existingRol.setDescripcion("Antigua descripcion");

        RolDTO updateDto = new RolDTO();
        updateDto.setNombre("ABOGADO_SENIOR");
        updateDto.setDescripcion("Nueva descripcion");

        Rol updatedRol = new Rol();
        updatedRol.setIdRol(1L);
        updatedRol.setNombre("ABOGADO_SENIOR");
        updatedRol.setDescripcion("Nueva descripcion");

        when(rolRepository.findById(1L)).thenReturn(Optional.of(existingRol));
        when(rolRepository.save(any(Rol.class))).thenReturn(updatedRol);

        RolDTO resultado = rolService.updateRol(1L, updateDto);

        assertNotNull(resultado);
        assertEquals("ABOGADO_SENIOR", resultado.getNombre());
        assertEquals("Nueva descripcion", resultado.getDescripcion());
        verify(rolRepository).save(existingRol);
    }

    @Test
    void updateRol_idNoExiste_returnsNull() {
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(rolService.updateRol(99L, new RolDTO()));
        verify(rolRepository, never()).save(any());
    }

    @Test
    void deleteRol_idExiste_returnsTrue() {
        when(rolRepository.existsById(1L)).thenReturn(true);

        assertTrue(rolService.deleteRol(1L));
        verify(rolRepository).deleteById(1L);
    }

    @Test
    void deleteRol_idNoExiste_returnsFalse() {
        when(rolRepository.existsById(99L)).thenReturn(false);

        assertFalse(rolService.deleteRol(99L));
        verify(rolRepository, never()).deleteById(anyLong());
    }
}
