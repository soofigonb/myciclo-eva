package cl.myciclo.eva.ui.screens.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Validaciones compartidas por Login y Registro.
internal fun validarCorreo(correo: String): String? {
    return when {
        correo.isBlank() -> "Ingresa tu correo electrónico."
        !Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() ->
            "Ingresa un correo válido."
        else -> null
    }
}

internal fun validarContrasena(
    contrasena: String,
    exigirLargo: Boolean
): String? {
    return when {
        contrasena.isBlank() -> "Ingresa una contraseña."
        exigirLargo && contrasena.length < 8 ->
            "Utiliza al menos 8 caracteres."
        else -> null
    }
}

data class LoginEstado(
    val correo: String = "",
    val contrasena: String = "",
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val mostrarAviso: Boolean = false,
    val validacionActivada: Boolean = false
)

class LoginViewModel : ViewModel() {

    // Los datos permanecen en memoria; no se guardan contraseñas en Room.
    private val _estado = MutableStateFlow(LoginEstado())
    val estado = _estado.asStateFlow()

    fun cambiarCorreo(valor: String) {
        val actual = _estado.value

        _estado.value = actual.copy(
            correo = valor,
            errorCorreo = if (actual.validacionActivada) {
                validarCorreo(valor)
            } else {
                null
            },
            mostrarAviso = false
        )
    }

    fun cambiarContrasena(valor: String) {
        val actual = _estado.value

        _estado.value = actual.copy(
            contrasena = valor,
            errorContrasena = if (actual.validacionActivada) {
                validarContrasena(valor, exigirLargo = false)
            } else {
                null
            },
            mostrarAviso = false
        )
    }

    fun iniciarSesion() {
        val actual = _estado.value
        val errorCorreo = validarCorreo(actual.correo)
        val errorContrasena = validarContrasena(
            actual.contrasena,
            exigirLargo = false
        )

        _estado.value = actual.copy(
            validacionActivada = true,
            errorCorreo = errorCorreo,
            errorContrasena = errorContrasena,
            mostrarAviso = errorCorreo == null &&
                    errorContrasena == null
        )
    }

    fun cerrarAviso() {
        _estado.value = _estado.value.copy(
            mostrarAviso = false
        )
    }
}