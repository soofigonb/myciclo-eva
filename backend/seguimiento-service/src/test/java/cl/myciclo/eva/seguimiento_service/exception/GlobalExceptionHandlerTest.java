package cl.myciclo.eva.seguimiento_service.exception;

import cl.myciclo.eva.seguimiento_service.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testManejarRegistroNoEncontrado() {
        RegistroNoEncontradoException ex = new RegistroNoEncontradoException("No existe el registro");
        ResponseEntity<ErrorResponse> response = handler.manejarRegistroNoEncontrado(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("REGISTRO_NO_ENCONTRADO", response.getBody().codigo());
        assertEquals("No existe el registro", response.getBody().mensaje());
    }

    @Test
    void testManejarViolacionIntegridad() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Duplicado");
        ResponseEntity<ErrorResponse> response = handler.manejarViolacionIntegridad(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("REGISTRO_DUPLICADO", response.getBody().codigo());
    }

    @Test
    void testManejarValidacion() throws Exception {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "request");
        bindingResult.addError(new FieldError("request", "fecha", "La fecha es obligatoria."));

        MethodParameter parameter = new MethodParameter(this.getClass().getDeclaredMethod("setUp"), -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.manejarValidacion(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("VALIDACION_ERROR", response.getBody().codigo());
        assertEquals("La fecha es obligatoria.", response.getBody().campos().get("fecha"));
    }

    @Test
    void testManejarJsonInvalido() {
        MockHttpInputMessage inputMessage = new MockHttpInputMessage("{}".getBytes(StandardCharsets.UTF_8));
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON roto", inputMessage);
        ResponseEntity<ErrorResponse> response = handler.manejarJsonInvalido(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("SOLICITUD_INVALIDA", response.getBody().codigo());
    }

    @Test
    void testManejarArgumentoInvalido() {
        IllegalArgumentException ex = new IllegalArgumentException("Parámetro erróneo");
        ResponseEntity<ErrorResponse> response = handler.manejarArgumentoInvalido(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("SOLICITUD_INVALIDA", response.getBody().codigo());
        assertEquals("Parámetro erróneo", response.getBody().mensaje());
    }

    @Test
    void testManejarErrorGenerico() {
        RuntimeException ex = new RuntimeException("Fallo inesperado");
        ResponseEntity<ErrorResponse> response = handler.manejarErrorGenerico(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("ERROR_INTERNO", response.getBody().codigo());
    }
}
