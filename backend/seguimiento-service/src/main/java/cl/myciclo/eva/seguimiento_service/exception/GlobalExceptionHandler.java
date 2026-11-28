package cl.myciclo.eva.seguimiento_service.exception;

import cl.myciclo.eva.seguimiento_service.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegistroNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarRegistroNoEncontrado(RegistroNoEncontradoException ex) {
        ErrorResponse body = new ErrorResponse(
                "REGISTRO_NO_ENCONTRADO",
                ex.getMessage() != null ? ex.getMessage() : "El registro solicitado no existe o no pertenece al usuario autenticado.",
                Collections.emptyMap()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        ErrorResponse body = new ErrorResponse(
                "VALIDACION_ERROR",
                "Revisa los datos enviados.",
                campos
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarJsonInvalido(HttpMessageNotReadableException ex) {
        ErrorResponse body = new ErrorResponse(
                "SOLICITUD_INVALIDA",
                "El cuerpo JSON o el formato de fecha (YYYY-MM-DD) no es válido.",
                Collections.emptyMap()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> manejarViolacionIntegridad(DataIntegrityViolationException ex) {
        ErrorResponse body = new ErrorResponse(
                "REGISTRO_DUPLICADO",
                "Ya existe un registro para esta fecha y usuario.",
                Collections.emptyMap()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> manejarArgumentoInvalido(IllegalArgumentException ex) {
        ErrorResponse body = new ErrorResponse(
                "SOLICITUD_INVALIDA",
                ex.getMessage() != null ? ex.getMessage() : "Solicitud inválida.",
                Collections.emptyMap()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarErrorGenerico(Exception ex) {
        ErrorResponse body = new ErrorResponse(
                "ERROR_INTERNO",
                "Ha ocurrido un error inesperado en el servidor.",
                Collections.emptyMap()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
