package cl.myciclo.eva.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegistroRequest(
        @Schema(example = "Nitsi Sepúlveda")
        @NotBlank(message = "Ingresa tu nombre.")
        @Size(max = 120, message = "El nombre no puede superar los 120 caracteres.")
        String nombre,

        @Schema(example = "nitsi@ejemplo.cl")
        @NotBlank(message = "Ingresa tu correo electrónico.")
        @Email(message = "Ingresa un correo electrónico válido.")
        @Size(max = 255, message = "El correo no puede superar los 255 caracteres.")
        String correo,

        @Schema(example = "ClaveSegura123")
        @NotBlank(message = "Ingresa una contraseña.")
        @Size(min = 8, max = 100, message = "La contraseña debe tener al menos 8 caracteres.")
        String contrasena,

        @Schema(example = "1998-05-14", nullable = true)
        @PastOrPresent(message = "La fecha de nacimiento no puede ser futura.")
        LocalDate fechaNacimiento
) {
    @Override
    public String toString() {
        return "RegistroRequest[nombre=" + nombre + ", correo=" + correo
                + ", fechaNacimiento=" + fechaNacimiento + "]";
    }
}
