package cl.myciclo.eva.usuarios.service;

import cl.myciclo.eva.usuarios.config.JwtTokenService;
import cl.myciclo.eva.usuarios.dto.ActualizarPerfilRequest;
import cl.myciclo.eva.usuarios.dto.LoginRequest;
import cl.myciclo.eva.usuarios.dto.LoginResponse;
import cl.myciclo.eva.usuarios.dto.PerfilResponse;
import cl.myciclo.eva.usuarios.dto.RegistroRequest;
import cl.myciclo.eva.usuarios.exception.CorreoYaRegistradoException;
import cl.myciclo.eva.usuarios.exception.CredencialesInvalidasException;
import cl.myciclo.eva.usuarios.exception.UsuarioNoEncontradoException;
import cl.myciclo.eva.usuarios.model.Usuario;
import cl.myciclo.eva.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioPrueba;

    @BeforeEach
    void setUp() {
        usuarioPrueba = new Usuario(
                "Nitsi Sepúlveda",
                "nitsi@ejemplo.cl",
                "hash_encriptado",
                LocalDate.of(1998, 5, 14)
        );
        usuarioPrueba.setId(1L);
    }

    @Test
    void registrar_exitoso() {
        RegistroRequest request = new RegistroRequest(
                "Nitsi Sepúlveda",
                "nitsi@ejemplo.cl",
                "ClaveSegura123",
                LocalDate.of(1998, 5, 14)
        );

        when(usuarioRepository.existsByCorreo("nitsi@ejemplo.cl")).thenReturn(false);
        when(passwordEncoder.encode("ClaveSegura123")).thenReturn("hash_encriptado");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPrueba);

        PerfilResponse respuesta = usuarioService.registrar(request);

        assertNotNull(respuesta);
        assertEquals(1L, respuesta.id());
        assertEquals("Nitsi Sepúlveda", respuesta.nombre());
        assertEquals("nitsi@ejemplo.cl", respuesta.correo());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void registrar_falla_por_correo_repetido() {
        RegistroRequest request = new RegistroRequest(
                "Nitsi Sepúlveda",
                "nitsi@ejemplo.cl",
                "ClaveSegura123",
                null
        );

        when(usuarioRepository.existsByCorreo("nitsi@ejemplo.cl")).thenReturn(true);

        assertThrows(CorreoYaRegistradoException.class, () -> usuarioService.registrar(request));
    }

    @Test
    void iniciarSesion_exitoso() {
        LoginRequest request = new LoginRequest("nitsi@ejemplo.cl", "ClaveSegura123");

        when(usuarioRepository.findByCorreo("nitsi@ejemplo.cl")).thenReturn(Optional.of(usuarioPrueba));
        when(passwordEncoder.matches("ClaveSegura123", "hash_encriptado")).thenReturn(true);
        when(jwtTokenService.generarToken(1L, "nitsi@ejemplo.cl")).thenReturn("token_jwt_valido");
        when(jwtTokenService.getExpirationSeconds()).thenReturn(86400L);

        LoginResponse respuesta = usuarioService.iniciarSesion(request);

        assertNotNull(respuesta);
        assertEquals("token_jwt_valido", respuesta.token());
        assertEquals("Bearer", respuesta.tipoToken());
        assertEquals(86400L, respuesta.expiraEnSegundos());
        assertEquals(1L, respuesta.usuario().id());
    }

    @Test
    void iniciarSesion_falla_por_clave_incorrecta() {
        LoginRequest request = new LoginRequest("nitsi@ejemplo.cl", "ClaveErronea");

        when(usuarioRepository.findByCorreo("nitsi@ejemplo.cl")).thenReturn(Optional.of(usuarioPrueba));
        when(passwordEncoder.matches("ClaveErronea", "hash_encriptado")).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class, () -> usuarioService.iniciarSesion(request));
    }

    @Test
    void iniciarSesion_falla_por_usuario_no_existente() {
        LoginRequest request = new LoginRequest("noexiste@ejemplo.cl", "ClaveSegura123");

        when(usuarioRepository.findByCorreo(anyString())).thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class, () -> usuarioService.iniciarSesion(request));
    }

    @Test
    void obtenerPerfil_exitoso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPrueba));

        PerfilResponse respuesta = usuarioService.obtenerPerfil(1L);

        assertNotNull(respuesta);
        assertEquals(1L, respuesta.id());
        assertEquals("Nitsi Sepúlveda", respuesta.nombre());
    }

    @Test
    void obtenerPerfil_falla_cuando_no_existe() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.obtenerPerfil(99L));
    }

    @Test
    void actualizarPerfil_exitoso() {
        ActualizarPerfilRequest request = new ActualizarPerfilRequest(
                "Nitsi Actualizada",
                LocalDate.of(1998, 5, 14)
        );

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPrueba));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPrueba);

        PerfilResponse respuesta = usuarioService.actualizarPerfil(1L, request);

        assertNotNull(respuesta);
        assertEquals("Nitsi Actualizada", respuesta.nombre());
        verify(usuarioRepository).save(usuarioPrueba);
    }
}
