package com.medina.asocDev.Medina.Asociados.service.interfac;

import com.medina.asocDev.Medina.Asociados.dto.LogInRequest;
import com.medina.asocDev.Medina.Asociados.dto.MensajeResponse;
import com.medina.asocDev.Medina.Asociados.dto.Response;
import com.medina.asocDev.Medina.Asociados.entity.Usuario;

public interface IUserService {

    Response register(Usuario user);

    Response login(LogInRequest loginRequest);

    Response getAllUsers();

    Response getUserTurnoHistory(String userId);

    Response deleteUser(String userId);

    Response getUserById(String userId);

    Response getMyInfo(String email);
}