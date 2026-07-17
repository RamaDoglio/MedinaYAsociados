package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.dto.MensajeResponse;
import com.medina.asocDev.Medina.Asociados.dto.RegisterDTO;
import com.medina.asocDev.Medina.Asociados.dto.Response;
import com.medina.asocDev.Medina.Asociados.dto.UsuarioDTO;
import com.medina.asocDev.Medina.Asociados.service.SecurityService;
import com.medina.asocDev.Medina.Asociados.service.UsuarioService;
import com.medina.asocDev.Medina.Asociados.repo.TokenBlacklistedRepository;
import com.medina.asocDev.Medina.Asociados.service.CustomUserDetailsService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private JWTUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private TokenBlacklistedRepository tokenBlacklistedRepository;

    private final String BASE_URL = "/api/usuarios";

    @Test
    void createUsuario_returns200() throws Exception {
        MensajeResponse response = new MensajeResponse("Usuario creado");
        when(usuarioService.createUsuario(any(RegisterDTO.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Juan\",\"apellido\":\"Perez\",\"dni\":\"12345678\",\"email\":\"juan@test.com\",\"password\":\"pass123\",\"idRol\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario creado"));
    }

    @Test
    void getAllUsers_returns200() throws Exception {
        Page<UsuarioDTO> page = new PageImpl<>(List.of(new UsuarioDTO()));
        when(usuarioService.getAllUsers(any())).thenReturn(page);
        when(securityService.hasAnyRole(any(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_cuandoExiste_returns200() throws Exception {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(1L);
        dto.setNombre("Juan");
        when(usuarioService.getUserByIdInternal(1L)).thenReturn(dto);
        when(securityService.canAccessClienteDetalle(any(), eq(1L))).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    void getUserById_cuandoNoExiste_returns404() throws Exception {
        when(usuarioService.getUserByIdInternal(99L)).thenReturn(null);
        when(securityService.canAccessClienteDetalle(any(), eq(99L))).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_cuandoExiste_returns200() throws Exception {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(1L);
        dto.setNombre("Juan Actualizado");
        when(usuarioService.updateUser(eq(1L), any(UsuarioDTO.class))).thenReturn(dto);
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Juan Actualizado\",\"email\":\"juan@test.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.nombre").value("Juan Actualizado"));
    }

    @Test
    void updateUser_cuandoNoExiste_returns404() throws Exception {
        when(usuarioService.updateUser(eq(99L), any(UsuarioDTO.class))).thenReturn(null);
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(put(BASE_URL + "/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Juan\",\"email\":\"juan@test.com\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarPorDni_returns200() throws Exception {
        Page<UsuarioDTO> page = new PageImpl<>(List.of(new UsuarioDTO()));
        when(usuarioService.buscarPorDni(anyString(), any())).thenReturn(page);
        when(securityService.hasAnyRole(any(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/buscar-por-dni?dni=12345678"))
                .andExpect(status().isOk());
    }

    @Test
    void getClienteDetalle_returns200() throws Exception {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("Detalle del cliente");
        when(usuarioService.getClienteDetalle(1L)).thenReturn(response);
        when(securityService.canAccessClienteDetalle(any(), eq(1L))).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/1/detalle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Detalle del cliente"));
    }

    @Test
    void deleteUser_cuandoExiste_returns204() throws Exception {
        when(usuarioService.deleteUserInternal(1L)).thenReturn(true);
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_cuandoNoExiste_returns404() throws Exception {
        when(usuarioService.deleteUserInternal(99L)).thenReturn(false);
        when(securityService.isAdmin(any())).thenReturn(true);

        mockMvc.perform(delete(BASE_URL + "/99"))
                .andExpect(status().isNotFound());
    }
}
