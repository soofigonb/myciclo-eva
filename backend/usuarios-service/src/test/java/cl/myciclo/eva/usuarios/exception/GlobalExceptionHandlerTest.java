package cl.myciclo.eva.usuarios.exception;

import cl.myciclo.eva.usuarios.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void manejarValidacion() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objeto");
        bindingResult.addError(new FieldError("objeto", "correo", "Correo inválido"));
        MethodParameter parameter = new MethodParameter(this.getClass().getDeclaredMethod("setUp"), -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponse> respuesta = handler.manejarValidacion(ex);
        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals("VALIDACION_ERROR", respuesta.getBody().codigo());
        assertEquals("Correo inválido", respuesta.getBody().campos().get("correo"));
    }

    @Test
    void manejarCredencialesInvalidas() {
        CredencialesInvalidasException ex = new CredencialesInvalidasException("Credenciales malas");
        ResponseEntity<ErrorResponse> respuesta = handler.manejarCredencialesInvalidas(ex);
        assertEquals(HttpStatus.UNAUTHORIZED, respuesta.getStatusCode());
        assertEquals("CREDENCIALES_INVALIDAS", respuesta.getBody().codigo());
    }

    @Test
    void manejarUsuarioNoEncontrado() {
        UsuarioNoEncontradoException ex = new UsuarioNoEncontradoException("Usuario 1 no existe");
        ResponseEntity<ErrorResponse> respuesta = handler.manejarUsuarioNoEncontrado(ex);
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertEquals("USUARIO_NO_ENCONTRADO", respuesta.getBody().codigo());
    }

    @Test
    void manejarCorreoDuplicado() {
        CorreoYaRegistradoException ex = new CorreoYaRegistradoException("Correo ya existe");
        ResponseEntity<ErrorResponse> respuesta = handler.manejarCorreoDuplicado(ex);
        assertEquals(HttpStatus.CONFLICT, respuesta.getStatusCode());
        assertEquals("CORREO_DUPLICADO", respuesta.getBody().codigo());
    }

    @Test
    void manejarJsonInvalido() {
        MockHttpInputMessage inputMessage = new MockHttpInputMessage("{}".getBytes(StandardCharsets.UTF_8));
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON corrupto", inputMessage);
        ResponseEntity<ErrorResponse> respuesta = handler.manejarJsonInvalido(ex);
        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals("SOLICITUD_INVALIDA", respuesta.getBody().codigo());
    }

    @Test
    void manejarArgumentoInvalido() {
        IllegalArgumentException ex = new IllegalArgumentException("Dato inválido");
        ResponseEntity<ErrorResponse> respuesta = handler.manejarArgumentoInvalido(ex);
        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
    }
}
