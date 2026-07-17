package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.AbogadoDTO;
import com.medina.asocDev.Medina.Asociados.entity.*;
import com.medina.asocDev.Medina.Asociados.repo.*;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbogadoServiceTest {

    @Mock private AbogadoRepository abogadoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private RolRepository rolRepository;
    @Mock private EspecialidadRepository especialidadRepository;
    @Mock private TurnoRepository turnoRepository;

    @InjectMocks private AbogadoService abogadoService;

    private Usuario usuario;
    private Rol rolAbogado;
    private Rol rolCliente;
    private Abogado abogado;
    private Especialidad especialidad;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juan@test.com");
        usuario.setRolesUsuario(new java.util.ArrayList<>());

        rolCliente = new Rol();
        rolCliente.setIdRol(1L);
        rolCliente.setNombre("CLIENTE");

        rolAbogado = new Rol();
        rolAbogado.setIdRol(2L);
        rolAbogado.setNombre("ABOGADO");

        abogado = new Abogado();
        abogado.setIdAbogado(1L);
        abogado.setMatricula("MAT-001");
        abogado.setUsuario(usuario);
        abogado.setEspecialidadesAbogado(new java.util.ArrayList<>());

        especialidad = new Especialidad();
        especialidad.setIdEspecialidad(1L);
        especialidad.setNombreEspecialidad("Penal");
    }

    // ──────────────────────────────────────────────
    // esFinDeSemana
    // ──────────────────────────────────────────────

    @Test
    void esFinDeSemana_sabado_returnsTrue() {
        assertTrue(abogadoService.esFinDeSemana(LocalDate.of(2026, 7, 11))); // sábado
    }

    @Test
    void esFinDeSemana_domingo_returnsTrue() {
        assertTrue(abogadoService.esFinDeSemana(LocalDate.of(2026, 7, 12))); // domingo
    }

    @Test
    void esFinDeSemana_lunes_returnsFalse() {
        assertFalse(abogadoService.esFinDeSemana(LocalDate.of(2026, 7, 13)));
    }

    @Test
    void esFinDeSemana_miercoles_returnsFalse() {
        assertFalse(abogadoService.esFinDeSemana(LocalDate.of(2026, 7, 15)));
    }

    // ──────────────────────────────────────────────
    // verificarDisponibilidad
    // ──────────────────────────────────────────────

    @Test
    void verificarDisponibilidad_finde_returnsFalse() {
        LocalDateTime sabado = LocalDateTime.of(2026, 7, 11, 14, 0);
        assertFalse(abogadoService.verificarDisponibilidad(1L, sabado));
        verify(turnoRepository, never()).findTurnosOcupadosPorAbogadoEnFecha(anyLong(), any());
    }

    @Test
    void verificarDisponibilidad_horarioOcupado_returnsFalse() {
        LocalDate fecha = LocalDate.of(2026, 7, 13);
        LocalDateTime fechaHora = LocalDateTime.of(2026, 7, 13, 14, 0);
        Turno turnoOcupado = new Turno();
        turnoOcupado.setHorarioTurno(fechaHora);

        when(turnoRepository.findTurnosOcupadosPorAbogadoEnFecha(1L, fecha))
                .thenReturn(List.of(turnoOcupado));

        assertFalse(abogadoService.verificarDisponibilidad(1L, fechaHora));
    }

    @Test
    void verificarDisponibilidad_horarioLibre_returnsTrue() {
        LocalDate fecha = LocalDate.of(2026, 7, 13);
        LocalDateTime fechaHora = LocalDateTime.of(2026, 7, 13, 14, 0);

        when(turnoRepository.findTurnosOcupadosPorAbogadoEnFecha(1L, fecha))
                .thenReturn(List.of());

        assertTrue(abogadoService.verificarDisponibilidad(1L, fechaHora));
    }

    // ──────────────────────────────────────────────
    // obtenerHorariosDisponibles
    // ──────────────────────────────────────────────

    @Test
    void obtenerHorariosDisponibles_finde_returnsEmptyList() {
        LocalDate sabado = LocalDate.of(2026, 7, 11);
        assertTrue(abogadoService.obtenerHorariosDisponibles(1L, sabado).isEmpty());
        verify(turnoRepository, never()).findTurnosOcupadosPorAbogadoEnFecha(anyLong(), any());
    }

    @Test
    void obtenerHorariosDisponibles_sinTurnos_returnsAllSlots() {
        LocalDate lunes = LocalDate.of(2026, 7, 13);
        when(turnoRepository.findTurnosOcupadosPorAbogadoEnFecha(1L, lunes))
                .thenReturn(List.of());

        List<LocalTime> disponibles = abogadoService.obtenerHorariosDisponibles(1L, lunes);

        assertEquals(7, disponibles.size());
        assertTrue(disponibles.contains(LocalTime.of(12, 0)));
        assertTrue(disponibles.contains(LocalTime.of(12, 45)));
        assertTrue(disponibles.contains(LocalTime.of(13, 30)));
        assertTrue(disponibles.contains(LocalTime.of(14, 15)));
        assertTrue(disponibles.contains(LocalTime.of(15, 0)));
        assertTrue(disponibles.contains(LocalTime.of(15, 45)));
        assertTrue(disponibles.contains(LocalTime.of(16, 30)));
    }

    @Test
    void obtenerHorariosDisponibles_conTurnoOcupado_excludesThatSlot() {
        LocalDate lunes = LocalDate.of(2026, 7, 13);
        Turno ocupado = new Turno();
        ocupado.setHorarioTurno(LocalDateTime.of(2026, 7, 13, 13, 30));

        when(turnoRepository.findTurnosOcupadosPorAbogadoEnFecha(1L, lunes))
                .thenReturn(List.of(ocupado));

        List<LocalTime> disponibles = abogadoService.obtenerHorariosDisponibles(1L, lunes);

        assertEquals(6, disponibles.size());
        assertFalse(disponibles.contains(LocalTime.of(13, 30)));
        assertTrue(disponibles.contains(LocalTime.of(12, 0)));
        assertTrue(disponibles.contains(LocalTime.of(16, 30)));
    }

    // ──────────────────────────────────────────────
    // createAbogado
    // ──────────────────────────────────────────────

    @Test
    void createAbogado_usuarioExiste_createsAbogado() {
        usuario.getRolesUsuario().add(rolCliente);
        AbogadoDTO dto = new AbogadoDTO();
        dto.setMatricula("MAT-001");
        dto.setEspecialidadesAbogado(List.of(1L));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findByNombre("ABOGADO")).thenReturn(Optional.of(rolAbogado));
        when(especialidadRepository.findById(1L)).thenReturn(Optional.of(especialidad));
        when(abogadoRepository.save(any(Abogado.class))).thenAnswer(i -> i.getArgument(0));

        AbogadoDTO resultado = abogadoService.createAbogado(1L, dto);

        assertNotNull(resultado);
        assertFalse(usuario.getRolesUsuario().contains(rolCliente));
        assertTrue(usuario.getRolesUsuario().contains(rolAbogado));
        verify(usuarioRepository).save(usuario);
        verify(abogadoRepository).save(any(Abogado.class));
    }

    @Test
    void createAbogado_usuarioNoExiste_throwsException() {
        AbogadoDTO dto = new AbogadoDTO();
        dto.setMatricula("MAT-001");

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> abogadoService.createAbogado(99L, dto));
    }

    // ──────────────────────────────────────────────
    // getAbogadoById
    // ──────────────────────────────────────────────

    @Test
    void getAbogadoById_idExiste_returnsDTO() {
        when(abogadoRepository.findById(1L)).thenReturn(Optional.of(abogado));

        AbogadoDTO resultado = abogadoService.getAbogadoById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdAbogado());
        assertEquals("MAT-001", resultado.getMatricula());
    }

    @Test
    void getAbogadoById_idNoExiste_returnsNull() {
        when(abogadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(abogadoService.getAbogadoById(99L));
    }

    // ──────────────────────────────────────────────
    // getAll
    // ──────────────────────────────────────────────

    @Test
    void getAll_returnsPagedDTOs() {
        Pageable pageable = PageRequest.of(0, 10);
        when(abogadoRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(abogado)));

        Page<AbogadoDTO> resultado = abogadoService.getAll(pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("MAT-001", resultado.getContent().get(0).getMatricula());
    }

    // ──────────────────────────────────────────────
    // deleteAbogado
    // ──────────────────────────────────────────────

    @Test
    void deleteAbogado_idExiste_returnsTrue() {
        when(abogadoRepository.existsById(1L)).thenReturn(true);

        assertTrue(abogadoService.deleteAbogado(1L));
        verify(abogadoRepository).deleteById(1L);
    }

    @Test
    void deleteAbogado_idNoExiste_returnsFalse() {
        when(abogadoRepository.existsById(99L)).thenReturn(false);

        assertFalse(abogadoService.deleteAbogado(99L));
        verify(abogadoRepository, never()).deleteById(anyLong());
    }

    // ──────────────────────────────────────────────
    // updateMatricula
    // ──────────────────────────────────────────────

    @Test
    void updateMatricula_idExiste_updatesAndReturnsDTO() {
        when(abogadoRepository.findById(1L)).thenReturn(Optional.of(abogado));
        when(abogadoRepository.save(any(Abogado.class))).thenAnswer(i -> i.getArgument(0));

        AbogadoDTO resultado = abogadoService.updateMatricula(1L, "NUEVA-MAT");

        assertNotNull(resultado);
        assertEquals("NUEVA-MAT", resultado.getMatricula());
        assertEquals("NUEVA-MAT", abogado.getMatricula());
    }

    @Test
    void updateMatricula_idNoExiste_returnsNull() {
        when(abogadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(abogadoService.updateMatricula(99L, "NUEVA-MAT"));
    }

    // ──────────────────────────────────────────────
    // updateEspecialidades
    // ──────────────────────────────────────────────

    @Test
    void updateEspecialidades_idExiste_updatesAndReturnsDTO() {
        when(abogadoRepository.findById(1L)).thenReturn(Optional.of(abogado));
        when(especialidadRepository.findById(1L)).thenReturn(Optional.of(especialidad));
        when(abogadoRepository.save(any(Abogado.class))).thenAnswer(i -> i.getArgument(0));

        AbogadoDTO resultado = abogadoService.updateEspecialidades(1L, List.of(1L));

        assertNotNull(resultado);
        assertEquals(1, abogado.getEspecialidadesAbogado().size());
        assertEquals("Penal", abogado.getEspecialidadesAbogado().get(0).getNombreEspecialidad());
    }

    @Test
    void updateEspecialidades_idNoExiste_returnsNull() {
        when(abogadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(abogadoService.updateEspecialidades(99L, List.of(1L)));
    }

    // ──────────────────────────────────────────────
    // getAbogadosByEspecialidad
    // ──────────────────────────────────────────────

    @Test
    void getAbogadosByEspecialidad_returnsFilteredPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(abogadoRepository.findByEspecialidadesAbogado_IdEspecialidad(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(abogado)));

        Page<AbogadoDTO> resultado = abogadoService.getAbogadosByEspecialidad(1L, pageable);

        assertEquals(1, resultado.getTotalElements());
    }
}
