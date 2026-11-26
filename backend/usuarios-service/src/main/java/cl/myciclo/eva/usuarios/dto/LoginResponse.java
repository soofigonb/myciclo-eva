package cl.myciclo.eva.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,

        @Schema(example = "Bearer")
        String tipoToken,

        @Schema(example = "86400")
        long expiraEnSegundos,

        PerfilResponse usuario
) {
}
