package cl.myciclo.eva.seguimiento_service.controller;

import cl.myciclo.eva.seguimiento_service.dto.InicioMenstrualRequest;
import cl.myciclo.eva.seguimiento_service.dto.InicioMenstrualResponse;
import cl.myciclo.eva.seguimiento_service.service.InicioMenstrualService;
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
@RequestMapping("/api/v1/seguimiento/inicios-menstruales")
@Tag(name = "Inicios Menstruales", description = "Endpoints para consultar, registrar, modificar y eliminar inicios de ciclo menstrual")
public class InicioMenstrualController {

    private final InicioMenstrualService service;

    public InicioMenstrualController(InicioMenstrualService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar inicios menstruales", description = "Retorna todos los inicios menstruales registrados por el usuario autenticado, ordenados por fecha descendente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de inicios menstruales obtenida correctamente"),
            @ApiResponse(responseCode = "401", description = "Token JWT faltante o inválido")
    })
    public ResponseEntity<List<InicioMenstrualResponse>> listar(Authentication authentication) {
        Long usuarioId = extraerUsuarioId(authentication);
        return ResponseEntity.ok(service.listarPorUsuario(usuarioId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener inicio menstrual por ID", description = "Obtiene los detalles de un inicio menstrual específico perteneciente al usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado o pertenece a otra cuenta")
    })
    public ResponseEntity<InicioMenstrualResponse> obtenerPorId(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long usuarioId = extraerUsuarioId(authentication);
        return ResponseEntity.ok(service.obtenerPorId(id, usuarioId));
    }

    @PostMapping
    @Operation(summary = "Registrar inicio menstrual", description = "Registra una nueva fecha de inicio de ciclo menstrual para el usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Inicio menstrual registrado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de petición inválidos"),
            @ApiResponse(responseCode = "401", description = "Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "409", description = "Ya existe un registro para esa fecha")
    })
    public ResponseEntity<InicioMenstrualResponse> crear(
            @Valid @RequestBody InicioMenstrualRequest request,
            Authentication authentication
    ) {
        Long usuarioId = extraerUsuarioId(authentication);
        InicioMenstrualResponse respuesta = service.crear(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar inicio menstrual", description = "Modifica la fecha de un inicio menstrual existente perteneciente al usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de petición inválidos"),
            @ApiResponse(responseCode = "401", description = "Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado o pertenece a otra cuenta"),
            @ApiResponse(responseCode = "409", description = "Fecha en conflicto con otro registro")
    })
    public ResponseEntity<InicioMenstrualResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody InicioMenstrualRequest request,
            Authentication authentication
    ) {
        Long usuarioId = extraerUsuarioId(authentication);
        return ResponseEntity.ok(service.actualizar(id, usuarioId, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar inicio menstrual", description = "Elimina un inicio menstrual existente perteneciente al usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Registro eliminado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado o pertenece a otra cuenta")
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
