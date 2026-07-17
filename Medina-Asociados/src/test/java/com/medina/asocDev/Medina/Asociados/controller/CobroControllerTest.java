package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.CobroConDetallesDTO;
import com.medina.asocDev.Medina.Asociados.dto.CobroDTO;
import com.medina.asocDev.Medina.Asociados.service.CobroService;
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

@WebMvcTest(CobroController.class)
@AutoConfigureMockMvc(addFilters = false)
class CobroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CobroService cobroService;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private JWTUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private TokenBlacklistedRepository tokenBlacklistedRepository;

    private final String BASE_URL = "/api/cobros";

    @Test
    void createCobro_returns200() throws Exception {
        CobroDTO dto = new CobroDTO();
        dto.setIdCobro(1L);
        dto.setImporteTotal(15000F);
        when(cobroService.createCobro(any(CobroDTO.class))).thenReturn(dto);
        when(securityService.hasAnyRole(any(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idTurno\":1,\"importeTotal\":15000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCobro").value(1L))
                .andExpect(jsonPath("$.importeTotal").value(15000));
    }

    @Test
    void createCobro_cuandoNull_returns400() throws Exception {
        when(cobroService.createCobro(any(CobroDTO.class))).thenReturn(null);
        when(securityService.hasAnyRole(any(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idTurno\":1,\"importeTotal\":15000}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCobroPorTurno_returns200() throws Exception {
        CobroDTO dto = new CobroDTO();
        dto.setIdCobro(1L);
        when(cobroService.getCobroPorTurno(1L)).thenReturn(dto);
        when(securityService.hasAnyRole(any(), anyString(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/turno/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCobro").value(1L));
    }

    @Test
    void getCobroPorId_cuandoExiste_returns200() throws Exception {
        CobroDTO dto = new CobroDTO();
        dto.setIdCobro(1L);
        dto.setImporteTotal(15000F);
        when(cobroService.getCobroPorId(1L)).thenReturn(dto);
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCobro").value(1L));
    }

    @Test
    void getCobroPorId_cuandoNoExiste_returns404() throws Exception {
        when(cobroService.getCobroPorId(99L)).thenReturn(null);
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCobro_cuandoExiste_returns200() throws Exception {
        CobroDTO dto = new CobroDTO();
        dto.setIdCobro(1L);
        dto.setImporteTotal(20000F);
        when(cobroService.updateCobro(eq(1L), any(CobroDTO.class))).thenReturn(dto);
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"importeTotal\":20000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.importeTotal").value(20000));
    }

    @Test
    void updateCobro_cuandoNoExiste_returns404() throws Exception {
        when(cobroService.updateCobro(eq(99L), any(CobroDTO.class))).thenReturn(null);
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(put(BASE_URL + "/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"importeTotal\":20000}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCobro_cuandoExiste_returns204() throws Exception {
        when(cobroService.deleteCobro(1L)).thenReturn(true);
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCobro_cuandoNoExiste_returns404() throws Exception {
        when(cobroService.deleteCobro(99L)).thenReturn(false);
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(delete(BASE_URL + "/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCobroConDetalles_cuandoExiste_returns200() throws Exception {
        CobroConDetallesDTO dto = new CobroConDetallesDTO();
        dto.setIdCobro(1L);
        when(cobroService.getCobroConDetalles(1L)).thenReturn(dto);
        when(securityService.hasAnyRole(any(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/1/detalles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCobro").value(1L));
    }

    @Test
    void getCobroConDetalles_cuandoNoExiste_returns404() throws Exception {
        when(cobroService.getCobroConDetalles(99L)).thenReturn(null);
        when(securityService.hasAnyRole(any(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/99/detalles"))
                .andExpect(status().isNotFound());
    }
}
