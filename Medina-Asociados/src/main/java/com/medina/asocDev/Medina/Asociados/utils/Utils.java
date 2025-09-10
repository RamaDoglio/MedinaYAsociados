package com.medina.asocDev.Medina.Asociados.utils;

import com.medina.asocDev.Medina.Asociados.dto.*;
import com.medina.asocDev.Medina.Asociados.entity.*;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static UsuarioDTO mapUserEntityToUserDTO(Usuario usuario) {
        UsuarioDTO userDTO = new UsuarioDTO();

        userDTO.setIdUsuario(usuario.getIdUsuario());
        userDTO.setNombre(usuario.getNombre());
        userDTO.setEmail(usuario.getEmail());
        userDTO.setTelefono(usuario.getTelefono());

        if (usuario.getRol() != null) {
            userDTO.setRol(mapRolEntityToRolDTO(usuario.getRol()));
        }

        if (usuario.getDireccion() != null) {
            userDTO.setDireccion(mapDireccionEntityToDTO(usuario.getDireccion()));
        }
        return userDTO;
    }

    //Rol -> RolDTO
    public static RolDTO mapRolEntityToRolDTO(Rol rol) {
        if (rol == null) {
            return null;
        }

        RolDTO rolDTO = new RolDTO();
        rolDTO.setIdRol(rol.getIdRol());
        rolDTO.setNombre(rol.getNombre());
        return rolDTO;
    }

    //Direccion -> DireccionDTO
    public static DireccionDTO mapDireccionEntityToDTO(Direccion direccion) {
        if (direccion == null) {
            return null;
        }

        DireccionDTO direccionDTO = new DireccionDTO();
        direccionDTO.setIdDireccion(direccion.getIdDireccion());
        direccionDTO.setCalle(direccion.getCalle());
        direccionDTO.setNumeroCalle(direccion.getNumeroCalle());
        direccionDTO.setLocalidad(mapLocalidadEntityToDTO(direccion.getLocalidad()));
        return direccionDTO;
    }

    public static LocalidadDTO mapLocalidadEntityToDTO(Localidad localidad){
        if (localidad == null){
            return null;
        }

        LocalidadDTO localidadDTO = new LocalidadDTO();
        localidadDTO.setIdLocalidad(localidadDTO.getIdLocalidad());
        localidadDTO.setNombreLocalidad(localidadDTO.getNombreLocalidad());
        localidadDTO.setCodigoPostal(localidadDTO.getCodigoPostal());

        return localidadDTO;
    }

    public static TurnoDTO mapTurnoEntityToDTO(Turno turno){
        TurnoDTO turnoDTO = new TurnoDTO();

        turnoDTO.setIdTurno(turno.getIdTurno());

        return turnoDTO;
    }

    public static UsuarioDTO mapUsuarioEntityToDTOxTurnos(Usuario usuario){
        UsuarioDTO userDTO = new UsuarioDTO();

        userDTO.setIdUsuario(usuario.getIdUsuario());
        userDTO.setNombre(usuario.getNombre());
        userDTO.setEmail(usuario.getEmail());
        userDTO.setTelefono(usuario.getTelefono());

        if (usuario.getRol() != null) {
            userDTO.setRol(mapRolEntityToRolDTO(usuario.getRol()));
        }

        if (usuario.getDireccion() != null) {
            userDTO.setDireccion(mapDireccionEntityToDTO(usuario.getDireccion()));
        }

        if (usuario.getListaTurnos() != null) {
            List<TurnoDTO> turnosDTO = usuario.getListaTurnos()
                    .stream()
                    .map(Utils::mapTurnoEntityToDTO) // 🔹 usamos Utils directamente
                    .toList();
            userDTO.setTurnos(turnosDTO);
        }

        return userDTO;
    }

    private EstadoDTO mapEstadoEntityToDTO(Estado estado){
        EstadoDTO estadoDTO = new EstadoDTO();

        estadoDTO.setIdEstado(estado.getIdEstado());
        estadoDTO.setAmbito(estado.getAmbito());
        estadoDTO.setNombreEstado(estado.getNombreEstado());

        return estadoDTO;
    }

    private TipoCobroDTO mapTipoCobroEntityToDTO(TipoCobro tipoCobro){
        TipoCobroDTO tipoCobroDTO = new TipoCobroDTO();

        tipoCobroDTO.setIdTipoCobro(tipoCobro.getIdTipoCobro());
        tipoCobroDTO.setNombreTipoCobro(tipoCobro.getNombreTipoCobro());
        tipoCobroDTO.setDescripcionTipoCobro(tipoCobroDTO.getDescripcionTipoCobro());

        return tipoCobroDTO;
    }

    private EspecialidadDTO mapEspecialidadEntityToDTO(Especialidad especialidad){
        EspecialidadDTO especialidadDTO = new EspecialidadDTO();

        especialidadDTO.setIdEspecialidad(especialidad.getIdEspecialidad());
        especialidadDTO.setNombreEspecialidad(especialidad.getNombreEspecialidad());
        especialidadDTO.setDescripcionEspecialidad(especialidad.getDescripcionEspecialidad());

        return especialidadDTO;
    }

    private HistorialTurnoDTO mapHistorialTurnoEntityToDTO(HistorialTurno historialTurno){
        HistorialTurnoDTO historialTurnoDTO = new HistorialTurnoDTO();

        historialTurnoDTO.setIdHistorial(historialTurno.getIdHistorial());
        historialTurnoDTO.setEstadoHistorial();
        historialTurnoDTO.setFechaHoraInicio(historialTurno.getFechaHoraInicio());
        historialTurnoDTO.setFechaHoraFin(historialTurno.getFechaHoraFin());
        historialTurnoDTO.setTurno(historialTurno.getTurno());

        return historialTurnoDTO;
    }
}
