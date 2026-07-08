package com.medina.asocDev.Medina.Asociados.utils;

import com.medina.asocDev.Medina.Asociados.dto.*;
import com.medina.asocDev.Medina.Asociados.entity.*;
import java.security.SecureRandom;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    private static final String ALPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateRandomConfirmationCode(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(ALPHANUMERIC_STRING.length());
            char randomChar = ALPHANUMERIC_STRING.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }


    public static void validarDiaHabil(LocalDateTime fechaHoraTurno) {
        DayOfWeek dia = fechaHoraTurno.getDayOfWeek();
        if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) {
            throw new RuntimeException("No se pueden reservar turnos los fines de semana");
        }
    }
    //Usuario(Sin turnos) -> UsuarioDTO
    public static UsuarioDTO mapUserEntityToUserDTO(Usuario usuario) {
        if (usuario == null) return null;

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setIdUsuario(usuario.getIdUsuario());
        usuarioDTO.setNombre(usuario.getNombre());
        usuarioDTO.setApellido(usuario.getApellido());
        usuarioDTO.setDni(usuario.getDni());
        usuarioDTO.setTelefono(usuario.getTelefono());
        usuarioDTO.setEmail(usuario.getEmail());
        usuarioDTO.setPassword(usuario.getPassword());

        // 🔥 CAMBIO: Obtener rol de la lista (primer rol o null)
        if (usuario.getRolesUsuario() != null && !usuario.getRolesUsuario().isEmpty()) {
            usuarioDTO.setIdRol(usuario.getRolesUsuario().get(0).getIdRol());
        }

        if (usuario.getDireccion() != null) {
            usuarioDTO.setIdDireccion(usuario.getDireccion().getIdDireccion());
        }

        return usuarioDTO;
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
        direccionDTO.setLocalidad(direccion.getLocalidad().getIdLocalidad());
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
        turnoDTO.setAbogadoTurno(turno.getAbogadoTurno().getIdUsuario());
        turnoDTO.setUsuarioTurno(turno.getClienteTurno().getIdUsuario());
        turnoDTO.setHistorialTurno(
                turno.getHistorialTurno().stream()
                        .map(HistorialTurno::getIdHistorial)
                        .collect(Collectors.toList())
        );
        turnoDTO.setIdCobro(turno.getCobro().getIdCobro());
        turnoDTO.setIdEspecialidad(turno.getEspecialidad().getIdEspecialidad());
        turnoDTO.setObservacionesCliente(turno.getObservacionesCliente());
        turnoDTO.setObservacionesAbogado(turno.getObservacionesAbogado());
        turnoDTO.setHorarioTurno(turno.getHorarioTurno());
        turnoDTO.setIdEstado(turno.getEstadoActual().getIdEstado());

        return turnoDTO;
    }

    //Usuario con Turno
    public static UsuarioDTO mapUsuarioEntityToDTOxTurnos(Usuario usuario) {
        if (usuario == null) return null;
        UsuarioDTO userDTO;

        userDTO = mapUserEntityToUserDTO(usuario); // Ya incluye el cambio anterior

        // 🔥 CAMBIO: Obtener rol de la lista (el resto igual)
        if (usuario.getRolesUsuario() != null && !usuario.getRolesUsuario().isEmpty()) {
            userDTO.setIdRol(usuario.getRolesUsuario().get(0).getIdRol());
        }

        if (usuario.getDireccion() != null) {
            userDTO.setIdDireccion(usuario.getDireccion().getIdDireccion());
        }

        if (usuario.getListaTurnos() != null) {
            userDTO.setIdTurnos(usuario.getListaTurnos().stream()
                    .map(Turno::getIdTurno)
                    .collect(Collectors.toList()));
        }

        return userDTO;
    }

    //Estado -> Estado
    public static EstadoDTO mapEstadoEntityToDTO(Estado estado){
        if (estado == null) return null;
        EstadoDTO estadoDTO = new EstadoDTO();

        estadoDTO.setIdEstado(estado.getIdEstado());
        estadoDTO.setAmbito(estado.getAmbito());
        estadoDTO.setNombreEstado(estado.getNombreEstado());

        return estadoDTO;
    }

    //TipoCobro -> TipoCobroDTO
    public static TipoCobroDTO mapTipoCobroEntityToDTO(TipoCobro tipoCobro){
        if (tipoCobro == null) return null;
        TipoCobroDTO tipoCobroDTO = new TipoCobroDTO();

        tipoCobroDTO.setIdTipoCobro(tipoCobro.getIdTipoCobro());
        tipoCobroDTO.setNombreTipoCobro(tipoCobro.getNombreTipoCobro());
        tipoCobroDTO.setDescripcionTipoCobro(tipoCobro.getDescTipoCobro());

        return tipoCobroDTO;
    }

    //Especialidad -> EspecialidadDTO
    public static EspecialidadDTO mapEspecialidadEntityToDTO(Especialidad especialidad){
        if (especialidad == null) return null;
        EspecialidadDTO especialidadDTO = new EspecialidadDTO();

        especialidadDTO.setIdEspecialidad(especialidad.getIdEspecialidad());
        especialidadDTO.setNombreEspecialidad(especialidad.getNombreEspecialidad());
        especialidadDTO.setDescripcionEspecialidad(especialidad.getDescripcionEspecialidad());

        return especialidadDTO;
    }

    //HistorialTurno -> HistorialTurnoDTO
    public static HistorialTurnoDTO mapHistorialTurnoEntityToDTO(HistorialTurno historialTurno){
        if (historialTurno == null) return null;
        HistorialTurnoDTO historialTurnoDTO = new HistorialTurnoDTO();

        historialTurnoDTO.setIdHistorial(historialTurno.getIdHistorial());
        historialTurnoDTO.setIdEstado(historialTurno.getEstadoHistorial().getIdEstado());
        historialTurnoDTO.setFechaHoraInicio(historialTurno.getFechaHoraInicio());
        historialTurnoDTO.setFechaHoraFin(historialTurno.getFechaHoraFin());

        if (historialTurno.getTurno() != null) {
            historialTurnoDTO.setIdTurno(historialTurno.getTurno().getIdTurno()); // ✅ solo el id
        }

        return historialTurnoDTO;
    }

    public static List<HistorialTurnoDTO> mapHistorialTurnoEntityListToDTO(List<HistorialTurno> historialTurnoList) {
        if (historialTurnoList == null) return new ArrayList<>();

        return historialTurnoList.stream()
                .map(historial -> mapHistorialTurnoEntityToDTO(historial))
                .collect(Collectors.toList());
    }

    //Abogado con Usuario Sin Turnos
    public static AbogadoDTO mapAbogadoEntityToDTOxUsuarioSinTurno(Abogado abogado){
        if (abogado == null) return null;
        AbogadoDTO abogadoDTO = new AbogadoDTO();

        abogadoDTO.setIdUsuario(abogado.getUsuario().getIdUsuario());
        abogadoDTO.setIdAbogado(abogado.getIdAbogado());
        abogadoDTO.setMatricula(abogado.getMatricula());

        if (abogado.getEspecialidadesAbogado() != null) {
            List<Long> especialidadesIds = abogado.getEspecialidadesAbogado()
                    .stream()
                    .map(Especialidad::getIdEspecialidad) // ✅ solo el ID
                    .toList();
            abogadoDTO.setEspecialidadesAbogado(especialidadesIds);
        }

        return abogadoDTO;
    }

    //Abogado con Usuario Con Turnos
    public static AbogadoConTurnosDTO mapAbogadoEntityToDTOConTurnos(Abogado abogado) {
        if (abogado == null) return null;

        AbogadoConTurnosDTO dto = new AbogadoConTurnosDTO();
        dto.setIdAbogado(abogado.getIdAbogado());
        dto.setMatricula(abogado.getMatricula());

        if (abogado.getUsuario() != null) {
            dto.setUsuario(mapUsuarioEntityToDTOxTurnos(abogado.getUsuario())); // ✅ con turnos
        }

        return dto;
    }


    //Cobro -> CobroDTO
    public static CobroDTO mapCobroEntityToDTO(Cobro cobro) {
        if (cobro == null) {
            return null;
        }

        CobroDTO cobroDTO = new CobroDTO();
        cobroDTO.setIdCobro(cobro.getIdCobro());
        cobroDTO.setImporteTotal(cobro.getImporteTotal());

        // Evitar recursión: solo setear idTurno
        if (cobro.getTurno() != null) {
            cobroDTO.setIdTurno(cobro.getTurno().getIdTurno()); // ✅ solo el id
        }

        return cobroDTO;
    }


    //DetalleCobro ->DetalleCobroDTO
    public static DetalleCobroDTO mapDetalleCobroEntityToDTO(DetalleCobro detalleCobro){
        if (detalleCobro == null) return null;
        DetalleCobroDTO detalleCobroDTO = new DetalleCobroDTO();

        detalleCobroDTO.setIdDetalleCobro(detalleCobro.getIdDetalleCobro());
        detalleCobroDTO.setIdCobro(detalleCobro.getCobro().getIdCobro());
        detalleCobroDTO.setDescripcionCobro(detalleCobro.getDescripcionCobro());
        detalleCobroDTO.setIdTipoCobro(detalleCobro.getTipoCobro().getIdTipoCobro());
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
        usuario.setPassword(registerDTO.getPassword());

        // 🔥 CAMBIO: Agregar rol a la lista
        if (rol != null) {
            usuario.getRolesUsuario().add(rol);
        }

        usuario.setDireccion(direccion);
        return usuario;
    }

    public static Direccion mapDireccionDTOToEntity(DireccionDTO direccionDTO) {
        if (direccionDTO == null) return null;

        Direccion direccion = new Direccion();
        direccion.setIdDireccion(direccionDTO.getIdDireccion());
        direccion.setCalle(direccionDTO.getCalle());
        direccion.setNumeroCalle(direccionDTO.getNumeroCalle());

        if (direccionDTO.getLocalidad() != null) {
            Localidad localidad = new Localidad();
            localidad.setIdLocalidad(direccionDTO.getLocalidad()); // ✅ solo id
            direccion.setLocalidad(localidad);
        }

        return direccion;
    }

    public static Localidad mapLocalidadDTOToEntity(LocalidadDTO localidadDTO) {
        if (localidadDTO == null) return null;

        Localidad localidad = new Localidad();
        localidad.setIdLocalidad(localidadDTO.getIdLocalidad());
        localidad.setNombreLocalidad(localidadDTO.getNombreLocalidad());
        localidad.setCodigoPostal(localidadDTO.getCodigoPostal());

        return localidad;
    }

    public static Usuario mapUsuarioDTOToEntity(UsuarioDTO usuarioDTO, Rol rol, Direccion direccion) {
        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());
        usuario.setDni(usuarioDTO.getDni());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setPassword(usuarioDTO.getPassword());

        // 🔥 CAMBIO: Agregar rol a la lista
        if (rol != null) {
            usuario.getRolesUsuario().add(rol);
        }

        usuario.setDireccion(direccion);
        return usuario;
    }

    public static Abogado mapAbogadoDTOToEntity(AbogadoDTO abogadoDTO, Usuario usuario, List<Especialidad> especialidades) {
        if (abogadoDTO == null) return null;

        Abogado abogado = new Abogado();
        abogado.setIdAbogado(abogadoDTO.getIdAbogado());
        abogado.setMatricula(abogadoDTO.getMatricula());
        abogado.setUsuario(usuario);
        abogado.setEspecialidadesAbogado(especialidades);

        return abogado;
    }

    public static Cobro mapDTOToCobro(CobroDTO dto) {
        Cobro cobro = new Cobro();
        cobro.setIdCobro(dto.getIdCobro());
        cobro.setImporteTotal(dto.getImporteTotal());

        if (dto.getIdEstado() != null) {
            Estado estado = new Estado();
            estado.setIdEstado(dto.getIdEstado());
            cobro.setEstadoCobro(estado);
        }

        return cobro;
    }

    // Para listado de turnos de un cliente (mostrar abogado)
    public static TurnoListadoDTO mapTurnoToListadoDTOParaCliente(Turno turno) {
        if (turno == null) return null;
        TurnoListadoDTO dto = new TurnoListadoDTO();
        dto.setIdTurno(turno.getIdTurno());
        dto.setPersona(turno.getAbogadoTurno().getNombre() + " " + turno.getAbogadoTurno().getApellido());
        dto.setFechaHora(turno.getHorarioTurno());
        dto.setEstado(turno.getEstadoActual().getNombreEstado());
        return dto;
    }

    // Para listado de turnos de un abogado (mostrar cliente)
    public static TurnoListadoDTO mapTurnoToListadoDTOParaAbogado(Turno turno) {
        if (turno == null) return null;
        TurnoListadoDTO dto = new TurnoListadoDTO();
        dto.setIdTurno(turno.getIdTurno());
        dto.setPersona(turno.getClienteTurno().getNombre() + " " + turno.getClienteTurno().getApellido());
        dto.setFechaHora(turno.getHorarioTurno());
        dto.setEstado(turno.getEstadoActual().getNombreEstado());
        return dto;
    }

    // Vista para el CLIENTE (mostrar datos del abogado)
    public static TurnoDetalleDTO mapTurnoToDetalleDTOParaCliente(Turno turno) {
        if (turno == null) return null;
        TurnoDetalleDTO dto = new TurnoDetalleDTO();

        Usuario abogado = turno.getAbogadoTurno();
        dto.setIdTurno(turno.getIdTurno());
        dto.setPersona(abogado.getNombre() + " " + abogado.getApellido());
        dto.setDni(abogado.getDni());
        dto.setTelefono(abogado.getTelefono());
        if (abogado.getDireccion() != null) {
            Direccion dir = abogado.getDireccion();
            dto.setDireccion(dir.getCalle() + " " + dir.getNumeroCalle());
        }
        dto.setEspecialidad(turno.getEspecialidad().getNombreEspecialidad());
        dto.setFechaHora(turno.getHorarioTurno());
        dto.setObservacionesCliente(turno.getObservacionesCliente());
        dto.setObservacionesAbogado(turno.getObservacionesAbogado());
        dto.setEstado(turno.getEstadoActual().getNombreEstado());

        return dto;
    }

    // Vista para el ABOGADO (mostrar datos del cliente)
    public static TurnoDetalleDTO mapTurnoToDetalleDTOParaAbogado(Turno turno) {
        if (turno == null) return null;
        TurnoDetalleDTO dto = new TurnoDetalleDTO();

        Usuario cliente = turno.getClienteTurno();
        dto.setIdTurno(turno.getIdTurno());
        dto.setPersona(cliente.getNombre() + " " + cliente.getApellido());
        dto.setDni(cliente.getDni());
        dto.setTelefono(cliente.getTelefono());
        if (cliente.getDireccion() != null) {
            Direccion dir = cliente.getDireccion();
            dto.setDireccion(dir.getCalle() + " " + dir.getNumeroCalle());
        }
        dto.setEspecialidad(turno.getEspecialidad().getNombreEspecialidad());
        dto.setFechaHora(turno.getHorarioTurno());
        dto.setObservacionesCliente(turno.getObservacionesCliente());
        dto.setObservacionesAbogado(turno.getObservacionesAbogado());
        dto.setEstado(turno.getEstadoActual().getNombreEstado());

        return dto;
    }


    //Cobro -> CobroConDetallesDTO
    public static CobroConDetallesDTO mapCobroToConDetallesDTO(Cobro cobro, List<DetalleCobro> detalles) {
        if (cobro == null) return null;
        CobroConDetallesDTO dto = new CobroConDetallesDTO();

        dto.setIdCobro(cobro.getIdCobro());
        dto.setImporteTotal(cobro.getImporteTotal());

        if (cobro.getTurno() != null) {
            dto.setIdTurno(cobro.getTurno().getIdTurno());
        }

        if (cobro.getEstadoCobro() != null) {
            dto.setIdEstado(cobro.getEstadoCobro().getIdEstado());
            dto.setNombreEstado(cobro.getEstadoCobro().getNombreEstado());
            dto.setAmbitoEstado(cobro.getEstadoCobro().getAmbito());
        }

        if (detalles != null) {
            dto.setDetalles(detalles.stream()
                    .map(Utils::mapDetalleCobroToConTipoDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    //DetalleCobro -> DetalleCobroConTipoDTO
    public static DetalleCobroConTipoDTO mapDetalleCobroToConTipoDTO(DetalleCobro detalle) {
        if (detalle == null) return null;
        DetalleCobroConTipoDTO dto = new DetalleCobroConTipoDTO();

        dto.setIdDetalleCobro(detalle.getIdDetalleCobro());
        dto.setFecha(detalle.getFecha());
        dto.setDescripcionCobro(detalle.getDescripcionCobro());
        dto.setSubTotal(detalle.getSubTotal());

        if (detalle.getTipoCobro() != null) {
            dto.setTipoCobro(mapTipoCobroEntityToDTO(detalle.getTipoCobro()));
        }

        return dto;
    }

    //Turno -> TurnoConHistorialDTO
    public static TurnoConHistorialDTO mapTurnoToConHistorialDTO(Turno turno) {
        if (turno == null) return null;
        TurnoConHistorialDTO dto = new TurnoConHistorialDTO();

        dto.setIdTurno(turno.getIdTurno());

        if (turno.getClienteTurno() != null) {
            dto.setIdCliente(turno.getClienteTurno().getIdUsuario());
            dto.setNombreCliente(turno.getClienteTurno().getNombre() + " " + turno.getClienteTurno().getApellido());
        }

        if (turno.getAbogadoTurno() != null) {
            dto.setIdAbogado(turno.getAbogadoTurno().getIdUsuario());
            dto.setNombreAbogado(turno.getAbogadoTurno().getNombre() + " " + turno.getAbogadoTurno().getApellido());
        }

        if (turno.getEspecialidad() != null) {
            dto.setIdEspecialidad(turno.getEspecialidad().getIdEspecialidad());
            dto.setNombreEspecialidad(turno.getEspecialidad().getNombreEspecialidad());
        }

        dto.setObservacionesCliente(turno.getObservacionesCliente());
        dto.setObservacionesAbogado(turno.getObservacionesAbogado());
        dto.setHorarioTurno(turno.getHorarioTurno());

        if (turno.getEstadoActual() != null) {
            dto.setIdEstado(turno.getEstadoActual().getIdEstado());
            dto.setNombreEstado(turno.getEstadoActual().getNombreEstado());
        }

        if (turno.getHistorialTurno() != null) {
            dto.setHistorial(turno.getHistorialTurno().stream()
                    .map(Utils::mapHistorialToConEstadoDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    //HistorialTurno -> HistorialConEstadoDTO
    public static HistorialConEstadoDTO mapHistorialToConEstadoDTO(HistorialTurno ht) {
        if (ht == null) return null;
        HistorialConEstadoDTO dto = new HistorialConEstadoDTO();

        dto.setIdHistorial(ht.getIdHistorial());
        dto.setFechaHoraInicio(ht.getFechaHoraInicio());
        dto.setFechaHoraFin(ht.getFechaHoraFin());

        if (ht.getEstadoHistorial() != null) {
            dto.setIdEstado(ht.getEstadoHistorial().getIdEstado());
            dto.setNombreEstado(ht.getEstadoHistorial().getNombreEstado());
            dto.setAmbitoEstado(ht.getEstadoHistorial().getAmbito());
        }

        return dto;
    }

    public static RegisterDTO mapUsuarioToRegisterDTO(Usuario usuario) {
        RegisterDTO dto = new RegisterDTO();
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setTelefono(usuario.getTelefono());
        dto.setDni(usuario.getDni());
        dto.setPassword(""); // No mapeamos password por seguridad

        // Dirección si existe
        if (usuario.getDireccion() != null) {
            DireccionDTO direccionDTO = mapDireccionEntityToDTO(usuario.getDireccion());
            dto.setDireccion(direccionDTO);
        }

        // 🔥 CAMBIO: Obtener rol de la lista
        if (usuario.getRolesUsuario() != null && !usuario.getRolesUsuario().isEmpty()) {
            dto.setIdRol(usuario.getRolesUsuario().get(0).getIdRol());
        }

        return dto;
    }
}