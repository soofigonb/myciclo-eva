package cl.myciclo.eva.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public record ErrorResponse(
        @Schema(example = "VALIDACION_ERROR")
        String codigo,

        @Schema(example = "Revisa los datos enviados.")
        String mensaje,

        @Schema(example = "{\"correo\": \"Ingresa un correo electrónico válido.\"}")
        Map<String, String> campos
) {
}
