package cl.myciclo.eva.ui.screens.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.myciclo.eva.data.repository.CicloRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

data class InicioEstado(
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    val ultimoInicio: String? = null,
    val diaDelCiclo: Int? = null,
    val fecha: String = LocalDate.now().toString(),
    val errorFecha: String? = null,
    val errorCarga: String? = null,
    val guardado: Boolean = false
)

class InicioViewModel(
    private val repository: CicloRepository
) : ViewModel() {
    private val _estado = MutableStateFlow(InicioEstado())
    val estado = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repository.todosLosInicios.collect { inicios ->
                    val hoy = LocalDate.now()
                    val ultimo = inicios
                        .mapNotNull {
                            runCatching {
                                LocalDate.parse(it.fechaIso)
                            }.getOrNull()
                        }
                        .filter { !it.isAfter(hoy) }
                        .maxOrNull()

                    _estado.value = _estado.value.copy(
                        cargando = false,
                        ultimoInicio = ultimo?.toString(),
                        diaDelCiclo = ultimo?.let {
                            ChronoUnit.DAYS.between(it, hoy).toInt() + 1
                        },
                        errorCarga = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    cargando = false,
                    errorCarga = "No se pudo cargar la información del ciclo."
                )
            }
        }
    }

    fun prepararFormulario() {
        _estado.value = _estado.value.copy(
            fecha = LocalDate.now().toString(),
            errorFecha = null,
            guardado = false
        )
    }

    fun cambiarFecha(fecha: String) {
        _estado.value = _estado.value.copy(
            fecha = fecha,
            errorFecha = null
        )
    }

    fun confirmarGuardado() {
        _estado.value = _estado.value.copy(guardado = false)
    }

    fun registrarNuevoInicio() {
        if (_estado.value.guardando) return

        val texto = _estado.value.fecha.trim()
        val fecha = try {
            require(Regex("\\d{4}-\\d{2}-\\d{2}").matches(texto))
            LocalDate.parse(texto)
        } catch (e: DateTimeParseException) {
            _estado.value = _estado.value.copy(
                errorFecha = "Ingresa una fecha existente, por ejemplo 2026-10-12."
            )
            return
        } catch (e: IllegalArgumentException) {
            _estado.value = _estado.value.copy(
                errorFecha = "Usa el formato AAAA-MM-DD."
            )
            return
        }

        if (fecha.isAfter(LocalDate.now())) {
            _estado.value = _estado.value.copy(
                errorFecha = "No puedes registrar una fecha futura."
            )
            return
        }

        viewModelScope.launch {
            _estado.value = _estado.value.copy(
                guardando = true,
                errorFecha = null
            )

            try {
                repository.registrarInicioMenstrual(fecha.toString())

                _estado.value = _estado.value.copy(
                    guardando = false,
                    guardado = true
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: IllegalArgumentException) {
                _estado.value = _estado.value.copy(
                    guardando = false,
                    errorFecha = e.message
                )
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    guardando = false,
                    errorFecha = "No se pudo guardar. Intenta nuevamente."
                )
            }
        }
    }
}