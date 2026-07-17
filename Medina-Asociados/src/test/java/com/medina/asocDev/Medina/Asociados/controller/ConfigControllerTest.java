package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.service.ParametroService;
import com.medina.asocDev.Medina.Asociados.service.SecurityService;
import com.medina.asocDev.Medina.Asociados.repo.TokenBlacklistedRepository;
import com.medina.asocDev.Medina.Asociados.service.CustomUserDetailsService;
import com.medina.asocDev.Medina.Asociados.utils.JWTUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConfigController.class)
@AutoConfigureMockMvc(addFilters = false)
class ConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParametroService parametroService;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private JWTUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private TokenBlacklistedRepository tokenBlacklistedRepository;

    private final String BASE_URL = "/api/config";

    @Test
    void getPrecioTurno_returns200() throws Exception {
        when(parametroService.getValor("PRECIO_TURNO")).thenReturn("15000");
        when(securityService.hasAnyRole(any(), anyString(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/precio-turno"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(15000.0));
    }

    @Test
    void updatePrecioTurno_returns200() throws Exception {
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(put(BASE_URL + "/precio-turno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("20000"))
                .andExpect(status().isOk())
                .andExpect(content().string("Precio de turno actualizado a: 20000.0"));
    }
}
