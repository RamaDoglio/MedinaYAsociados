package com.medina.asocDev.Medina.Asociados.utils;

import com.medina.asocDev.Medina.Asociados.dto.*;
import com.medina.asocDev.Medina.Asociados.entity.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    //Usuario(Sin turnos) -> UsuarioDTO
    public static UsuarioDTO mapUserEntityToUserDTO(Usuario usuario) {
        if (usuario == null) return null;
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

    //Localidad -> LocalidadDTO
    public static LocalidadDTO mapLocalidadEntityToDTO(Localidad localidad){
        if (localidad == null){
            return null;
        }

        LocalidadDTO localidadDTO = new LocalidadDTO();
        localidadDTO.setIdLocalidad(localidad.getIdLocalidad());
        localidadDTO.setNombreLocalidad(localidad.getNombreLocalidad());
        localidadDTO.setCodigoPostal(localidad.getCodigoPostal());

        return localidadDTO;
    }

    //Turno -> TurnoDTO
    public static TurnoDTO mapTurnoEntityToDTO(Turno turno){
        if (turno == null) return null;
        TurnoDTO turnoDTO = new TurnoDTO();

        turnoDTO.setIdTurno(turno.getIdTurno());
        turnoDTO.setAbogadoTurno(mapUsuarioEntityToDTOxTurnos(turno.getAbogadoTurno()));
        turnoDTO.setUsuarioTurno(mapUserEntityToUserDTO(turno.getClienteTurno()));
        turnoDTO.setHistorialTurno(mapHistorialTurnoEntityListToDTO(turno.getHistorialTurno()));
        turnoDTO.setCobro(mapCobroEntityToDTO(turno.getCobro()));
        turnoDTO.setEspecialidad(mapEspecialidadEntityToDTO(turno.getEspecialidad()));
        turnoDTO.setObservacionesCliente(turno.getObservacionesCliente());
        turnoDTO.setObservacionesAbogado(turno.getObservacionesAbogado());
        turnoDTO.setHorarioTurno(mapHorarioTurnoEntityToDTO(turno.getHorarioTurno()));
        turnoDTO.setEstadoActual(mapEstadoEntityToDTO(turno.getEstadoActual()));

        return turnoDTO;
    }

    //Usuario con Turno
    public static UsuarioDTO mapUsuarioEntityToDTOxTurnos(Usuario usuario){
        if (usuario == null) return null;
        UsuarioDTO userDTO;

        userDTO = mapUserEntityToUserDTO(usuario);

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

    //Estado -> Estado
    private static EstadoDTO mapEstadoEntityToDTO(Estado estado){
        if (estado == null) return null;
        EstadoDTO estadoDTO = new EstadoDTO();

        estadoDTO.setIdEstado(estado.getIdEstado());
        estadoDTO.setAmbito(estado.getAmbito());
        estadoDTO.setNombreEstado(estado.getNombreEstado());

        return estadoDTO;
    }

    //TipoCobro -> TipoCobroDTO
    private static TipoCobroDTO mapTipoCobroEntityToDTO(TipoCobro tipoCobro){
        if (tipoCobro == null) return null;
        TipoCobroDTO tipoCobroDTO = new TipoCobroDTO();

        tipoCobroDTO.setIdTipoCobro(tipoCobro.getIdTipoCobro());
        tipoCobroDTO.setNombreTipoCobro(tipoCobro.getNombreTipoCobro());
        tipoCobroDTO.setDescripcionTipoCobro(tipoCobro.getDescTipoCobro());

        return tipoCobroDTO;
    }

    //Especialidad -> EspecialidadDTO
    private static EspecialidadDTO mapEspecialidadEntityToDTO(Especialidad especialidad){
        if (especialidad == null) return null;
        EspecialidadDTO especialidadDTO = new EspecialidadDTO();

        especialidadDTO.setIdEspecialidad(especialidad.getIdEspecialidad());
        especialidadDTO.setNombreEspecialidad(especialidad.getNombreEspecialidad());
        especialidadDTO.setDescripcionEspecialidad(especialidad.getDescripcionEspecialidad());

        return especialidadDTO;
    }

    //HistorialTurno -> HistorialTurnoDTO
    private static HistorialTurnoDTO mapHistorialTurnoEntityToDTO(HistorialTurno historialTurno){
        if (historialTurno == null) return null;
        HistorialTurnoDTO historialTurnoDTO = new HistorialTurnoDTO();

        historialTurnoDTO.setIdHistorial(historialTurno.getIdHistorial());
        historialTurnoDTO.setEstadoHistorial(mapEstadoEntityToDTO(historialTurno.getEstadoHistorial()));
        historialTurnoDTO.setFechaHoraInicio(historialTurno.getFechaHoraInicio());
        historialTurnoDTO.setFechaHoraFin(historialTurno.getFechaHoraFin());

        // Evitar recursión infinita
        if (historialTurno.getTurno() != null) {
            TurnoDTO turnoDTO = new TurnoDTO();
            turnoDTO.setIdTurno(historialTurno.getTurno().getIdTurno());
            historialTurnoDTO.setTurno(turnoDTO);
        }

        return historialTurnoDTO;
    }

    private static List<HistorialTurnoDTO> mapHistorialTurnoEntityListToDTO(List<HistorialTurno> historialTurnoList) {
        if (historialTurnoList == null) return new ArrayList<>();

        return historialTurnoList.stream()
                .map(historial -> mapHistorialTurnoEntityToDTO(historial))
                .collect(Collectors.toList());
    }

    //Abogado con Usuario Sin Turnos
    private static AbogadoDTO mapAbogadoEntityToDTOxUsuarioSinTurno(Abogado abogado){
        if (abogado == null) return null;
        AbogadoDTO abogadoDTO = new AbogadoDTO();

        abogadoDTO.setUsuario(mapUserEntityToUserDTO(abogado.getUsuario()));
        abogadoDTO.setIdAbogado(abogado.getIdAbogado());
        abogadoDTO.setMatricula(abogado.getMatricula());

        if (abogado.getEspecialidadesAbogado() != null) {
            List<EspecialidadDTO> especialidadDTO = abogado.getEspecialidadesAbogado()
                    .stream()
                    .map(Utils::mapEspecialidadEntityToDTO)
                    .toList();
            abogadoDTO.setEspecialidadesAbogado(especialidadDTO);
        }

        return abogadoDTO;
    }

    //Abogado con Usuario Con Turnos
    private static AbogadoDTO mapAbogadoEntityToDTOxUsuarioConTurnos(Abogado abogado){
        if (abogado == null) return null;
        AbogadoDTO abogadoDTO;

        abogadoDTO=mapAbogadoEntityToDTOxUsuarioSinTurno(abogado);
        abogadoDTO.setUsuario(mapUsuarioEntityToDTOxTurnos(abogado.getUsuario()));

        return abogadoDTO;
    }

    //HorarioTurno -> HorarioTurnoDTO
    private static HorarioTurnoDTO mapHorarioTurnoEntityToDTO(HorarioTurno horarioTurno) {
        if (horarioTurno == null) {
            return null;
        }

        HorarioTurnoDTO horarioTurnoDTO = new HorarioTurnoDTO();
        horarioTurnoDTO.setIdHorarioTurno(horarioTurno.getIdHorarioTurno());
        horarioTurnoDTO.setFechaHoraInicio(horarioTurno.getFechaHoraInicio());
        horarioTurnoDTO.setEstadoHorario(horarioTurnoDTO.getEstadoHorario());

        // Evitar recursión: solo setear idTurno
        if (horarioTurno.getTurno() != null) {
            TurnoDTO turnoDTO = new TurnoDTO();
            turnoDTO.setIdTurno(horarioTurno.getTurno().getIdTurno());
            horarioTurnoDTO.setTurno(turnoDTO);
        }

        return horarioTurnoDTO;
    }


    //Cobro -> CobroDTO
    private static CobroDTO mapCobroEntityToDTO(Cobro cobro) {
        if (cobro == null) {
            return null;
        }

        CobroDTO cobroDTO = new CobroDTO();
        cobroDTO.setIdCobro(cobro.getIdCobro());
        cobroDTO.setImporteTotal(cobro.getImporteTotal());

        // Evitar recursión: solo setear idTurno
        if (cobro.getTurno() != null) {
            TurnoDTO turnoDTO = new TurnoDTO();
            turnoDTO.setIdTurno(cobro.getTurno().getIdTurno());
            cobroDTO.setTurno(turnoDTO);
        }

        return cobroDTO;
    }


    //DetalleCobro ->DetalleCobroDTO
    private static DetalleCobroDTO mapDetalleCobroEntityToDTO(DetalleCobro detalleCobro){
        if (detalleCobro == null) return null;
        DetalleCobroDTO detalleCobroDTO = new DetalleCobroDTO();

        detalleCobroDTO.setIdDetalleCobro(detalleCobro.getIdDetalleCobro());
        detalleCobroDTO.setIdCobro(mapCobroEntityToDTO(detalleCobro.getCobro()));
        detalleCobroDTO.setDescripcionCobro(detalleCobro.getDescripcionCobro());
        detalleCobroDTO.setTipoCobro(mapTipoCobroEntityToDTO(detalleCobro.getTipoCobro()));
        detalleCobroDTO.setFecha(detalleCobro.getFecha());
        detalleCobroDTO.setSubTotal(detalleCobro.getSubTotal());

        return detalleCobroDTO;
    }

    public static Usuario mapRegistroDTOToEntity(RegisterDTO registerDTO, Rol rol, Direccion direccion) {
        Usuario usuario = new Usuario();
        usuario.setNombre(registerDTO.getNombre());
        usuario.setApellido(registerDTO.getApellido());
        usuario.setDni(registerDTO.getDni());
        usuario.setEmail(registerDTO.getEmail());
        usuario.setTelefono(registerDTO.getTelefono());
        usuario.setPassword(registerDTO.getPassword()); // 🔒 después encriptás antes de persistir
        usuario.setRol(rol);
        usuario.setDireccion(direccion);
        return usuario;
    }
}