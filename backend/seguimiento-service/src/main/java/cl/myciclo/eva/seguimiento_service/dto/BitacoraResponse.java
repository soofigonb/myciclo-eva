package cl.myciclo.eva.seguimiento_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record BitacoraResponse(
        @Schema(description = "Identificador único del registro", example = "1")
        Long id,

        @Schema(description = "Identificador del usuario propietario", example = "10")
        Long usuarioId,

        @Schema(description = "Fecha del registro diario", example = "2026-11-28")
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
