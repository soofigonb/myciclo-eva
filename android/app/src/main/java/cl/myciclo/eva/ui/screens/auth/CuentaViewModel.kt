package cl.myciclo.eva.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.myciclo.eva.data.local.SesionManager
import cl.myciclo.eva.data.repository.AuthRepository
import cl.myciclo.eva.data.repository.UsuarioPerfil
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

data class CuentaEstado(
    val iniciando: Boolean = true,
    val usuarioId: Long? = null,
    val perfil: UsuarioPerfil? = null,
    val procesando: Boolean = false,
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val confirmacion: String = "",
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorConfirmacion: String? = null,
    val errorNombrePerfil: String? = null,
    val errorFechaNacimiento: String? = null,
    val perfilGuardado: Int = 0,
    val error: String? = null,
    val mensaje: String? = null
)

class CuentaViewModel(
    private val repository: AuthRepository,
    private val sesion: SesionManager
) : ViewModel() {

    private val _estado = MutableStateFlow(CuentaEstado())
    val estado = _estado.asStateFlow()

    init {
        observarSesion()
    }

    private fun observarSesion() {
        viewModelScope.launch {
            try {
                sesion.usuarioId.collectLatest { id ->
                    _estado.value = _estado.value.copy(
                        iniciando = true,
                        perfil = null
                    )

                    if (id == null || id == 0L) {
                        _estado.value = CuentaEstado(
                            iniciando = false,
                            usuarioId = id
                        )
                    } else {
                        val perfil = repository.obtenerPerfil(id)

                        if (perfil == null) {
                            sesion.cerrarSesion()
                        } else {
                            _estado.value = CuentaEstado(
                                iniciando = false,
                                usuarioId = id,
                                perfil = perfil
                            )

                            repository.observarPerfil(id).collect { actualizado ->
                                if (actualizado == null) {
                                    sesion.cerrarSesion()
                                } else {
                                    _estado.value = _estado.value.copy(
                                        perfil = actualizado
                                    )
                                }
                            }
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = CuentaEstado(
                    iniciando = false,
                    error = "No se pudo recuperar la sesión. " +
                            "Intenta iniciar sesión."
                )
            }
        }
    }

    fun prepararFormulario() {
        if (_estado.value.procesando) return

        _estado.value = _estado.value.copy(
            nombre = "",
            correo = "",
            contrasena = "",
            confirmacion = "",
            errorNombre = null,
            errorCorreo = null,
            errorContrasena = null,
            errorConfirmacion = null,
            error = null,
            mensaje = null
        )
    }

    fun cambiarNombre(valor: String) {
        if (_estado.value.procesando) return

        _estado.value = _estado.value.copy(
            nombre = valor,
            errorNombre = null,
            error = null
        )
    }

    fun cambiarCorreo(valor: String) {
        if (_estado.value.procesando) return

        _estado.value = _estado.value.copy(
            correo = valor,
            errorCorreo = null,
            error = null
        )
    }

    fun cambiarContrasena(valor: String) {
        if (_estado.value.procesando) return

        _estado.value = _estado.value.copy(
            contrasena = valor,
            errorContrasena = null,
            errorConfirmacion = null,
            error = null
        )
    }

    fun cambiarConfirmacion(valor: String) {
        if (_estado.value.procesando) return

        _estado.value = _estado.value.copy(
            confirmacion = valor,
            errorConfirmacion = null,
            error = null
        )
    }

    fun enviarFormulario(registro: Boolean) {
        val actual = _estado.value
        if (actual.procesando) return

        val nombreError = if (registro && actual.nombre.isBlank()) {
            "Ingresa tu nombre."
        } else {
            null
        }

        val correoError = validarCorreo(actual.correo)

        val contrasenaError = validarContrasena(
            actual.contrasena,
            exigirLargo = registro
        )

        val confirmacionError = if (registro) {
            when {
                actual.confirmacion.isBlank() ->
                    "Confirma tu contraseña."

                actual.confirmacion != actual.contrasena ->
                    "Las contraseñas no coinciden."

                else -> null
            }
        } else {
            null
        }

        val valido =
            nombreError == null &&
                    correoError == null &&
                    contrasenaError == null &&
                    confirmacionError == null

        _estado.value = actual.copy(
            errorNombre = nombreError,
            errorCorreo = correoError,
            errorContrasena = contrasenaError,
            errorConfirmacion = confirmacionError,
            error = null,
            mensaje = null,
            procesando = valido
        )

        if (!valido) return

        viewModelScope.launch {
            try {
                val perfil = if (registro) {
                    repository.registrar(
                        nombre = actual.nombre,
                        correo = actual.correo,
                        contrasena = actual.contrasena,
                        confirmacion = actual.confirmacion
                    )
                } else {
                    repository.iniciarSesion(
                        correo = actual.correo,
                        contrasena = actual.contrasena
                    )
                }

                sesion.iniciarSesion(perfil.id)
            } catch (e: CancellationException) {
                _estado.value = _estado.value.copy(
                    procesando = false
                )
                throw e
            } catch (e: IllegalArgumentException) {
                _estado.value = _estado.value.copy(
                    procesando = false,
                    error = e.message ?: "Revisa los datos."
                )
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    procesando = false,
                    error = if (registro) {
                        "No se pudo completar el registro. " +
                                "Si la cuenta se creó, intenta iniciar sesión."
                    } else {
                        "No se pudo iniciar sesión. Intenta nuevamente."
                    }
                )
            }
        }
    }

    fun explorar() {
        operarSesion {
            sesion.entrarComoInvitada()
        }
    }

    fun cerrarSesion() {
        operarSesion {
            sesion.cerrarSesion()
        }
    }

    private fun operarSesion(
        operacion: suspend () -> Unit
    ) {
        if (_estado.value.procesando) return

        _estado.value = _estado.value.copy(
            procesando = true,
            error = null,
            mensaje = null
        )

        viewModelScope.launch {
            try {
                operacion()
            } catch (e: CancellationException) {
                _estado.value = _estado.value.copy(
                    procesando = false
                )
                throw e
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    procesando = false,
                    error = "No se pudo cambiar la sesión. " +
                            "Intenta nuevamente."
                )
            }
        }
    }

    // Compatibilidad con la pantalla anterior.
    fun guardarNombre(nombre: String) {
        val id = _estado.value.usuarioId ?: return

        if (id <= 0L || _estado.value.procesando) return

        if (nombre.isBlank()) {
            _estado.value = _estado.value.copy(
                error = "Ingresa tu nombre.",
                mensaje = null
            )
            return
        }

        _estado.value = _estado.value.copy(
            procesando = true,
            error = null,
            mensaje = null
        )

        viewModelScope.launch {
            try {
                repository.actualizarNombre(
                    id = id,
                    nombre = nombre
                )

                _estado.value = _estado.value.copy(
                    procesando = false,
                    mensaje = "Nombre actualizado."
                )
            } catch (e: CancellationException) {
                _estado.value = _estado.value.copy(
                    procesando = false
                )
                throw e
            } catch (e: IllegalArgumentException) {
                _estado.value = _estado.value.copy(
                    procesando = false,
                    error = e.message ?: "Revisa tu nombre."
                )
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    procesando = false,
                    error = "No se pudo actualizar el nombre."
                )
            }
        }
    }

    fun limpiarErroresPerfil() {
        _estado.value = _estado.value.copy(
            errorNombrePerfil = null,
            errorFechaNacimiento = null,
            error = null,
            mensaje = null
        )
    }

    fun limpiarErrorNombrePerfil() {
        _estado.value = _estado.value.copy(
            errorNombrePerfil = null,
            error = null,
            mensaje = null
        )
    }

    fun limpiarErrorFechaNacimiento() {
        _estado.value = _estado.value.copy(
            errorFechaNacimiento = null,
            error = null,
            mensaje = null
        )
    }

    fun guardarPerfil(
        nombre: String,
        fechaNacimiento: String,
        fotoPerfilPath: String?
    ) {
        val actual = _estado.value
        val id = actual.usuarioId ?: return

        if (id <= 0L || actual.procesando) return

        val nombreLimpio = nombre.trim()

        val errorNombre = if (nombreLimpio.isBlank()) {
            "Ingresa tu nombre."
        } else {
            null
        }

        val fecha = runCatching {
            LocalDate.parse(fechaNacimiento)
        }.getOrNull()

        val errorFecha = when {
            fechaNacimiento.isBlank() ->
                "Selecciona tu fecha de nacimiento."

            fecha == null ->
                "Selecciona una fecha válida."

            fecha.isAfter(LocalDate.now()) ->
                "La fecha de nacimiento no puede ser futura."

            else -> null
        }

        val valido = errorNombre == null && errorFecha == null

        _estado.value = actual.copy(
            errorNombrePerfil = errorNombre,
            errorFechaNacimiento = errorFecha,
            error = null,
            mensaje = null,
            procesando = valido
        )

        if (!valido) return

        viewModelScope.launch {
            try {
                val fechaGuardada = requireNotNull(fecha).toString()

                repository.actualizarPerfil(
                    id = id,
                    nombre = nombreLimpio,
                    fechaNacimiento = fechaGuardada,
                    fotoPerfilPath = fotoPerfilPath
                )

                _estado.value = _estado.value.copy(
                    procesando = false,
                    perfil = _estado.value.perfil?.copy(
                        nombre = nombreLimpio,
                        fechaNacimiento = fechaGuardada,
                        fotoPerfilPath = fotoPerfilPath
                    ),
                    errorNombrePerfil = null,
                    errorFechaNacimiento = null,
                    mensaje = "Perfil actualizado.",
                    perfilGuardado = _estado.value.perfilGuardado + 1
                )
            } catch (e: CancellationException) {
                _estado.value = _estado.value.copy(
                    procesando = false
                )
                throw e
            } catch (e: IllegalArgumentException) {
                _estado.value = _estado.value.copy(
                    procesando = false,
                    error = e.message ?: "Revisa los datos del perfil."
                )
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    procesando = false,
                    error = "No se pudo actualizar el perfil."
                )
            }
        }
    }
}