package cl.myciclo.eva.ui.screens.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.myciclo.eva.data.repository.CicloRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class InicioViewModel(private val repository: CicloRepository) : ViewModel() {

    private val _diaDelCiclo = MutableStateFlow<Int?>(null)
    val diaDelCiclo: StateFlow<Int?> = _diaDelCiclo

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

    init {
        cargarEstadoCiclo()
    }

    fun cargarEstadoCiclo() {
        viewModelScope.launch {
            val ultimoInicio = repository.obtenerUltimoInicio()
            if (ultimoInicio != null) {
                val fechaInicio = LocalDate.parse(ultimoInicio.fechaIso)
                val hoy = LocalDate.now()
                if (!fechaInicio.isAfter(hoy)) {
                    val diasDiferencia = ChronoUnit.DAYS.between(fechaInicio, hoy).toInt()
                    _diaDelCiclo.value = diasDiferencia + 1 // El día de inicio es el Día 1
                    _mensajeError.value = null
                } else {
                    _mensajeError.value = "La fecha ingresada es posterior a hoy."
                }
            } else {
                _diaDelCiclo.value = null
            }
        }
    }

    fun registrarNuevoInicio(fechaIso: String) {
        viewModelScope.launch {
            try {
                val fecha = LocalDate.parse(fechaIso)
                if (fecha.isAfter(LocalDate.now())) {
                    _mensajeError.value = "No puedes seleccionar una fecha futura."
                    return@launch
                }
                repository.registrarInicioMenstrual(fechaIso)
                cargarEstadoCiclo()
            } catch (e: Exception) {
                _mensajeError.value = "Formato de fecha inválido. Usar YYYY-MM-DD."
            }
        }
    }
}