package cl.myciclo.eva.seguimiento_service.controller;

import cl.myciclo.eva.seguimiento_service.dto.BitacoraRequest;
import cl.myciclo.eva.seguimiento_service.dto.BitacoraResponse;
import cl.myciclo.eva.seguimiento_service.service.BitacoraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seguimiento/bitacoras")
@Tag(name = "Bitácoras", description = "Endpoints para consultar, registrar, modificar y eliminar bitácoras diarias de síntomas y estado")
public class BitacoraController {

    private final BitacoraService service;

    public BitacoraController(BitacoraService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar bitácoras", description = "Retorna todas las bitácoras diarias registradas por el usuario autenticado, ordenadas por fecha descendente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de bitácoras obtenida correctamente"),
            @ApiResponse(responseCode = "401", description = "Token JWT faltante o inválido")
    })
    public ResponseEntity<List<BitacoraResponse>> listar(Authentication authentication) {
        Long usuarioId = extraerUsuarioId(authentication);
        return ResponseEntity.ok(service.listarPorUsuario(usuarioId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener bitácora por ID", description = "Obtiene los detalles de una bitácora específica perteneciente al usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bitácora encontrada"),
            @ApiResponse(responseCode = "401", description = "Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Bitácora no encontrada o pertenece a otra cuenta")
    })
    public ResponseEntity<BitacoraResponse> obtenerPorId(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long usuarioId = extraerUsuarioId(authentication);
        return ResponseEntity.ok(service.obtenerPorId(id, usuarioId));
    }

    @PostMapping
    @Operation(summary = "Registrar bitácora diaria", description = "Crea un nuevo registro diario de síntomas, flujo, estado de ánimo y notas para el usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Bitácora creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de petición inválidos"),
            @ApiResponse(responseCode = "401", description = "Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "409", description = "Ya existe una bitácora para esa fecha")
    })
    public ResponseEntity<BitacoraResponse> crear(
            @Valid @RequestBody BitacoraRequest request,
            Authentication authentication
    ) {
        Long usuarioId = extraerUsuarioId(authentication);
        BitacoraResponse respuesta = service.crear(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar bitácora", description = "Modifica los datos de una bitácora existente perteneciente al usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bitácora actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de petición inválidos"),
            @ApiResponse(responseCode = "401", description = "Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Bitácora no encontrada o pertenece a otra cuenta"),
            @ApiResponse(responseCode = "409", description = "Conflicto con otra bitácora para la misma fecha")
    })
    public ResponseEntity<BitacoraResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody BitacoraRequest request,
            Authentication authentication
    ) {
        Long usuarioId = extraerUsuarioId(authentication);
        return ResponseEntity.ok(service.actualizar(id, usuarioId, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar bitácora", description = "Elimina una bitácora existente perteneciente al usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Bitácora eliminada exitosamente"),
            @ApiResponse(responseCode = "401", description = "Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Bitácora no encontrada o pertenece a otra cuenta")
    })
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long usuarioId = extraerUsuarioId(authentication);
        service.eliminar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    private Long extraerUsuarioId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long id) {
            return id;
        }
        return Long.parseLong(authentication.getName());
    }
}
