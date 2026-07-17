package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.LocalidadDTO;
import com.medina.asocDev.Medina.Asociados.service.LocalidadService;
import com.medina.asocDev.Medina.Asociados.repo.TokenBlacklistedRepository;
import com.medina.asocDev.Medina.Asociados.service.CustomUserDetailsService;
import com.medina.asocDev.Medina.Asociados.utils.JWTUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocalidadController.class)
@AutoConfigureMockMvc(addFilters = false)
class LocalidadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocalidadService localidadService;

    @MockBean
    private JWTUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private TokenBlacklistedRepository tokenBlacklistedRepository;

    private final String BASE_URL = "/api/localidades";

    @Test
    void getAllLocalidades_returns200() throws Exception {
        LocalidadDTO dto = new LocalidadDTO();
        dto.setIdLocalidad(1L);
        dto.setNombreLocalidad("Ciudad");
        when(localidadService.getAllLocalidades()).thenReturn(List.of(dto));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].idLocalidad").value(1L));
    }
}
