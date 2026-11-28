package cl.myciclo.eva.seguimiento_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record InicioMenstrualRequest(
        @Schema(description = "Fecha de inicio del ciclo en formato YYYY-MM-DD", example = "2026-11-28", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "La fecha es obligatoria.")
        LocalDate fecha
) {
}
