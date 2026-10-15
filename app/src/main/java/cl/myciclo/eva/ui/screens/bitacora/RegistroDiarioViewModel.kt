package cl.myciclo.eva.ui.screens.bitacora

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.myciclo.eva.data.local.entities.RegistroDiarioEntity
import cl.myciclo.eva.data.repository.CicloRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class RegistroDiarioEstado(
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    val eliminando: Boolean = false,
    val registro: RegistroDiarioEntity? = null,
    val errorPatron: String? = null,
    val error: String? = null,
    val guardado: Boolean = false,
    val eliminado: Boolean = false
)

class RegistroDiarioViewModel(
    private val repository: CicloRepository,
    private val fechaIso: String
) : ViewModel() {

    private val _estado = MutableStateFlow(RegistroDiarioEstado())
    val estado = _estado.asStateFlow()

    init {
        cargarRegistro()
    }

    fun cargarRegistro() {
        if (_estado.value.guardando || _estado.value.eliminando) return

        viewModelScope.launch {
            _estado.value = RegistroDiarioEstado(cargando = true)

            try {
                val fecha = LocalDate.parse(fechaIso)

                require(!fecha.isAfter(LocalDate.now())) {
                    "No puedes registrar una fecha futura."
                }

                val registro = repository.obtenerRegistroPorFecha(fechaIso)

                _estado.value = RegistroDiarioEstado(
                    cargando = false,
                    registro = registro
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: IllegalArgumentException) {
                _estado.value = RegistroDiarioEstado(
                    cargando = false,
                    error = e.message ?: "Revisa la fecha seleccionada."
                )
            } catch (e: Exception) {
                _estado.value = RegistroDiarioEstado(
                    cargando = false,
                    error = "No se pudo cargar el registro. Intenta nuevamente."
                )
            }
        }
    }

    fun limpiarErrorPatron() {
        _estado.value = _estado.value.copy(
            errorPatron = null
        )
    }

    fun limpiarError() {
        _estado.value = _estado.value.copy(
            error = null
        )
    }

    fun guardarRegistro(
        patron: String,
        estadoAnimo: String,
        sintomas: String,
        flujoMenstrual: String,
        flujoVaginal: String,
        notas: String,
        fotoPath: String?
    ) {
        val actual = _estado.value

        if (
            actual.cargando ||
            actual.guardando ||
            actual.eliminando ||
            actual.guardado ||
            actual.eliminado
        ) return

        if (patron !in setOf("Gotas", "Helechos")) {
            _estado.value = actual.copy(
                errorPatron = "Selecciona el patrón que observaste.",
                error = null
            )
            return
        }

        _estado.value = actual.copy(
            guardando = true,
            error = null,
            errorPatron = null,
            guardado = false
        )

        viewModelScope.launch {
            try {
                val fecha = LocalDate.parse(fechaIso)

                require(!fecha.isAfter(LocalDate.now())) {
                    "No puedes registrar una fecha futura."
                }

                val registro = RegistroDiarioEntity(
                    fechaIso = fecha.toString(),
                    patron = patron,
                    estadoAnimo = estadoAnimo.trim(),
                    sintomas = sintomas.trim(),
                    flujoMenstrual = flujoMenstrual.trim(),
                    flujoVaginal = flujoVaginal.trim(),
                    notas = notas.trim(),
                    fotoPath = fotoPath
                )

                repository.registrarDiario(registro)

                _estado.value = _estado.value.copy(
                    guardando = false,
                    registro = registro,
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
                    error = e.message ?: "Revisa los datos del registro."
                )
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    guardando = false,
                    error = "No se pudo guardar el registro. Intenta nuevamente."
                )
            }
        }
    }

    fun eliminarRegistro() {
        val actual = _estado.value
        val registro = actual.registro ?: return

        if (
            actual.cargando ||
            actual.guardando ||
            actual.eliminando ||
            actual.guardado ||
            actual.eliminado
        ) return

        _estado.value = actual.copy(
            eliminando = true,
            error = null,
            errorPatron = null
        )

        viewModelScope.launch {
            try {
                repository.eliminarRegistroDiario(registro)

                _estado.value = _estado.value.copy(
                    eliminando = false,
                    registro = null,
                    eliminado = true
                )
            } catch (e: CancellationException) {
                _estado.value = _estado.value.copy(
                    eliminando = false
                )
                throw e
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    eliminando = false,
                    error = "No se pudo eliminar el registro. Intenta nuevamente."
                )
            }
        }
    }
}