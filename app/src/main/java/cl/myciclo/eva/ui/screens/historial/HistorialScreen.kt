package cl.myciclo.eva.ui.screens.historial

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.myciclo.eva.data.repository.CicloRepository
import cl.myciclo.eva.data.local.entities.InicioMenstrualEntity
import cl.myciclo.eva.data.local.entities.RegistroDiarioEntity

@Composable
fun HistorialScreen(repository: CicloRepository) {
    val viewModel = remember { HistorialViewModel(repository) }
    val inicios by viewModel.inicios.collectAsState(initial = emptyList())
    // 1. Recogemos los registros diarios de la bitácora en tiempo real
    val registrosDiarios by viewModel.registrosDiarios.collectAsState(initial = emptyList())
    val duraciones = remember(inicios) { viewModel.calcularDuraciones(inicios) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Historial y Bitácora", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            "Cálculo de duración de periodos y registros diarios guardados.",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Usamos una LazyColumn para mostrar ambas secciones de forma ordenada
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección 1: Duración de ciclos
            item {
                Text("📊 Duración entre periodos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (duraciones.isEmpty()) {
                item {
                    Text("Se necesitan al menos 2 registros de inicio para calcular la duración de un ciclo.")
                }
            } else {
                items(duraciones) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
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

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text("📖 Registros de bitácora diaria", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Sección 2: Registros diarios de la bitácora
            if (registrosDiarios.isEmpty()) {
                item {
                    Text("No hay registros diarios guardados todavía.")
                }
            } else {
                items(registrosDiarios) { registro ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "📅 Fecha: ${registro.fechaIso}", fontWeight = FontWeight.Bold)
                            Text(text = "🔬 Patrón: ${registro.patron}")
                            Text(text = "😊 Ánimo: ${registro.estadoAnimo}")
                            if (registro.sintomas.isNotBlank()) {
                                Text(text = "⚠️ Síntomas: ${registro.sintomas}")
                            }
                            if (registro.notas.isNotBlank()) {
                                Text(text = "📝 Notas: ${registro.notas}")
                            }
                        }
                    }
                }
            }
        }
    }
}