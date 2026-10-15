package cl.myciclo.eva.ui.screens.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.myciclo.eva.data.repository.CicloRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
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

    /*
     * Comprueba la fecha del teléfono cada segundo, pero solo emite
     * cuando cambia el día.
     *
     * No consulta Room cada segundo ni modifica la fecha que la
     * usuaria está seleccionando en el formulario.
     */
    private fun observarFechaActual() = flow {
        var ultimaFecha = LocalDate.now()
        emit(ultimaFecha)

        while (currentCoroutineContext().isActive) {
            delay(1_000L)

            val fechaActual = LocalDate.now()

            if (fechaActual != ultimaFecha) {
                ultimaFecha = fechaActual
                emit(fechaActual)
            }
        }
    }

    init {
        observarCiclo()
    }

    private fun observarCiclo() {
        viewModelScope.launch {
            try {
                combine(
                    repository.todosLosInicios,
                    observarFechaActual()
                ) { inicios, hoy ->
                    val ultimo = inicios
                        .mapNotNull { inicio ->
                            runCatching {
                                LocalDate.parse(inicio.fechaIso)
                            }.getOrNull()
                        }
                        .filter { fecha ->
                            !fecha.isAfter(hoy)
                        }
                        .maxOrNull()

                    val diaDelCiclo = ultimo?.let { inicio ->
                        ChronoUnit.DAYS.between(inicio, hoy).toInt() + 1
                    }

                    ultimo?.toString() to diaDelCiclo
                }.collect { (ultimoInicio, diaDelCiclo) ->
                    _estado.value = _estado.value.copy(
                        cargando = false,
                        ultimoInicio = ultimoInicio,
                        diaDelCiclo = diaDelCiclo,
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
        if (_estado.value.guardando) return

        _estado.value = _estado.value.copy(
            fecha = LocalDate.now().toString(),
            errorFecha = null,
            guardado = false
        )
    }

    fun cambiarFecha(fecha: String) {
        if (_estado.value.guardando) return

        _estado.value = _estado.value.copy(
            fecha = fecha,
            errorFecha = null
        )
    }

    fun confirmarGuardado() {
        _estado.value = _estado.value.copy(
            guardado = false
        )
    }

    fun registrarNuevoInicio() {
        val actual = _estado.value
        if (actual.guardando) return

        val texto = actual.fecha.trim()

        val fecha = try {
            require(
                Regex("\\d{4}-\\d{2}-\\d{2}").matches(texto)
            )

            LocalDate.parse(texto)
        } catch (e: DateTimeParseException) {
            _estado.value = actual.copy(
                errorFecha = "Ingresa una fecha existente, " +
                        "por ejemplo 2026-10-12."
            )
            return
        } catch (e: IllegalArgumentException) {
            _estado.value = actual.copy(
                errorFecha = "Usa el formato AAAA-MM-DD."
            )
            return
        }

        if (fecha.isAfter(LocalDate.now())) {
            _estado.value = actual.copy(
                errorFecha = "No puedes registrar una fecha futura."
            )
            return
        }

        // Bloquea nuevos intentos antes de iniciar el guardado.
        _estado.value = actual.copy(
            guardando = true,
            errorFecha = null,
            guardado = false
        )

        viewModelScope.launch {
            try {
                // Comprueba nuevamente por si cambió la fecha del teléfono.
                require(!fecha.isAfter(LocalDate.now())) {
                    "No puedes registrar una fecha futura."
                }

                repository.registrarInicioMenstrual(
                    fecha.toString()
                )

                _estado.value = _estado.value.copy(
                    guardando = false,
                    guardado = true
                )
            } catch (e: CancellationException) {
                _estado.value = _estado.value.copy(
                    guardando = false
                )
                throw e
            } catch (e: IllegalArgumentException) {
                _estado.value = _estado.value.copy(
                    guardando = false,
                    errorFecha = e.message
                        ?: "Revisa la fecha seleccionada."
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