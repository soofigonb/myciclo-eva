package cl.myciclo.eva.ui.screens.historial

import androidx.lifecycle.ViewModel
import com.equipo.myciclo.data.local.entities.InicioMenstrualEntity
import cl.myciclo.eva.data.repository.CicloRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class DuracionCiclo(
    val fechaInicio: String,
    val duracionDias: Int
)

class HistorialViewModel(repository: CicloRepository) : ViewModel() {
    val inicios: Flow<List<InicioMenstrualEntity>> = repository.todosLosInicios

    fun calcularDuraciones(lista: List<InicioMenstrualEntity>): List<DuracionCiclo> {
        val resultado = mutableListOf<DuracionCiclo>()
        val ordenados = lista.map { LocalDate.parse(it.fechaIso) }.sorted()

        for (i in 0 until ordenados.size - 1) {
            val inicio = ordenados[i]
            val siguiente = ordenados[i + 1]
            val dias = ChronoUnit.DAYS.between(inicio, siguiente).toInt()
            resultado.add(DuracionCiclo(inicio.toString(), dias))
        }
        return resultado.reversed()
    }
}