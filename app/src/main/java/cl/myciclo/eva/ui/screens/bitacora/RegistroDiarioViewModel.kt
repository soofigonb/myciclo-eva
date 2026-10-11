package cl.myciclo.eva.ui.screens.bitacora // Ajusta el paquete según tu carpeta actual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.myciclo.eva.data.local.entities.RegistroDiarioEntity
import cl.myciclo.eva.data.repository.CicloRepository
import kotlinx.coroutines.launch

class RegistroDiarioViewModel(private val repository: CicloRepository) : ViewModel() {

    fun guardarRegistro(
        fechaIso: String,
        patron: String,
        estadoAnimo: String,
        sintomas: String,
        flujoMenstrual: String,
        flujoVaginal: String,
        notas: String
    ) {
        viewModelScope.launch {
            val nuevoRegistro = RegistroDiarioEntity(
                fechaIso = fechaIso,
                patron = patron,
                estadoAnimo = estadoAnimo,
                sintomas = sintomas,
                flujoMenstrual = flujoMenstrual,
                flujoVaginal = flujoVaginal,
                notas = notas
            )
            repository.registrarDiario(nuevoRegistro)
        }
    }
}