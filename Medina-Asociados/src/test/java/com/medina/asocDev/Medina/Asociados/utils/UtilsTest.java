package com.medina.asocDev.Medina.Asociados.utils;

import com.medina.asocDev.Medina.Asociados.dto.*;
import com.medina.asocDev.Medina.Asociados.entity.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    private Usuario createUsuario() {
        Usuario u = new Usuario();
        u.setIdUsuario(1L);
        u.setNombre("Juan");
        u.setApellido("Perez");
        u.setDni("12345678");
        u.setTelefono("1122334455");
        u.setEmail("juan@test.com");
        u.setPassword("pass123");
        return u;
    }

    private Direccion createDireccion(Localidad localidad) {
        Direccion d = new Direccion();
        d.setIdDireccion(10L);
        d.setCalle("Av. Siempre Viva");
        d.setNumeroCalle(742);
        d.setLocalidad(localidad);
        return d;
    }

    private Localidad createLocalidad() {
        Localidad l = new Localidad();
        l.setIdLocalidad(1L);
        l.setNombreLocalidad("Springfield");
        l.setCodigoPostal("1234");
        return l;
    }

    private Rol createRol() {
        Rol r = new Rol();
        r.setIdRol(1L);
        r.setNombre("CLIENTE");
        return r;
    }

    private Especialidad createEspecialidad() {
        Especialidad e = new Especialidad();
        e.setIdEspecialidad(1L);
        e.setNombreEspecialidad("Penal");
        e.setDescripcionEspecialidad("Derecho Penal");
        return e;
    }

    private Estado createEstado() {
        Estado e = new Estado();
        e.setIdEstado(1L);
        e.setNombreEstado("RESERVADO");
        e.setAmbito("TURNO");
        return e;
    }

    private Cobro createCobro(Estado estado) {
        Cobro c = new Cobro();
        c.setIdCobro(100L);
        c.setImporteTotal(5000f);
        c.setEstadoCobro(estado);
        return c;
    }

    private TipoCobro createTipoCobro() {
        TipoCobro tc = new TipoCobro();
        tc.setIdTipoCobro(1L);
        tc.setNombreTipoCobro("HONORARIOS");
        tc.setDescTipoCobro("Honorarios profesionales");
        return tc;
    }

    private DetalleCobro createDetalleCobro(Cobro cobro, TipoCobro tipoCobro) {
        DetalleCobro dc = new DetalleCobro();
        dc.setIdDetalleCobro(1L);
        dc.setCobro(cobro);
        dc.setDescripcionCobro("Consulta penal");
        dc.setTipoCobro(tipoCobro);
        dc.setFecha(LocalDateTime.of(2026, 7, 10, 10, 0));
        dc.setSubTotal(5000f);
        return dc;
    }

    private HistorialTurno createHistorialTurno(Estado estado, Turno turno) {
        HistorialTurno ht = new HistorialTurno();
        ht.setIdHistorial(1L);
        ht.setEstadoHistorial(estado);
        ht.setFechaHoraInicio(LocalDateTime.of(2026, 7, 10, 10, 0));
        ht.setFechaHoraFin(null);
        ht.setTurno(turno);
        return ht;
    }

    private Abogado createAbogado(Usuario usuario, List<Especialidad> especialidades) {
        Abogado a = new Abogado();
        a.setIdAbogado(1L);
        a.setMatricula("MAT-001");
        a.setUsuario(usuario);
        a.setEspecialidadesAbogado(especialidades);
        return a;
    }

    private Turno createTurno(Usuario abogado, Usuario cliente, Cobro cobro,
                              Especialidad especialidad, Estado estado,
                              HistorialTurno historial) {
        Turno t = Turno.builder()
                .idTurno(1L)
                .abogadoTurno(abogado)
                .clienteTurno(cliente)
                .cobro(cobro)
                .especialidad(especialidad)
                .estadoActual(estado)
                .historialTurno(historial != null ? List.of(historial) : new ArrayList<>())
                .observacionesCliente("Obs cliente")
                .observacionesAbogado("Obs abogado")
                .horarioTurno(LocalDateTime.of(2026, 7, 20, 14, 0))
                .build();
        if (cobro != null) cobro.setTurno(t);
        if (historial != null) historial.setTurno(t);
        return t;
    }

    @Test
    void generateRandomConfirmationCode_returnsCorrectLength() {
        String code = Utils.generateRandomConfirmationCode(6);
        assertNotNull(code);
        assertEquals(6, code.length());

        String code2 = Utils.generateRandomConfirmationCode(10);
        assertEquals(10, code2.length());
    }

    @Test
    void generateRandomConfirmationCode_containsOnlyAlphanumeric() {
        String code = Utils.generateRandomConfirmationCode(100);
        assertTrue(code.matches("[A-Z0-9]+"));
    }

    @Test
    void validarDiaHabil_weekdayDoesNotThrow() {
        assertDoesNotThrow(() ->
                Utils.validarDiaHabil(LocalDateTime.of(2026, 7, 15, 10, 0)));
    }

    @Test
    void validarDiaHabil_saturdayThrows() {
        assertThrows(RuntimeException.class, () ->
                Utils.validarDiaHabil(LocalDateTime.of(2026, 7, 18, 10, 0)));
    }

    @Test
    void validarDiaHabil_sundayThrows() {
        assertThrows(RuntimeException.class, () ->
                Utils.validarDiaHabil(LocalDateTime.of(2026, 7, 19, 10, 0)));
    }

    @Test
    void mapUserEntityToUserDTO_mapsAllFields() {
        Rol rol = createRol();
        Localidad localidad = createLocalidad();
        Direccion direccion = createDireccion(localidad);
        Usuario usuario = createUsuario();
        usuario.getRolesUsuario().add(rol);
        usuario.setDireccion(direccion);

        UsuarioDTO dto = Utils.mapUserEntityToUserDTO(usuario);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdUsuario());
        assertEquals("Juan", dto.getNombre());
        assertEquals("Perez", dto.getApellido());
        assertEquals("12345678", dto.getDni());
        assertEquals("1122334455", dto.getTelefono());
        assertEquals("juan@test.com", dto.getEmail());
        assertEquals("pass123", dto.getPassword());
        assertEquals(1L, dto.getIdRol());
        assertEquals(10L, dto.getIdDireccion());
    }

    @Test
    void mapUserEntityToUserDTO_nullReturnsNull() {
        assertNull(Utils.mapUserEntityToUserDTO(null));
    }

    @Test
    void mapUserEntityToUserDTO_withoutRolAndDireccion() {
        Usuario usuario = createUsuario();
        UsuarioDTO dto = Utils.mapUserEntityToUserDTO(usuario);
        assertNotNull(dto);
        assertNull(dto.getIdRol());
        assertNull(dto.getIdDireccion());
    }

    @Test
    void mapRolEntityToRolDTO_mapsAllFields() {
        RolDTO dto = Utils.mapRolEntityToRolDTO(createRol());
        assertNotNull(dto);
        assertEquals(1L, dto.getIdRol());
        assertEquals("CLIENTE", dto.getNombre());
    }

    @Test
    void mapRolEntityToRolDTO_nullReturnsNull() {
        assertNull(Utils.mapRolEntityToRolDTO(null));
    }

    @Test
    void mapDireccionEntityToDTO_mapsAllFields() {
        DireccionDTO dto = Utils.mapDireccionEntityToDTO(createDireccion(createLocalidad()));
        assertNotNull(dto);
        assertEquals(10L, dto.getIdDireccion());
        assertEquals("Av. Siempre Viva", dto.getCalle());
        assertEquals(742, dto.getNumeroCalle());
        assertEquals(1L, dto.getLocalidad());
    }

    @Test
    void mapDireccionEntityToDTO_nullReturnsNull() {
        assertNull(Utils.mapDireccionEntityToDTO(null));
    }

    @Test
    void mapLocalidadEntityToDTO_mapsAllFields() {
        LocalidadDTO dto = Utils.mapLocalidadEntityToDTO(createLocalidad());
        assertNotNull(dto);
        assertEquals(1L, dto.getIdLocalidad());
        assertEquals("Springfield", dto.getNombreLocalidad());
        assertEquals("1234", dto.getCodigoPostal());
    }

    @Test
    void mapLocalidadEntityToDTO_nullReturnsNull() {
        assertNull(Utils.mapLocalidadEntityToDTO(null));
    }

    @Test
    void mapTurnoEntityToDTO_mapsAllFields() {
        Usuario abogado = createUsuario();
        abogado.setIdUsuario(1L);
        Usuario cliente = createUsuario();
        cliente.setIdUsuario(2L);
        Cobro cobro = createCobro(createEstado());
        Especialidad especialidad = createEspecialidad();
        Estado estado = createEstado();
        Turno turno = createTurno(abogado, cliente, cobro, especialidad, estado, null);

        TurnoDTO dto = Utils.mapTurnoEntityToDTO(turno);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdTurno());
        assertEquals(1L, dto.getAbogadoTurno());
        assertEquals(2L, dto.getUsuarioTurno());
        assertEquals(100L, dto.getIdCobro());
        assertEquals(1L, dto.getIdEspecialidad());
        assertEquals(1L, dto.getIdEstado());
        assertEquals("Obs cliente", dto.getObservacionesCliente());
        assertEquals("Obs abogado", dto.getObservacionesAbogado());
        assertNotNull(dto.getHorarioTurno());
    }

    @Test
    void mapTurnoEntityToDTO_nullReturnsNull() {
        assertNull(Utils.mapTurnoEntityToDTO(null));
    }

    @Test
    void mapUsuarioEntityToDTOxTurnos_withTurnos() {
        Usuario usuario = createUsuario();
        usuario.getRolesUsuario().add(createRol());
        usuario.setDireccion(createDireccion(createLocalidad()));
        Turno turno = createTurno(createUsuario(), usuario, createCobro(createEstado()),
                createEspecialidad(), createEstado(), null);
        usuario.getListaTurnos().add(turno);

        UsuarioDTO dto = Utils.mapUsuarioEntityToDTOxTurnos(usuario);

        assertNotNull(dto);
        assertEquals(1, dto.getIdTurnos().size());
        assertEquals(1L, dto.getIdTurnos().get(0));
    }

    @Test
    void mapUsuarioEntityToDTOxTurnos_nullReturnsNull() {
        assertNull(Utils.mapUsuarioEntityToDTOxTurnos(null));
    }

    @Test
    void mapEstadoEntityToDTO_mapsAllFields() {
        EstadoDTO dto = Utils.mapEstadoEntityToDTO(createEstado());
        assertNotNull(dto);
        assertEquals(1L, dto.getIdEstado());
        assertEquals("RESERVADO", dto.getNombreEstado());
        assertEquals("TURNO", dto.getAmbito());
    }

    @Test
    void mapEstadoEntityToDTO_nullReturnsNull() {
        assertNull(Utils.mapEstadoEntityToDTO(null));
    }

    @Test
    void mapTipoCobroEntityToDTO_mapsAllFields() {
        TipoCobroDTO dto = Utils.mapTipoCobroEntityToDTO(createTipoCobro());
        assertNotNull(dto);
        assertEquals(1L, dto.getIdTipoCobro());
        assertEquals("HONORARIOS", dto.getNombreTipoCobro());
        assertEquals("Honorarios profesionales", dto.getDescripcionTipoCobro());
    }

    @Test
    void mapTipoCobroEntityToDTO_nullReturnsNull() {
        assertNull(Utils.mapTipoCobroEntityToDTO(null));
    }

    @Test
    void mapEspecialidadEntityToDTO_mapsAllFields() {
        EspecialidadDTO dto = Utils.mapEspecialidadEntityToDTO(createEspecialidad());
        assertNotNull(dto);
        assertEquals(1L, dto.getIdEspecialidad());
        assertEquals("Penal", dto.getNombreEspecialidad());
        assertEquals("Derecho Penal", dto.getDescripcionEspecialidad());
    }

    @Test
    void mapEspecialidadEntityToDTO_nullReturnsNull() {
        assertNull(Utils.mapEspecialidadEntityToDTO(null));
    }

    @Test
    void mapHistorialTurnoEntityToDTO_mapsAllFields() {
        Estado estado = createEstado();
        Turno turno = Turno.builder().idTurno(1L).historialTurno(new ArrayList<>()).build();
        HistorialTurno ht = createHistorialTurno(estado, turno);

        HistorialTurnoDTO dto = Utils.mapHistorialTurnoEntityToDTO(ht);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdHistorial());
        assertEquals(1L, dto.getIdEstado());
        assertNotNull(dto.getFechaHoraInicio());
        assertNull(dto.getFechaHoraFin());
        assertEquals(1L, dto.getIdTurno());
    }

    @Test
    void mapHistorialTurnoEntityToDTO_nullReturnsNull() {
        assertNull(Utils.mapHistorialTurnoEntityToDTO(null));
    }

    @Test
    void mapHistorialTurnoEntityListToDTO_mapsList() {
        Estado estado = createEstado();
        Turno turno = Turno.builder().idTurno(1L).historialTurno(new ArrayList<>()).build();
        List<HistorialTurno> list = List.of(createHistorialTurno(estado, turno));

        List<HistorialTurnoDTO> result = Utils.mapHistorialTurnoEntityListToDTO(list);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getIdHistorial());
    }

    @Test
    void mapHistorialTurnoEntityListToDTO_nullReturnsEmpty() {
        assertTrue(Utils.mapHistorialTurnoEntityListToDTO(null).isEmpty());
    }

    @Test
    void mapAbogadoEntityToDTOxUsuarioSinTurno_mapsAllFields() {
        Usuario usuario = createUsuario();
        Especialidad especialidad = createEspecialidad();
        Abogado abogado = createAbogado(usuario, List.of(especialidad));

        AbogadoDTO dto = Utils.mapAbogadoEntityToDTOxUsuarioSinTurno(abogado);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdAbogado());
        assertEquals("MAT-001", dto.getMatricula());
        assertEquals(1L, dto.getIdUsuario());
        assertEquals("Juan", dto.getNombre());
        assertEquals("Perez", dto.getApellido());
        assertEquals(1, dto.getEspecialidadesAbogado().size());
        assertEquals(1L, dto.getEspecialidadesAbogado().get(0));
    }

    @Test
    void mapAbogadoEntityToDTOxUsuarioSinTurno_nullReturnsNull() {
        assertNull(Utils.mapAbogadoEntityToDTOxUsuarioSinTurno(null));
    }

    @Test
    void mapAbogadoEntityToDTOxUsuarioSinTurno_withoutEspecialidades() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        Abogado abogado = new Abogado();
        abogado.setIdAbogado(1L);
        abogado.setMatricula("MAT-001");
        abogado.setUsuario(usuario);

        AbogadoDTO dto = Utils.mapAbogadoEntityToDTOxUsuarioSinTurno(abogado);

        assertNotNull(dto);
        assertNull(dto.getNombre());
        assertNull(dto.getApellido());
        assertTrue(dto.getEspecialidadesAbogado() == null || dto.getEspecialidadesAbogado().isEmpty());
    }

    @Test
    void mapAbogadoEntityToDTOConTurnos_mapsAllFields() {
        Usuario usuario = createUsuario();
        usuario.getRolesUsuario().add(createRol());
        Turno turno = createTurno(createUsuario(), usuario, createCobro(createEstado()),
                createEspecialidad(), createEstado(), null);
        usuario.getListaTurnos().add(turno);
        Abogado abogado = createAbogado(usuario, List.of(createEspecialidad()));

        AbogadoConTurnosDTO dto = Utils.mapAbogadoEntityToDTOConTurnos(abogado);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdAbogado());
        assertEquals("MAT-001", dto.getMatricula());
        assertNotNull(dto.getUsuario());
        assertEquals(1L, dto.getUsuario().getIdTurnos().get(0));
    }

    @Test
    void mapAbogadoEntityToDTOConTurnos_nullReturnsNull() {
        assertNull(Utils.mapAbogadoEntityToDTOConTurnos(null));
    }

    @Test
    void mapCobroEntityToDTO_mapsAllFields() {
        Cobro cobro = createCobro(createEstado());
        Turno turno = Turno.builder().idTurno(1L).historialTurno(new ArrayList<>()).build();
        cobro.setTurno(turno);

        CobroDTO dto = Utils.mapCobroEntityToDTO(cobro);

        assertNotNull(dto);
        assertEquals(100L, dto.getIdCobro());
        assertEquals(5000f, dto.getImporteTotal());
        assertEquals(1L, dto.getIdTurno());
    }

    @Test
    void mapCobroEntityToDTO_nullReturnsNull() {
        assertNull(Utils.mapCobroEntityToDTO(null));
    }

    @Test
    void mapCobroEntityToDTO_withoutTurno() {
        Cobro cobro = createCobro(createEstado());
        CobroDTO dto = Utils.mapCobroEntityToDTO(cobro);
        assertNull(dto.getIdTurno());
    }

    @Test
    void mapDetalleCobroEntityToDTO_mapsAllFields() {
        Cobro cobro = createCobro(createEstado());
        DetalleCobro dc = createDetalleCobro(cobro, createTipoCobro());

        DetalleCobroDTO dto = Utils.mapDetalleCobroEntityToDTO(dc);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdDetalleCobro());
        assertEquals(100L, dto.getIdCobro());
        assertEquals("Consulta penal", dto.getDescripcionCobro());
        assertEquals(1L, dto.getIdTipoCobro());
        assertNotNull(dto.getFecha());
        assertEquals(5000f, dto.getSubTotal());
    }

    @Test
    void mapDetalleCobroEntityToDTO_nullReturnsNull() {
        assertNull(Utils.mapDetalleCobroEntityToDTO(null));
    }

    @Test
    void mapRegistroDTOToEntity_mapsAllFields() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setNombre("Ana");
        registerDTO.setApellido("Lopez");
        registerDTO.setDni("87654321");
        registerDTO.setEmail("ana@test.com");
        registerDTO.setTelefono("9988776655");
        registerDTO.setPassword("pass456");

        Rol rol = createRol();
        Direccion direccion = createDireccion(createLocalidad());

        Usuario usuario = Utils.mapRegistroDTOToEntity(registerDTO, rol, direccion);

        assertNotNull(usuario);
        assertEquals("Ana", usuario.getNombre());
        assertEquals("Lopez", usuario.getApellido());
        assertEquals(1, usuario.getRolesUsuario().size());
        assertEquals(1L, usuario.getRolesUsuario().get(0).getIdRol());
        assertNotNull(usuario.getDireccion());
        assertEquals(10L, usuario.getDireccion().getIdDireccion());
    }

    @Test
    void mapRegistroDTOToEntity_withoutRol() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setNombre("Ana");
        registerDTO.setApellido("Lopez");
        registerDTO.setDni("87654321");
        registerDTO.setEmail("ana@test.com");
        registerDTO.setTelefono("9988776655");
        registerDTO.setPassword("pass456");

        Usuario usuario = Utils.mapRegistroDTOToEntity(registerDTO, null, null);
        assertTrue(usuario.getRolesUsuario().isEmpty());
        assertNull(usuario.getDireccion());
    }

    @Test
    void mapDireccionDTOToEntity_mapsAllFields() {
        DireccionDTO dto = new DireccionDTO();
        dto.setIdDireccion(10L);
        dto.setCalle("Av. Siempre Viva");
        dto.setNumeroCalle(742);
        dto.setLocalidad(1L);

        Direccion direccion = Utils.mapDireccionDTOToEntity(dto);

        assertNotNull(direccion);
        assertEquals("Av. Siempre Viva", direccion.getCalle());
        assertEquals(742, direccion.getNumeroCalle());
        assertNotNull(direccion.getLocalidad());
        assertEquals(1L, direccion.getLocalidad().getIdLocalidad());
    }

    @Test
    void mapDireccionDTOToEntity_nullReturnsNull() {
        assertNull(Utils.mapDireccionDTOToEntity(null));
    }

    @Test
    void mapDireccionDTOToEntity_withoutLocalidad() {
        DireccionDTO dto = new DireccionDTO();
        dto.setIdDireccion(10L);
        dto.setCalle("Av. Siempre Viva");
        dto.setNumeroCalle(742);

        Direccion direccion = Utils.mapDireccionDTOToEntity(dto);
        assertNull(direccion.getLocalidad());
    }

    @Test
    void mapLocalidadDTOToEntity_mapsAllFields() {
        LocalidadDTO dto = new LocalidadDTO();
        dto.setIdLocalidad(1L);
        dto.setNombreLocalidad("Springfield");
        dto.setCodigoPostal("1234");

        Localidad localidad = Utils.mapLocalidadDTOToEntity(dto);

        assertNotNull(localidad);
        assertEquals(1L, localidad.getIdLocalidad());
        assertEquals("Springfield", localidad.getNombreLocalidad());
        assertEquals("1234", localidad.getCodigoPostal());
    }

    @Test
    void mapLocalidadDTOToEntity_nullReturnsNull() {
        assertNull(Utils.mapLocalidadDTOToEntity(null));
    }

    @Test
    void mapUsuarioDTOToEntity_mapsAllFields() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Ana");
        dto.setApellido("Lopez");
        dto.setDni("87654321");
        dto.setEmail("ana@test.com");
        dto.setTelefono("9988776655");
        dto.setPassword("pass456");

        Rol rol = createRol();
        Direccion direccion = createDireccion(createLocalidad());

        Usuario usuario = Utils.mapUsuarioDTOToEntity(dto, rol, direccion);

        assertNotNull(usuario);
        assertEquals("Ana", usuario.getNombre());
        assertEquals(1, usuario.getRolesUsuario().size());
        assertNotNull(usuario.getDireccion());
    }

    @Test
    void mapUsuarioDTOToEntity_withoutRolAndDireccion() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Ana");
        dto.setApellido("Lopez");
        dto.setDni("87654321");
        dto.setEmail("ana@test.com");
        dto.setTelefono("9988776655");
        dto.setPassword("pass456");

        Usuario usuario = Utils.mapUsuarioDTOToEntity(dto, null, null);
        assertTrue(usuario.getRolesUsuario().isEmpty());
        assertNull(usuario.getDireccion());
    }

    @Test
    void mapAbogadoDTOToEntity_mapsAllFields() {
        AbogadoDTO dto = new AbogadoDTO();
        dto.setIdAbogado(1L);
        dto.setMatricula("MAT-001");

        Usuario usuario = createUsuario();
        List<Especialidad> especialidades = List.of(createEspecialidad());

        Abogado abogado = Utils.mapAbogadoDTOToEntity(dto, usuario, especialidades);

        assertNotNull(abogado);
        assertEquals(1L, abogado.getIdAbogado());
        assertEquals("MAT-001", abogado.getMatricula());
        assertEquals(usuario, abogado.getUsuario());
        assertEquals(1, abogado.getEspecialidadesAbogado().size());
    }

    @Test
    void mapAbogadoDTOToEntity_nullReturnsNull() {
        assertNull(Utils.mapAbogadoDTOToEntity(null, null, null));
    }

    @Test
    void mapDTOToCobro_mapsAllFields() {
        CobroDTO dto = new CobroDTO();
        dto.setIdCobro(100L);
        dto.setImporteTotal(5000f);
        dto.setIdEstado(1L);

        Cobro cobro = Utils.mapDTOToCobro(dto);

        assertNotNull(cobro);
        assertEquals(100L, cobro.getIdCobro());
        assertEquals(5000f, cobro.getImporteTotal());
        assertNotNull(cobro.getEstadoCobro());
        assertEquals(1L, cobro.getEstadoCobro().getIdEstado());
    }

    @Test
    void mapDTOToCobro_withoutEstado() {
        CobroDTO dto = new CobroDTO();
        dto.setIdCobro(100L);
        dto.setImporteTotal(5000f);

        Cobro cobro = Utils.mapDTOToCobro(dto);
        assertNull(cobro.getEstadoCobro());
    }

    @Test
    void mapTurnoToListadoDTOParaCliente_mapsAllFields() {
        Usuario abogado = createUsuario();
        abogado.setNombre("Carlos");
        abogado.setApellido("Garcia");
        Turno turno = createTurno(abogado, createUsuario(), createCobro(createEstado()),
                createEspecialidad(), createEstado(), null);

        TurnoListadoDTO dto = Utils.mapTurnoToListadoDTOParaCliente(turno);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdTurno());
        assertEquals("Carlos Garcia", dto.getPersona());
        assertEquals("RESERVADO", dto.getEstado());
        assertNotNull(dto.getFechaHora());
    }

    @Test
    void mapTurnoToListadoDTOParaCliente_nullReturnsNull() {
        assertNull(Utils.mapTurnoToListadoDTOParaCliente(null));
    }

    @Test
    void mapTurnoToListadoDTOParaAbogado_mapsAllFields() {
        Usuario cliente = createUsuario();
        cliente.setNombre("Maria");
        cliente.setApellido("Gomez");
        Turno turno = createTurno(createUsuario(), cliente, createCobro(createEstado()),
                createEspecialidad(), createEstado(), null);

        TurnoListadoDTO dto = Utils.mapTurnoToListadoDTOParaAbogado(turno);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdTurno());
        assertEquals("Maria Gomez", dto.getPersona());
        assertEquals("RESERVADO", dto.getEstado());
    }

    @Test
    void mapTurnoToListadoDTOParaAbogado_nullReturnsNull() {
        assertNull(Utils.mapTurnoToListadoDTOParaAbogado(null));
    }

    @Test
    void mapTurnoToDetalleDTOParaCliente_mapsAllFields() {
        Usuario abogado = createUsuario();
        abogado.setNombre("Carlos");
        abogado.setApellido("Garcia");
        abogado.setDni("11111111");
        abogado.setTelefono("1111111111");
        abogado.setDireccion(createDireccion(createLocalidad()));
        Turno turno = createTurno(abogado, createUsuario(), createCobro(createEstado()),
                createEspecialidad(), createEstado(), null);

        TurnoDetalleDTO dto = Utils.mapTurnoToDetalleDTOParaCliente(turno);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdTurno());
        assertEquals("Carlos Garcia", dto.getPersona());
        assertEquals("11111111", dto.getDni());
        assertEquals("Av. Siempre Viva 742", dto.getDireccion());
        assertEquals("Penal", dto.getEspecialidad());
        assertEquals("RESERVADO", dto.getEstado());
    }

    @Test
    void mapTurnoToDetalleDTOParaCliente_nullReturnsNull() {
        assertNull(Utils.mapTurnoToDetalleDTOParaCliente(null));
    }

    @Test
    void mapTurnoToDetalleDTOParaAbogado_mapsAllFields() {
        Usuario cliente = createUsuario();
        cliente.setNombre("Maria");
        cliente.setApellido("Gomez");
        cliente.setDni("22222222");
        cliente.setTelefono("2222222222");
        cliente.setDireccion(createDireccion(createLocalidad()));
        Turno turno = createTurno(createUsuario(), cliente, createCobro(createEstado()),
                createEspecialidad(), createEstado(), null);

        TurnoDetalleDTO dto = Utils.mapTurnoToDetalleDTOParaAbogado(turno);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdTurno());
        assertEquals("Maria Gomez", dto.getPersona());
        assertEquals("22222222", dto.getDni());
        assertEquals("Av. Siempre Viva 742", dto.getDireccion());
        assertEquals("Penal", dto.getEspecialidad());
        assertEquals("RESERVADO", dto.getEstado());
    }

    @Test
    void mapTurnoToDetalleDTOParaAbogado_nullReturnsNull() {
        assertNull(Utils.mapTurnoToDetalleDTOParaAbogado(null));
    }

    @Test
    void mapCobroToConDetallesDTO_mapsAllFields() {
        Cobro cobro = createCobro(createEstado());
        Turno turno = Turno.builder().idTurno(1L).historialTurno(new ArrayList<>()).build();
        cobro.setTurno(turno);
        DetalleCobro dc = createDetalleCobro(cobro, createTipoCobro());

        CobroConDetallesDTO dto = Utils.mapCobroToConDetallesDTO(cobro, List.of(dc));

        assertNotNull(dto);
        assertEquals(100L, dto.getIdCobro());
        assertEquals(5000f, dto.getImporteTotal());
        assertEquals(1L, dto.getIdTurno());
        assertEquals(1L, dto.getIdEstado());
        assertEquals(1, dto.getDetalles().size());
    }

    @Test
    void mapCobroToConDetallesDTO_nullReturnsNull() {
        assertNull(Utils.mapCobroToConDetallesDTO(null, null));
    }

    @Test
    void mapCobroToConDetallesDTO_withoutTurnoAndEstado() {
        Cobro cobro = new Cobro();
        cobro.setIdCobro(100L);
        cobro.setImporteTotal(5000f);

        CobroConDetallesDTO dto = Utils.mapCobroToConDetallesDTO(cobro, null);
        assertNull(dto.getIdTurno());
        assertNull(dto.getIdEstado());
        assertNull(dto.getDetalles());
    }

    @Test
    void mapDetalleCobroToConTipoDTO_mapsAllFields() {
        Cobro cobro = createCobro(createEstado());
        DetalleCobro dc = createDetalleCobro(cobro, createTipoCobro());

        DetalleCobroConTipoDTO dto = Utils.mapDetalleCobroToConTipoDTO(dc);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdDetalleCobro());
        assertEquals(5000f, dto.getSubTotal());
        assertNotNull(dto.getTipoCobro());
        assertEquals("HONORARIOS", dto.getTipoCobro().getNombreTipoCobro());
    }

    @Test
    void mapDetalleCobroToConTipoDTO_nullReturnsNull() {
        assertNull(Utils.mapDetalleCobroToConTipoDTO(null));
    }

    @Test
    void mapTurnoToConHistorialDTO_mapsAllFields() {
        Estado estado = createEstado();
        Usuario abogado = createUsuario();
        abogado.setIdUsuario(1L);
        abogado.setNombre("Carlos");
        abogado.setApellido("Garcia");
        Usuario cliente = createUsuario();
        cliente.setIdUsuario(2L);
        cliente.setNombre("Maria");
        cliente.setApellido("Gomez");
        Turno turno = createTurno(abogado, cliente, createCobro(estado),
                createEspecialidad(), estado, createHistorialTurno(estado, null));

        TurnoConHistorialDTO dto = Utils.mapTurnoToConHistorialDTO(turno);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdTurno());
        assertEquals(2L, dto.getIdCliente());
        assertEquals("Maria Gomez", dto.getNombreCliente());
        assertEquals(1L, dto.getIdAbogado());
        assertEquals("Carlos Garcia", dto.getNombreAbogado());
        assertEquals(1L, dto.getIdEspecialidad());
        assertEquals("Penal", dto.getNombreEspecialidad());
        assertEquals(1L, dto.getIdEstado());
        assertEquals("RESERVADO", dto.getNombreEstado());
        assertEquals(1, dto.getHistorial().size());
    }

    @Test
    void mapTurnoToConHistorialDTO_nullReturnsNull() {
        assertNull(Utils.mapTurnoToConHistorialDTO(null));
    }

    @Test
    void mapHistorialToConEstadoDTO_mapsAllFields() {
        Estado estado = createEstado();
        Turno turno = Turno.builder().idTurno(1L).historialTurno(new ArrayList<>()).build();
        HistorialTurno ht = createHistorialTurno(estado, turno);

        HistorialConEstadoDTO dto = Utils.mapHistorialToConEstadoDTO(ht);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdHistorial());
        assertEquals(1L, dto.getIdEstado());
        assertEquals("RESERVADO", dto.getNombreEstado());
        assertEquals("TURNO", dto.getAmbitoEstado());
    }

    @Test
    void mapHistorialToConEstadoDTO_nullReturnsNull() {
        assertNull(Utils.mapHistorialToConEstadoDTO(null));
    }

    @Test
    void mapHistorialToConEstadoDTO_withoutEstado() {
        HistorialTurno ht = new HistorialTurno();
        ht.setIdHistorial(1L);
        ht.setFechaHoraInicio(LocalDateTime.of(2026, 7, 10, 10, 0));

        HistorialConEstadoDTO dto = Utils.mapHistorialToConEstadoDTO(ht);
        assertNull(dto.getIdEstado());
        assertNull(dto.getNombreEstado());
    }

    @Test
    void mapUsuarioToRegisterDTO_mapsAllFields() {
        Usuario usuario = createUsuario();
        usuario.getRolesUsuario().add(createRol());
        usuario.setDireccion(createDireccion(createLocalidad()));

        RegisterDTO dto = Utils.mapUsuarioToRegisterDTO(usuario);

        assertNotNull(dto);
        assertEquals("juan@test.com", dto.getEmail());
        assertEquals("", dto.getPassword());
        assertEquals(1L, dto.getIdRol());
        assertNotNull(dto.getDireccion());
        assertEquals(10L, dto.getDireccion().getIdDireccion());
    }

    @Test
    void mapUsuarioToRegisterDTO_withoutDireccionAndRoles() {
        Usuario usuario = createUsuario();

        RegisterDTO dto = Utils.mapUsuarioToRegisterDTO(usuario);
        assertNull(dto.getDireccion());
        assertNull(dto.getIdRol());
    }
}
