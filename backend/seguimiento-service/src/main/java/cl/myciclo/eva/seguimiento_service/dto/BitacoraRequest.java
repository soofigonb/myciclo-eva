package cl.myciclo.eva.seguimiento_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BitacoraRequest(
        @Schema(description = "Fecha del registro diario en formato YYYY-MM-DD", example = "2026-11-28", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "La fecha es obligatoria.")
        LocalDate fecha,

        @Schema(description = "Patrón menstrual observado", example = "regular")
        String patron,

        @Schema(description = "Síntomas experimentados", example = "cólicos leves, fatiga")
        String sintomas,

        @Schema(description = "Estado de ánimo", example = "tranquila")
        String estadoAnimo,

        @Schema(description = "Nivel o tipo de flujo menstrual", example = "moderado")
        String flujoMenstrual,

        @Schema(description = "Tipo de flujo vaginal", example = "acuoso")
        String flujoVaginal,

        @Schema(description = "Notas adicionales", example = "Tomé un té caliente y descansé.")
        String notas
) {
}
