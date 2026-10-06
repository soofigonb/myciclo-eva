package cl.myciclo.eva.ui.screens.calendario

import androidx.lifecycle.ViewModel
import cl.myciclo.eva.data.entities.InicioMenstrualEntity
import cl.myciclo.eva.data.repository.CicloRepository
import kotlinx.coroutines.flow.Flow

class CalendarioViewModel(repository: CicloRepository) : ViewModel() {
    val inicios: Flow<List<InicioMenstrualEntity>> = repository.todosLosInicios
}