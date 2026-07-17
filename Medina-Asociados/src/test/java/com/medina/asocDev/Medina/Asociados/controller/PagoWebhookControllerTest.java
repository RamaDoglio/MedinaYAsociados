package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.repo.CobroRepository;
import com.medina.asocDev.Medina.Asociados.service.CobroService;
import com.medina.asocDev.Medina.Asociados.repo.TokenBlacklistedRepository;
import com.medina.asocDev.Medina.Asociados.service.CustomUserDetailsService;
import com.medina.asocDev.Medina.Asociados.utils.JWTUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagoWebhookController.class)
@AutoConfigureMockMvc(addFilters = false)
class PagoWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CobroRepository cobroRepository;

    @MockBean
    private CobroService cobroService;

    @MockBean
    private JWTUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private TokenBlacklistedRepository tokenBlacklistedRepository;

    private final String BASE_URL = "/api/pagos";

    @Test
    void redirectPago_redirectsToFrontend() throws Exception {
        mockMvc.perform(get(BASE_URL + "/redirect")
                        .param("turnoId", "1")
                        .param("result", "success"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void recibirNotificacion_tipoNoPayment_returns200() throws Exception {
        mockMvc.perform(post(BASE_URL + "/notificacion")
                        .param("id", "12345")
                        .param("topic", "merchant_order"))
                .andExpect(status().isOk())
                .andExpect(content().string("Tipo ignorado: merchant_order"));
    }
}
