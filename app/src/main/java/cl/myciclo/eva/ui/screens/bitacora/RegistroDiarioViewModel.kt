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
    val registro: RegistroDiarioEntity? = null,
    val error: String? = null,
    val guardado: Boolean = false
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
        viewModelScope.launch {
            _estado.value = RegistroDiarioEstado(cargando = true)

            try {
                val fecha = LocalDate.parse(fechaIso)

                if (fecha.isAfter(LocalDate.now())) {
                    _estado.value = RegistroDiarioEstado(
                        cargando = false,
                        error = "No puedes registrar una fecha futura."
                    )
                    return@launch
                }

                val registro = repository.obtenerRegistroPorFecha(fechaIso)

                _estado.value = RegistroDiarioEstado(
                    cargando = false,
                    registro = registro
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = RegistroDiarioEstado(
                    cargando = false,
                    error = "No se pudo cargar el registro. Revisa la fecha e intenta nuevamente."
                )
            }
        }
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
        if (_estado.value.cargando || _estado.value.guardando) return

        viewModelScope.launch {
            _estado.value = _estado.value.copy(
                guardando = true,
                error = null,
                guardado = false
            )

            try {
                val fecha = LocalDate.parse(fechaIso)

                require(!fecha.isAfter(LocalDate.now())) {
                    "No puedes registrar una fecha futura."
                }

                require(patron.isNotBlank()) {
                    "Selecciona el patrón observado."
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
}