package cl.myciclo.eva.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record PerfilResponse(
        @Schema(example = "1")
        Long id,

        @Schema(example = "Nitsi Sepúlveda")
        String nombre,

        @Schema(example = "nitsi@ejemplo.cl")
        String correo,

        @Schema(example = "1998-05-14", nullable = true)
        LocalDate fechaNacimiento
) {
}
