package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.EspecialidadDTO;
import com.medina.asocDev.Medina.Asociados.service.EspecialidadService;
import com.medina.asocDev.Medina.Asociados.service.SecurityService;
import com.medina.asocDev.Medina.Asociados.repo.TokenBlacklistedRepository;
import com.medina.asocDev.Medina.Asociados.service.CustomUserDetailsService;
import com.medina.asocDev.Medina.Asociados.utils.JWTUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EspecialidadController.class)
@AutoConfigureMockMvc(addFilters = false)
class EspecialidadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EspecialidadService especialidadService;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private JWTUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private TokenBlacklistedRepository tokenBlacklistedRepository;

    private final String BASE_URL = "/api/especialidades";

    @Test
    void getAllEspecialidades_returns200() throws Exception {
        Page<EspecialidadDTO> page = new PageImpl<>(List.of(new EspecialidadDTO()));
        when(especialidadService.getAllEspecialidades(any())).thenReturn(page);
        when(securityService.hasAnyRole(any(), anyString(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk());
    }

    @Test
    void getEspecialidadById_cuandoExiste_returns200() throws Exception {
        EspecialidadDTO dto = new EspecialidadDTO();
        dto.setIdEspecialidad(1L);
        dto.setNombreEspecialidad("Derecho Civil");
        when(especialidadService.getEspecialidadById(1L)).thenReturn(dto);
        when(securityService.hasAnyRole(any(), anyString(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEspecialidad").value(1L))
                .andExpect(jsonPath("$.nombreEspecialidad").value("Derecho Civil"));
    }

    @Test
    void getEspecialidadById_cuandoNoExiste_returns404() throws Exception {
        when(especialidadService.getEspecialidadById(99L)).thenReturn(null);
        when(securityService.hasAnyRole(any(), anyString(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/99"))
                .andExpect(status().isNotFound());
    }
}
