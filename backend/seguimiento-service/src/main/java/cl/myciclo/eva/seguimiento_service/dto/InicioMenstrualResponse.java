package cl.myciclo.eva.seguimiento_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record InicioMenstrualResponse(
        @Schema(description = "Identificador único del registro", example = "1")
        Long id,

        @Schema(description = "Identificador del usuario propietario", example = "10")
        Long usuarioId,

        @Schema(description = "Fecha del inicio menstrual", example = "2026-11-28")
        LocalDate fecha
) {
}
