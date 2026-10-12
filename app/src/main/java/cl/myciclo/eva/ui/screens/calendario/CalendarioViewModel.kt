package cl.myciclo.eva.ui.screens.calendario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.myciclo.eva.data.local.entities.InicioMenstrualEntity
import cl.myciclo.eva.data.local.entities.RegistroDiarioEntity
import cl.myciclo.eva.data.repository.CicloRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CalendarioViewModel(repository: CicloRepository) : ViewModel() {

    // Exponemos los inicios menstruales como StateFlow seguro para Compose
    val inicios: StateFlow<List<InicioMenstrualEntity>> = repository.todosLosInicios
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Exponemos los registros diarios usando la propiedad correcta del repositorio
    val registrosDiarios: StateFlow<List<RegistroDiarioEntity>> = repository.todosLosRegistrosDiarios
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}