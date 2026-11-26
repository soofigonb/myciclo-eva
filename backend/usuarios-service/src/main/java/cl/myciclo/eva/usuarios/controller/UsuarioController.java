package cl.myciclo.eva.usuarios.controller;

import cl.myciclo.eva.usuarios.dto.ActualizarPerfilRequest;
import cl.myciclo.eva.usuarios.dto.LoginRequest;
import cl.myciclo.eva.usuarios.dto.LoginResponse;
import cl.myciclo.eva.usuarios.dto.PerfilResponse;
import cl.myciclo.eva.usuarios.dto.RegistroRequest;
import cl.myciclo.eva.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "Registro, autenticación y perfil de usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar nueva cuenta")
    public ResponseEntity<PerfilResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        PerfilResponse respuesta = usuarioService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse respuesta = usuarioService.iniciarSesion(request);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/perfil")
    @Operation(summary = "Consultar perfil autenticado", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PerfilResponse> obtenerPerfil(Authentication authentication) {
        Long usuarioId = extraerUsuarioId(authentication);
        return ResponseEntity.ok(usuarioService.obtenerPerfil(usuarioId));
    }

    @PutMapping("/perfil")
    @Operation(summary = "Actualizar perfil autenticado", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PerfilResponse> actualizarPerfil(
            Authentication authentication,
            @Valid @RequestBody ActualizarPerfilRequest request
    ) {
        Long usuarioId = extraerUsuarioId(authentication);
        return ResponseEntity.ok(usuarioService.actualizarPerfil(usuarioId, request));
    }

    private Long extraerUsuarioId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("Sesión no autenticada.");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long id) {
            return id;
        }
        return Long.parseLong(principal.toString());
    }
}
