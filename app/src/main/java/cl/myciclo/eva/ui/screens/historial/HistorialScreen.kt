package cl.myciclo.eva.ui.screens.historial

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.myciclo.eva.data.repository.CicloRepository

@Composable
fun HistorialScreen(repository: CicloRepository) {
    val viewModel = remember { HistorialViewModel(repository) }
    val inicios by viewModel.inicios.collectAsState(initial = emptyList())
    val duraciones = remember(inicios) { viewModel.calcularDuraciones(inicios) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Historial Comparativo", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Cálculo de la duración real entre periodos (sin asumir 28 días fijos).",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (duraciones.isEmpty()) {
            Text("Se necesitan al menos 2 registros de inicio para calcular la duración de un ciclo.")
        } else {
            LazyColumn {
                items(duraciones) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Periodo iniciado el: ${item.fechaInicio}")
                            Text(
                                text = "Duración: ${item.duracionDias} días",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}