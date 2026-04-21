package com.medina.asocDev.Medina.Asociados.controller;


import com.medina.asocDev.Medina.Asociados.dto.LogInRequest;
import com.medina.asocDev.Medina.Asociados.dto.MensajeResponse;
import com.medina.asocDev.Medina.Asociados.dto.RegisterDTO;
import com.medina.asocDev.Medina.Asociados.dto.Response;
import com.medina.asocDev.Medina.Asociados.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/register")
    public ResponseEntity<MensajeResponse> register(@RequestBody RegisterDTO registerDTO) {
        MensajeResponse response = usuarioService.createUsuario(registerDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LogInRequest loginRequest) {
        Response response = usuarioService.login(loginRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}