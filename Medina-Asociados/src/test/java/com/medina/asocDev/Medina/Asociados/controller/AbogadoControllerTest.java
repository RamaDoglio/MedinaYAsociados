package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.AbogadoDTO;
import com.medina.asocDev.Medina.Asociados.dto.AbogadoEspecialidadesDTO;
import com.medina.asocDev.Medina.Asociados.dto.AbogadoMatriculaDTO;
import com.medina.asocDev.Medina.Asociados.service.AbogadoService;
import com.medina.asocDev.Medina.Asociados.repo.TokenBlacklistedRepository;
import com.medina.asocDev.Medina.Asociados.service.CustomUserDetailsService;
import com.medina.asocDev.Medina.Asociados.service.SecurityService;
import com.medina.asocDev.Medina.Asociados.utils.JWTUtils;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AbogadoController.class)
@AutoConfigureMockMvc(addFilters = false)
class AbogadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AbogadoService abogadoService;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private JWTUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private TokenBlacklistedRepository tokenBlacklistedRepository;

    private final String BASE_URL = "/api/abogados";

    @Test
    void getAll_returns200() throws Exception {
        Page<AbogadoDTO> page = new PageImpl<>(List.of(new AbogadoDTO()));
        when(abogadoService.getAll(any())).thenReturn(page);
        when(securityService.hasAnyRole(any(), anyString(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk());
    }

    @Test
    void getById_cuandoExiste_returns200() throws Exception {
        AbogadoDTO dto = new AbogadoDTO();
        dto.setIdAbogado(1L);
        dto.setMatricula("MAT-001");
        when(abogadoService.getAbogadoById(1L)).thenReturn(dto);

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAbogado").value(1L))
                .andExpect(jsonPath("$.matricula").value("MAT-001"));
    }

    @Test
    void getById_cuandoNoExiste_returns404() throws Exception {
        when(abogadoService.getAbogadoById(99L)).thenReturn(null);

        mockMvc.perform(get(BASE_URL + "/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAbogado_returns200() throws Exception {
        AbogadoDTO dto = new AbogadoDTO();
        dto.setMatricula("MAT-001");
        when(abogadoService.createAbogado(anyLong(), any(AbogadoDTO.class))).thenReturn(dto);
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(post(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"matricula\":\"MAT-001\",\"especialidadesAbogado\":[]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value("MAT-001"));
    }

    @Test
    void updateMatricula_returns200() throws Exception {
        AbogadoDTO dto = new AbogadoDTO();
        dto.setIdAbogado(1L);
        AbogadoMatriculaDTO matriculaDTO = new AbogadoMatriculaDTO();
        matriculaDTO.setMatricula("NUEVA-MAT");
        when(abogadoService.updateMatricula(eq(1L), anyString())).thenReturn(dto);
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(patch(BASE_URL + "/1/matricula")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"matricula\":\"NUEVA-MAT\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateMatricula_cuandoNoExiste_returns404() throws Exception {
        when(abogadoService.updateMatricula(eq(99L), anyString())).thenReturn(null);
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(patch(BASE_URL + "/99/matricula")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"matricula\":\"NUEVA-MAT\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateEspecialidades_returns200() throws Exception {
        AbogadoDTO dto = new AbogadoDTO();
        dto.setIdAbogado(1L);
        when(abogadoService.updateEspecialidades(eq(1L), anyList())).thenReturn(dto);
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(put(BASE_URL + "/1/especialidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"especialidadesAbogado\":[1,2]}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAbogado_cuandoExiste_returns204() throws Exception {
        when(abogadoService.deleteAbogado(1L)).thenReturn(true);
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAbogado_cuandoNoExiste_returns404() throws Exception {
        when(abogadoService.deleteAbogado(99L)).thenReturn(false);
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(delete(BASE_URL + "/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAbogadosByEspecialidad_returns200() throws Exception {
        Page<AbogadoDTO> page = new PageImpl<>(List.of(new AbogadoDTO()));
        when(abogadoService.getAbogadosByEspecialidad(eq(1L), any())).thenReturn(page);
        when(securityService.hasAnyRole(any(), anyString(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/especialidad/1"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerHorariosDisponibles_returns200() throws Exception {
        when(abogadoService.obtenerHorariosDisponibles(eq(1L), any(LocalDate.class)))
                .thenReturn(List.of(LocalTime.of(12, 0), LocalTime.of(12, 45)));
        when(securityService.hasAnyRole(any(), anyString(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/1/horarios-disponibles?fecha=2026-07-13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void verificarDisponibilidad_returns200() throws Exception {
        when(abogadoService.verificarDisponibilidad(eq(1L), any())).thenReturn(true);
        when(securityService.hasAnyRole(any(), anyString(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/1/disponibilidad?fechaHora=2026-07-13T14:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}
