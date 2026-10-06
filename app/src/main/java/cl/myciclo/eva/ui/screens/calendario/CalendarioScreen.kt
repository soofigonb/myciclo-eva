package cl.myciclo.eva.ui.screens.calendario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.myciclo.eva.data.repository.CicloRepository

@Composable
fun CalendarioScreen(repository: CicloRepository) {
    val viewModel = remember { CalendarioViewModel(repository) }
    val inicios by viewModel.inicios.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Calendario de Ciclos", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (inicios.isEmpty()) {
            Text("No se han registrado fechas en el calendario.")
        } else {
            LazyColumn {
                items(inicios) { inicio ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "Inicio de periodo: ${inicio.fechaIso}",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}