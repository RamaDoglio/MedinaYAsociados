package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.TurnoCreateRequest;
import com.medina.asocDev.Medina.Asociados.dto.TurnoDTO;
import com.medina.asocDev.Medina.Asociados.dto.TurnoListadoDTO;
import com.medina.asocDev.Medina.Asociados.dto.TurnoConHistorialDTO;
import com.medina.asocDev.Medina.Asociados.entity.*;
import com.medina.asocDev.Medina.Asociados.repo.TokenBlacklistedRepository;
import com.medina.asocDev.Medina.Asociados.service.CustomUserDetailsService;
import com.medina.asocDev.Medina.Asociados.service.SecurityService;
import com.medina.asocDev.Medina.Asociados.utils.JWTUtils;
import com.medina.asocDev.Medina.Asociados.service.TurnoService;
import com.medina.asocDev.Medina.Asociados.utils.Utils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TurnoController.class)
@AutoConfigureMockMvc(addFilters = false)
class TurnoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TurnoService turnoService;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private JWTUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private TokenBlacklistedRepository tokenBlacklistedRepository;

    private final String BASE_URL = "/api/turnos";

    @Test
    void crearTurno_returns200() throws Exception {
        Usuario abogado = new Usuario();
        abogado.setIdUsuario(1L);
        Usuario cliente = new Usuario();
        cliente.setIdUsuario(2L);
        Cobro cobro = new Cobro();
        cobro.setIdCobro(1L);
        Especialidad especialidad = new Especialidad();
        especialidad.setIdEspecialidad(1L);
        Estado estado = new Estado();
        estado.setIdEstado(1L);

        Turno turno = Turno.builder()
                .idTurno(1L)
                .abogadoTurno(abogado)
                .clienteTurno(cliente)
                .cobro(cobro)
                .especialidad(especialidad)
                .estadoActual(estado)
                .historialTurno(new ArrayList<>())
                .build();

        when(turnoService.crearTurno(any(TurnoCreateRequest.class))).thenReturn(turno);
        when(securityService.isCliente(any())).thenReturn(true);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "idCliente": 1,
                                    "idAbogado": 2,
                                    "idEspecialidad": 1,
                                    "horarioTurno": "2026-07-20T14:00:00"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void pagarTurno_returns200() throws Exception {
        when(turnoService.pagarTurno(1L)).thenReturn("https://mp-pago.test");
        when(securityService.canAccessTurno(any(), eq(1L))).thenReturn(true);

        mockMvc.perform(post(BASE_URL + "/1/pagar"))
                .andExpect(status().isOk())
                .andExpect(content().string("https://mp-pago.test"));
    }

    @Test
    void cancelarTurno_returns200() throws Exception {
        TurnoDTO dto = new TurnoDTO();
        dto.setIdTurno(1L);
        when(turnoService.cancelarTurno(1L)).thenReturn(dto);
        when(securityService.canAccessTurno(any(), eq(1L))).thenReturn(true);

        mockMvc.perform(post(BASE_URL + "/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTurno").value(1L));
    }

    @Test
    void reprogramarTurno_returns200() throws Exception {
        TurnoDTO dto = new TurnoDTO();
        dto.setIdTurno(1L);
        when(turnoService.reprogramarTurno(eq(1L), any(LocalDateTime.class))).thenReturn(dto);
        when(securityService.canAccessTurno(any(), eq(1L))).thenReturn(true);

        mockMvc.perform(put(BASE_URL + "/1/reprogramar?fecha=2026-08-01T14:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTurno").value(1L));
    }

    @Test
    void listarTurnos_returns200() throws Exception {
        Turno turno = new Turno();
        turno.setIdTurno(1L);
        Page<Turno> page = new PageImpl<>(List.of(turno));
        when(turnoService.listarTurnos(any())).thenReturn(page);
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPorId_returns200() throws Exception {
        Turno turno = new Turno();
        turno.setIdTurno(1L);
        when(turnoService.obtenerPorId(1L)).thenReturn(turno);
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk());
    }

    @Test
    void marcarEnCurso_returns200() throws Exception {
        TurnoDTO dto = new TurnoDTO();
        dto.setIdTurno(1L);
        when(turnoService.marcarEnCurso(1L)).thenReturn(dto);
        when(securityService.canAccessTurno(any(), eq(1L))).thenReturn(true);

        mockMvc.perform(post(BASE_URL + "/1/enCurso"))
                .andExpect(status().isOk());
    }

    @Test
    void marcarNoAsistio_returns200() throws Exception {
        TurnoDTO dto = new TurnoDTO();
        dto.setIdTurno(1L);
        when(turnoService.marcarNoAsistio(1L)).thenReturn(dto);
        when(securityService.canAccessTurno(any(), eq(1L))).thenReturn(true);

        mockMvc.perform(post(BASE_URL + "/1/noAsistio"))
                .andExpect(status().isOk());
    }

    @Test
    void finalizarTurno_returns200() throws Exception {
        TurnoDTO dto = new TurnoDTO();
        dto.setIdTurno(1L);
        when(turnoService.finalizarTurno(1L)).thenReturn(dto);
        when(securityService.canAccessTurno(any(), eq(1L))).thenReturn(true);

        mockMvc.perform(post(BASE_URL + "/1/finalizar"))
                .andExpect(status().isOk());
    }

    @Test
    void listarTurnosPorCliente_returns200() throws Exception {
        Page<TurnoListadoDTO> page = new PageImpl<>(List.of(new TurnoListadoDTO()));
        when(turnoService.listarTurnosPorCliente(eq(1L), any())).thenReturn(page);
        when(securityService.canAccessClienteTurnos(any(), eq(1L))).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/cliente/1"))
                .andExpect(status().isOk());
    }

    @Test
    void listarTurnosPorAbogado_returns200() throws Exception {
        Page<TurnoListadoDTO> page = new PageImpl<>(List.of(new TurnoListadoDTO()));
        when(turnoService.listarTurnosPorAbogado(eq(1L), any(), any(), any(), any(), any())).thenReturn(page);
        when(securityService.canAccessAbogadoTurnos(any(), eq(1L))).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/abogado/1"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerTurnoConHistorial_returns200() throws Exception {
        TurnoConHistorialDTO dto = new TurnoConHistorialDTO();
        dto.setIdTurno(1L);
        when(turnoService.getTurnoConHistorial(1L)).thenReturn(dto);
        when(securityService.canAccessTurno(any(), eq(1L))).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/1/historial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTurno").value(1L));
    }

    @Test
    void eliminarTurno_returns204() throws Exception {
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void agregarObservacionesAbogado_returns200() throws Exception {
        TurnoDTO dto = new TurnoDTO();
        dto.setIdTurno(1L);
        when(turnoService.agregarObservacionesAbogado(eq(1L), anyString())).thenReturn(dto);
        when(securityService.canAccessTurno(any(), eq(1L))).thenReturn(true);

        mockMvc.perform(put(BASE_URL + "/1/observaciones-abogado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Observación test\""))
                .andExpect(status().isOk());
    }

    @Test
    void marcarPagado_returns200() throws Exception {
        TurnoDTO dto = new TurnoDTO();
        dto.setIdTurno(1L);
        when(turnoService.marcarPagado(1L)).thenReturn(dto);
        when(securityService.canAccessTurno(any(), anyLong())).thenReturn(true);

        mockMvc.perform(post(BASE_URL + "/1/marcar-pagado"))
                .andExpect(status().isOk());
    }
}
