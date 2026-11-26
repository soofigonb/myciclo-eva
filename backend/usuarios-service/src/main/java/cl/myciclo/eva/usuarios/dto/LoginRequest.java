package cl.myciclo.eva.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(example = "nitsi@ejemplo.cl")
        @NotBlank(message = "Ingresa tu correo electrónico.")
        @Email(message = "Ingresa un correo electrónico válido.")
        String correo,

        @Schema(example = "ClaveSegura123")
        @NotBlank(message = "Ingresa tu contraseña.")
        String contrasena
) {
    @Override
    public String toString() {
        return "LoginRequest[correo=" + correo + "]";
    }
}
