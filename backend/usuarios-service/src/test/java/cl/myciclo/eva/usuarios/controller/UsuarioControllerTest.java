package cl.myciclo.eva.usuarios.controller;

import cl.myciclo.eva.usuarios.dto.ActualizarPerfilRequest;
import cl.myciclo.eva.usuarios.dto.LoginRequest;
import cl.myciclo.eva.usuarios.dto.LoginResponse;
import cl.myciclo.eva.usuarios.dto.PerfilResponse;
import cl.myciclo.eva.usuarios.dto.RegistroRequest;
import cl.myciclo.eva.usuarios.exception.CorreoYaRegistradoException;
import cl.myciclo.eva.usuarios.exception.CredencialesInvalidasException;
import cl.myciclo.eva.usuarios.exception.GlobalExceptionHandler;
import cl.myciclo.eva.usuarios.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private PerfilResponse perfilEsperado;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        perfilEsperado = new PerfilResponse(
                1L,
                "Nitsi Sepúlveda",
                "nitsi@ejemplo.cl",
                LocalDate.of(1998, 5, 14)
        );
        authentication = new UsernamePasswordAuthenticationToken(1L, null, Collections.emptyList());
    }

    @Test
    void registro_retorna_201_creado() {
        RegistroRequest request = new RegistroRequest(
                "Nitsi Sepúlveda",
                "nitsi@ejemplo.cl",
                "ClaveSegura123",
                LocalDate.of(1998, 5, 14)
        );

        when(usuarioService.registrar(any(RegistroRequest.class))).thenReturn(perfilEsperado);

        ResponseEntity<PerfilResponse> respuesta = usuarioController.registrar(request);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("Nitsi Sepúlveda", respuesta.getBody().nombre());
        assertEquals("nitsi@ejemplo.cl", respuesta.getBody().correo());
        verify(usuarioService).registrar(request);
    }

    @Test
    void registro_lanza_excepcion_por_correo_repetido() {
        RegistroRequest request = new RegistroRequest(
                "Nitsi Sepúlveda",
                "repetido@ejemplo.cl",
                "ClaveSegura123",
                null
        );

        when(usuarioService.registrar(any(RegistroRequest.class)))
                .thenThrow(new CorreoYaRegistradoException("Ya existe una cuenta registrada con ese correo."));

        assertThrows(CorreoYaRegistradoException.class, () -> usuarioController.registrar(request));
    }

    @Test
    void login_retorna_200_con_token() {
        LoginRequest request = new LoginRequest("nitsi@ejemplo.cl", "ClaveSegura123");
        LoginResponse respuestaEsperada = new LoginResponse(
                "token_jwt_simulado",
                "Bearer",
                86400L,
                perfilEsperado
        );

        when(usuarioService.iniciarSesion(any(LoginRequest.class))).thenReturn(respuestaEsperada);

        ResponseEntity<LoginResponse> respuesta = usuarioController.login(request);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("token_jwt_simulado", respuesta.getBody().token());
        assertEquals("Bearer", respuesta.getBody().tipoToken());
        assertEquals(1L, respuesta.getBody().usuario().id());
    }

    @Test
    void login_lanza_excepcion_por_credenciales_invalidas() {
        LoginRequest request = new LoginRequest("nitsi@ejemplo.cl", "ClaveMala");

        when(usuarioService.iniciarSesion(any(LoginRequest.class)))
                .thenThrow(new CredencialesInvalidasException("Correo o contraseña incorrectos."));

        assertThrows(CredencialesInvalidasException.class, () -> usuarioController.login(request));
    }

    @Test
    void obtenerPerfil_retorna_200_perfil_autenticado() {
        when(usuarioService.obtenerPerfil(1L)).thenReturn(perfilEsperado);

        ResponseEntity<PerfilResponse> respuesta = usuarioController.obtenerPerfil(authentication);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(1L, respuesta.getBody().id());
        assertEquals("Nitsi Sepúlveda", respuesta.getBody().nombre());
    }

    @Test
    void obtenerPerfil_falla_si_sesion_no_autenticada() {
        assertThrows(IllegalStateException.class, () -> usuarioController.obtenerPerfil(null));
    }

    @Test
    void actualizarPerfil_retorna_200_actualizado() {
        ActualizarPerfilRequest request = new ActualizarPerfilRequest(
                "Nitsi Actualizada",
                LocalDate.of(1998, 5, 14)
        );
        PerfilResponse perfilModificado = new PerfilResponse(
                1L,
                "Nitsi Actualizada",
                "nitsi@ejemplo.cl",
                LocalDate.of(1998, 5, 14)
        );

        when(usuarioService.actualizarPerfil(1L, request)).thenReturn(perfilModificado);

        ResponseEntity<PerfilResponse> respuesta = usuarioController.actualizarPerfil(authentication, request);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("Nitsi Actualizada", respuesta.getBody().nombre());
        verify(usuarioService).actualizarPerfil(1L, request);
    }
}
