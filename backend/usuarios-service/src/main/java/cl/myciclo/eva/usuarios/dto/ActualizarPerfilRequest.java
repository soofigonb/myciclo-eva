package cl.myciclo.eva.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ActualizarPerfilRequest(
        @Schema(example = "Nitsi Sepúlveda")
        @NotBlank(message = "Ingresa tu nombre.")
        @Size(max = 120, message = "El nombre no puede superar los 120 caracteres.")
        String nombre,

        @Schema(example = "1998-05-14")
        @NotNull(message = "Selecciona tu fecha de nacimiento.")
        @PastOrPresent(message = "La fecha de nacimiento no puede ser futura.")
        LocalDate fechaNacimiento
) {
}
