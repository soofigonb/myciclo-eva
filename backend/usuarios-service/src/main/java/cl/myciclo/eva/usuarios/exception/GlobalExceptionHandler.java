package cl.myciclo.eva.usuarios.exception;

import cl.myciclo.eva.usuarios.dto.ErrorResponse;
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> manejarArgumentoInvalido(IllegalArgumentException ex) {
        ErrorResponse body = new ErrorResponse(
                "SOLICITUD_INVALIDA",
                ex.getMessage() != null ? ex.getMessage() : "Solicitud inválida.",
                Collections.emptyMap()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponse> manejarCredencialesInvalidas(CredencialesInvalidasException ex) {
        ErrorResponse body = new ErrorResponse(
                "CREDENCIALES_INVALIDAS",
                ex.getMessage(),
                Collections.emptyMap()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarUsuarioNoEncontrado(UsuarioNoEncontradoException ex) {
        ErrorResponse body = new ErrorResponse(
                "USUARIO_NO_ENCONTRADO",
                ex.getMessage(),
                Collections.emptyMap()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler({CorreoYaRegistradoException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ErrorResponse> manejarCorreoDuplicado(RuntimeException ex) {
        String mensaje = ex instanceof CorreoYaRegistradoException
                ? ex.getMessage()
                : "Ya existe una cuenta registrada con ese correo.";
        ErrorResponse body = new ErrorResponse(
                "CORREO_DUPLICADO",
                mensaje,
                Map.of("correo", mensaje)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
