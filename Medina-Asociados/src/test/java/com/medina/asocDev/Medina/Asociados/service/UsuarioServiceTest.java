package com.medina.asocDev.Medina.Asociados.service;

import com.medina.asocDev.Medina.Asociados.dto.*;
import com.medina.asocDev.Medina.Asociados.entity.*;
import com.medina.asocDev.Medina.Asociados.excepetion.OurException;
import com.medina.asocDev.Medina.Asociados.repo.*;
import com.medina.asocDev.Medina.Asociados.utils.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private DireccionRepository direccionRepository;
    @Mock private RolRepository rolRepository;
    @Mock private LocalidadRepository localidadRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JWTUtils jwtUtils;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private TokenBlacklistedRepository tokenBlacklistedRepository;
    @Mock private TurnoRepository turnoRepository;

    @InjectMocks private UsuarioService usuarioService;

    private Rol rolCliente;
    private Usuario usuario;
    private Direccion direccion;
    private Localidad localidad;

    @BeforeEach
    void setUp() {
        rolCliente = new Rol();
        rolCliente.setIdRol(1L);
        rolCliente.setNombre("CLIENTE");

        localidad = new Localidad();
        localidad.setIdLocalidad(1L);
        localidad.setNombreLocalidad("Córdoba");
        localidad.setCodigoPostal("5000");

        direccion = new Direccion();
        direccion.setIdDireccion(1L);
        direccion.setCalle("Av. Colón");
        direccion.setNumeroCalle(123);
        direccion.setLocalidad(localidad);

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setDni("12345678");
        usuario.setTelefono("3511234567");
        usuario.setEmail("juan@test.com");
        usuario.setPassword("encoded");
        usuario.setDireccion(direccion);
        usuario.getRolesUsuario().add(rolCliente);
    }

    @Test
    void createUsuario_withNewDireccion_returnsMensajeResponse() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setNombre("Juan");
        registerDTO.setApellido("Pérez");
        registerDTO.setDni("12345678");
        registerDTO.setTelefono("3511234567");
        registerDTO.setEmail("juan@test.com");
        registerDTO.setPassword("plain");
        DireccionDTO newDir = new DireccionDTO();
        newDir.setCalle("San Martín");
        newDir.setNumeroCalle(456);
        newDir.setLocalidad(1L);
        registerDTO.setDireccion(newDir);

        when(rolRepository.findByNombre("CLIENTE")).thenReturn(Optional.of(rolCliente));
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(direccionRepository.save(any(Direccion.class))).thenAnswer(i -> i.getArgument(0));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        MensajeResponse result = usuarioService.createUsuario(registerDTO);

        assertEquals("Registro completado, redirigiendo al inicio de sesión", result.getMessage());
        verify(direccionRepository).save(any(Direccion.class));
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void createUsuario_withExistingDireccion_returnsMensajeResponse() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setNombre("Juan");
        registerDTO.setApellido("Pérez");
        registerDTO.setDni("12345678");
        registerDTO.setTelefono("3511234567");
        registerDTO.setEmail("juan@test.com");
        registerDTO.setPassword("plain");
        DireccionDTO dirDTO = new DireccionDTO();
        dirDTO.setIdDireccion(1L);
        dirDTO.setCalle("Av. Colón");
        dirDTO.setNumeroCalle(123);
        registerDTO.setDireccion(dirDTO);

        when(rolRepository.findByNombre("CLIENTE")).thenReturn(Optional.of(rolCliente));
        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion));
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        MensajeResponse result = usuarioService.createUsuario(registerDTO);

        assertEquals("Registro completado, redirigiendo al inicio de sesión", result.getMessage());
        verify(direccionRepository, never()).save(any(Direccion.class));
    }

    @Test
    void createUsuario_direccionNull_returnsMensajeResponse() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setNombre("Juan");
        registerDTO.setApellido("Pérez");
        registerDTO.setDni("12345678");
        registerDTO.setTelefono("3511234567");
        registerDTO.setEmail("juan@test.com");
        registerDTO.setPassword("plain");
        registerDTO.setDireccion(null);

        when(rolRepository.findByNombre("CLIENTE")).thenReturn(Optional.of(rolCliente));
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        MensajeResponse result = usuarioService.createUsuario(registerDTO);

        assertEquals("Registro completado, redirigiendo al inicio de sesión", result.getMessage());
        verify(direccionRepository, never()).findById(any());
        verify(direccionRepository, never()).save(any());
    }

    @Test
    void createUsuario_rolNotFound_throwsRuntimeException() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setPassword("plain");

        when(rolRepository.findByNombre("CLIENTE")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> usuarioService.createUsuario(registerDTO));
    }

    @Test
    void getOrCreateUsuario_existingByEmail_returnsUser() {
        ClienteOfflineRequest request = new ClienteOfflineRequest();
        request.setEmail("juan@test.com");

        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(usuario));

        Usuario result = usuarioService.getOrCreateUsuario(request);

        assertSame(usuario, result);
    }

    @Test
    void getOrCreateUsuario_existingByTelefono_returnsUser() {
        ClienteOfflineRequest request = new ClienteOfflineRequest();
        request.setEmail("otro@test.com");
        request.setTelefono("3511234567");

        when(usuarioRepository.findByEmail("otro@test.com")).thenReturn(Optional.empty());
        when(usuarioRepository.findByTelefono("3511234567")).thenReturn(Optional.of(usuario));

        Usuario result = usuarioService.getOrCreateUsuario(request);

        assertSame(usuario, result);
    }

    @Test
    void getOrCreateUsuario_newUser_createsAndReturns() {
        ClienteOfflineRequest request = new ClienteOfflineRequest();
        request.setNombre("Pedro");
        request.setApellido("García");
        request.setDni("87654321");
        request.setTelefono("3517654321");
        request.setEmail("pedro@test.com");

        when(usuarioRepository.findByEmail("pedro@test.com")).thenReturn(Optional.empty());
        when(usuarioRepository.findByTelefono("3517654321")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(rolRepository.findByNombre("CLIENTE")).thenReturn(Optional.of(rolCliente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario result = usuarioService.getOrCreateUsuario(request);

        assertNotNull(result);
        assertEquals("Pedro", result.getNombre());
        assertEquals("pedro@test.com", result.getEmail());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void getOrCreateUsuario_newUserWithDireccion_createsAndReturns() {
        ClienteOfflineRequest request = new ClienteOfflineRequest();
        request.setNombre("Pedro");
        request.setApellido("García");
        request.setDni("87654321");
        request.setTelefono("3517654321");
        request.setEmail("pedro@test.com");
        DireccionDTO dirDTO = new DireccionDTO();
        dirDTO.setCalle("Belgrano");
        dirDTO.setNumeroCalle(789);
        dirDTO.setLocalidad(1L);
        request.setDireccion(dirDTO);

        when(usuarioRepository.findByEmail("pedro@test.com")).thenReturn(Optional.empty());
        when(usuarioRepository.findByTelefono("3517654321")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(rolRepository.findByNombre("CLIENTE")).thenReturn(Optional.of(rolCliente));
        when(direccionRepository.save(any(Direccion.class))).thenAnswer(i -> i.getArgument(0));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario result = usuarioService.getOrCreateUsuario(request);

        assertNotNull(result);
        assertNotNull(result.getDireccion());
        assertEquals("Belgrano", result.getDireccion().getCalle());
    }

    @Test
    void login_success_returnsResponseWithToken() {
        LogInRequest loginRequest = new LogInRequest();
        loginRequest.setEmail("juan@test.com");
        loginRequest.setPassword("plain");

        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(usuario));
        when(jwtUtils.generateToken(any())).thenReturn("fake-token");

        Response response = usuarioService.login(loginRequest);

        assertEquals(200, response.getStatusCode());
        assertEquals("fake-token", response.getToken());
        assertEquals("successful", response.getMessage());
        assertEquals("7 Days", response.getExpirationTime());
        assertNotNull(response.getUser());
        assertEquals(1L, response.getUser().getIdUsuario());
        assertEquals(List.of("CLIENTE"), response.getRoles());
    }

    @Test
    void login_badCredentials_returns401() {
        LogInRequest loginRequest = new LogInRequest();
        loginRequest.setEmail("juan@test.com");
        loginRequest.setPassword("wrong");

        doThrow(new BadCredentialsException("bad credentials"))
            .when(authenticationManager).authenticate(any());

        Response response = usuarioService.login(loginRequest);

        assertEquals(401, response.getStatusCode());
        assertEquals("Credenciales inválidas", response.getMessage());
    }

    @Test
    void login_userNotFound_returns404() {
        LogInRequest loginRequest = new LogInRequest();
        loginRequest.setEmail("unknown@test.com");
        loginRequest.setPassword("plain");

        when(usuarioRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        Response response = usuarioService.login(loginRequest);

        assertEquals(404, response.getStatusCode());
        assertEquals("user Not found", response.getMessage());
    }

    @Test
    void getMyInfo_userFound_returns200() {
        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(usuario));

        Response response = usuarioService.getMyInfo("juan@test.com");

        assertEquals(200, response.getStatusCode());
        assertEquals("Información del usuario", response.getMessage());
        assertNotNull(response.getData());
        assertInstanceOf(UsuarioDTO.class, response.getData());
        assertEquals("Juan", ((UsuarioDTO) response.getData()).getNombre());
    }

    @Test
    void getMyInfo_userNotFound_returns404() {
        when(usuarioRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        Response response = usuarioService.getMyInfo("unknown@test.com");

        assertEquals(404, response.getStatusCode());
        assertEquals("Usuario no encontrado", response.getMessage());
    }

    @Test
    void getAllUsers_returns500dueToClassCastBug() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        Response response = usuarioService.getAllUsers();

        assertEquals(500, response.getStatusCode());
        assertTrue(response.getMessage().contains("Error al obtener usuarios"));
    }

    @Test
    void getAllUsers_withPageable_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> userPage = new PageImpl<>(List.of(usuario));
        when(usuarioRepository.findAll(pageable)).thenReturn(userPage);

        Page<UsuarioDTO> result = usuarioService.getAllUsers(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Juan", result.getContent().get(0).getNombre());
    }

    @Test
    void getUserById_validIdUserFound_returns200() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Response response = usuarioService.getUserById("1");

        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        assertEquals("Usuario encontrado", response.getMessage());
    }

    @Test
    void getUserById_validIdUserNotFound_returns404() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Response response = usuarioService.getUserById("99");

        assertEquals(404, response.getStatusCode());
        assertEquals("Usuario no encontrado", response.getMessage());
    }

    @Test
    void getUserById_invalidId_returns400() {
        Response response = usuarioService.getUserById("not-a-number");

        assertEquals(400, response.getStatusCode());
        assertEquals("ID de usuario inválido", response.getMessage());
    }

    @Test
    void deleteUser_userExists_returns200() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        Response response = usuarioService.deleteUser("1");

        assertEquals(200, response.getStatusCode());
        assertEquals("Usuario eliminado correctamente", response.getMessage());
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void deleteUser_userNotFound_returns404() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        Response response = usuarioService.deleteUser("99");

        assertEquals(404, response.getStatusCode());
        assertEquals("Usuario no encontrado", response.getMessage());
        verify(usuarioRepository, never()).deleteById(any());
    }

    @Test
    void deleteUser_invalidId_returns400() {
        Response response = usuarioService.deleteUser("not-a-number");

        assertEquals(400, response.getStatusCode());
        assertEquals("ID de usuario inválido", response.getMessage());
    }

    @Test
    void updateUser_userExists_returnsUpdatedDTO() {
        UsuarioDTO updateDTO = new UsuarioDTO();
        updateDTO.setNombre("Juan Carlos");
        updateDTO.setApellido("Pérez");
        updateDTO.setDni("12345678");
        updateDTO.setTelefono("3511234567");
        updateDTO.setEmail("juan@test.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioDTO result = usuarioService.updateUser(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Juan Carlos", result.getNombre());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void updateUser_userNotFound_returnsNull() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        UsuarioDTO result = usuarioService.updateUser(99L, new UsuarioDTO());

        assertNull(result);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deleteUserInternal_userExists_returnsTrue() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        boolean result = usuarioService.deleteUserInternal(1L);

        assertTrue(result);
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void deleteUserInternal_userNotFound_returnsFalse() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        boolean result = usuarioService.deleteUserInternal(99L);

        assertFalse(result);
        verify(usuarioRepository, never()).deleteById(any());
    }

    @Test
    void getUserByIdInternal_userFound_returnsDTO() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioDTO result = usuarioService.getUserByIdInternal(1L);

        assertNotNull(result);
        assertEquals("Juan", result.getNombre());
    }

    @Test
    void getUserByIdInternal_userNotFound_returnsNull() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        UsuarioDTO result = usuarioService.getUserByIdInternal(99L);

        assertNull(result);
    }

    @Test
    void buscarPorDni_returnsListWithPasswordAndIdRolNull() {
        when(usuarioRepository.findByDniStartingWithAndRolCliente("123")).thenReturn(List.of(usuario));

        Response response = usuarioService.buscarPorDni("123");

        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        List<UsuarioDTO> lista = (List<UsuarioDTO>) response.getData();
        assertEquals(1, lista.size());
        assertNull(lista.get(0).getPassword());
        assertNull(lista.get(0).getIdRol());
    }

    @Test
    void getClienteDetalle_clienteFound_returns200() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(turnoRepository.countTurnosByClienteAndEstados(1L)).thenReturn(new ArrayList<>());

        Response response = usuarioService.getClienteDetalle(1L);

        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        assertInstanceOf(ClienteDetalleDTO.class, response.getData());
        ClienteDetalleDTO detalle = (ClienteDetalleDTO) response.getData();
        assertEquals("Juan", detalle.getNombre());
        assertNotNull(detalle.getDireccion());
    }

    @Test
    void getClienteDetalle_clienteNotFound_returns404() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Response response = usuarioService.getClienteDetalle(99L);

        assertEquals(404, response.getStatusCode());
        assertEquals("Cliente no encontrado", response.getMessage());
    }

    @Test
    void logout_tokenAlreadyBlacklisted_returnsMessage() {
        when(tokenBlacklistedRepository.findByToken("existing-token"))
            .thenReturn(Optional.of(new TokenBlacklisted()));

        MensajeResponse result = usuarioService.logout("existing-token");

        assertEquals("La sesión ya fue cerrada", result.getMessage());
        verify(jwtUtils, never()).extractExpiration(any());
        verify(tokenBlacklistedRepository, never()).save(any());
    }

    @Test
    void logout_newToken_blacklistsSuccessfully() {
        when(tokenBlacklistedRepository.findByToken("valid-token")).thenReturn(Optional.empty());
        when(jwtUtils.extractExpiration("valid-token")).thenReturn(new Date());
        when(tokenBlacklistedRepository.save(any(TokenBlacklisted.class))).thenAnswer(i -> i.getArgument(0));

        MensajeResponse result = usuarioService.logout("valid-token");

        assertEquals("Sesión cerrada exitosamente", result.getMessage());
        verify(jwtUtils).extractExpiration("valid-token");
        verify(tokenBlacklistedRepository).save(any(TokenBlacklisted.class));
    }
}
