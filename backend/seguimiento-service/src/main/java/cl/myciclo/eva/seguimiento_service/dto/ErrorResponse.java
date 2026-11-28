package cl.myciclo.eva.seguimiento_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public record ErrorResponse(
        @Schema(example = "REGISTRO_NO_ENCONTRADO")
        String codigo,

        @Schema(example = "El registro solicitado no existe o no pertenece al usuario autenticado.")
        String mensaje,

        @Schema(example = "{}")
        Map<String, String> campos
) {
}
