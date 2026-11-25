package cl.myciclo.eva.ui.screens.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.myciclo.eva.data.local.entities.InicioMenstrualEntity
import cl.myciclo.eva.data.local.entities.RegistroDiarioEntity
import cl.myciclo.eva.data.repository.CicloRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class DuracionCiclo(
    val fechaInicio: String,
    val duracionDias: Int
)

data class HistorialEstado(
    val eliminando: Boolean = false,
    val error: String? = null,
    val eliminado: Boolean = false,
    val mensaje: String? = null
)

class HistorialViewModel(
    private val repository: CicloRepository
) : ViewModel() {
    val inicios: Flow<List<InicioMenstrualEntity>> =
        repository.todosLosInicios

    val registrosDiarios: Flow<List<RegistroDiarioEntity>> =
        repository.todosLosRegistrosDiarios

    private val _estado = MutableStateFlow(HistorialEstado())
    val estado = _estado.asStateFlow()

    fun calcularDuraciones(
        lista: List<InicioMenstrualEntity>
    ): List<DuracionCiclo> {
        val hoy = LocalDate.now()

        val ordenados = lista
            .mapNotNull {
                runCatching {
                    LocalDate.parse(it.fechaIso)
                }.getOrNull()
            }
            .filter { !it.isAfter(hoy) }
            .distinct()
            .sorted()

        return ordenados.zipWithNext { inicio, siguiente ->
            DuracionCiclo(
                fechaInicio = inicio.toString(),
                duracionDias = ChronoUnit.DAYS
                    .between(inicio, siguiente)
                    .toInt()
            )
        }.reversed()
    }

    fun limpiarError() {
        _estado.value = _estado.value.copy(error = null)
    }

    fun confirmarMensaje() {
        _estado.value = _estado.value.copy(
            eliminado = false,
            mensaje = null
        )
    }

    fun eliminarRegistro(registro: RegistroDiarioEntity) {
        if (_estado.value.eliminando) return

        _estado.value = HistorialEstado(eliminando = true)

        viewModelScope.launch {
            try {
                repository.eliminarRegistroDiario(registro)

                _estado.value = HistorialEstado(
                    eliminado = true,
                    mensaje = "Registro diario eliminado"
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = HistorialEstado(
                    error = "No se pudo eliminar el registro. Intenta nuevamente."
                )
            }
        }
    }

    fun eliminarInicioMenstrual(fechaIso: String) {
        if (_estado.value.eliminando) return

        _estado.value = HistorialEstado(eliminando = true)

        viewModelScope.launch {
            try {
                repository.eliminarInicioMenstrual(fechaIso)

                _estado.value = HistorialEstado(
                    eliminado = true,
                    mensaje = "Inicio menstrual eliminado"
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = HistorialEstado(
                    error = "No se pudo eliminar el inicio menstrual. Intenta nuevamente."
                )
            }
        }
    }
}