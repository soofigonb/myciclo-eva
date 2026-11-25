package cl.myciclo.eva.ui.screens.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RegistroEstado(
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val confirmacion: String = "",
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorConfirmacion: String? = null,
    val mostrarAviso: Boolean = false,
    val validacionActivada: Boolean = false
)

class RegistroViewModel : ViewModel() {

    private val _estado = MutableStateFlow(RegistroEstado())
    val estado = _estado.asStateFlow()

    fun cambiarNombre(valor: String) {
        actualizar(_estado.value.copy(nombre = valor))
    }

    fun cambiarCorreo(valor: String) {
        actualizar(_estado.value.copy(correo = valor))
    }

    fun cambiarContrasena(valor: String) {
        actualizar(_estado.value.copy(contrasena = valor))
    }

    fun cambiarConfirmacion(valor: String) {
        actualizar(_estado.value.copy(confirmacion = valor))
    }

    private fun actualizar(nuevo: RegistroEstado) {
        val actualizado = nuevo.copy(mostrarAviso = false)

        _estado.value = if (actualizado.validacionActivada) {
            validar(actualizado)
        } else {
            actualizado
        }
    }

    private fun validar(datos: RegistroEstado): RegistroEstado {
        return datos.copy(
            errorNombre = if (datos.nombre.isBlank()) {
                "Ingresa tu nombre."
            } else {
                null
            },
            errorCorreo = validarCorreo(datos.correo),
            errorContrasena = validarContrasena(
                datos.contrasena,
                exigirLargo = true
            ),
            errorConfirmacion = when {
                datos.confirmacion.isBlank() ->
                    "Confirma tu contraseña."
                datos.confirmacion != datos.contrasena ->
                    "Las contraseñas no coinciden."
                else -> null
            }
        )
    }

    fun crearCuenta() {
        val validado = validar(
            _estado.value.copy(validacionActivada = true)
        )

        val formularioValido =
            validado.errorNombre == null &&
                    validado.errorCorreo == null &&
                    validado.errorContrasena == null &&
                    validado.errorConfirmacion == null

        _estado.value = validado.copy(
            mostrarAviso = formularioValido
        )
    }

    fun cerrarAviso() {
        _estado.value = _estado.value.copy(
            mostrarAviso = false
        )
    }
}