package cl.myciclo.eva.usuarios.controller;

import cl.myciclo.eva.usuarios.config.OpenApiConfig;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "Endpoints de autenticacion y gestion de perfil")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar nueva cuenta de usuario")
    public ResponseEntity<PerfilResponse> registrar(@Valid @RequestBody RegistroRequest solicitud) {
        PerfilResponse nuevo = usuarioService.registrar(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion y obtener token JWT")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest solicitud) {
        LoginResponse respuesta = usuarioService.iniciarSesion(solicitud);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/perfil")
    @Operation(
            summary = "Obtener datos del perfil autenticado",
            security = @SecurityRequirement(name = OpenApiConfig.ESQUEMA_BEARER)
    )
    public ResponseEntity<PerfilResponse> obtenerPerfil(@AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = Long.valueOf(jwt.getSubject());
        PerfilResponse perfil = usuarioService.obtenerPerfil(usuarioId);
        return ResponseEntity.ok(perfil);
    }

    @PutMapping("/perfil")
    @Operation(
            summary = "Actualizar datos del perfil autenticado",
            security = @SecurityRequirement(name = OpenApiConfig.ESQUEMA_BEARER)
    )
    public ResponseEntity<PerfilResponse> actualizarPerfil(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ActualizarPerfilRequest solicitud
    ) {
        Long usuarioId = Long.valueOf(jwt.getSubject());
        PerfilResponse perfil = usuarioService.actualizarPerfil(usuarioId, solicitud);
        return ResponseEntity.ok(perfil);
    }
}
