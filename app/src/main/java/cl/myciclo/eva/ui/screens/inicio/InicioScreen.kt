package cl.myciclo.eva.ui.screens.inicio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.myciclo.eva.data.repository.CicloRepository
import java.time.LocalDate

@Composable
fun InicioScreen(repository: CicloRepository) {
    val viewModel = remember { InicioViewModel(repository) }
    val diaDelCiclo by viewModel.diaDelCiclo.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()

    var mostrarDialogo by remember { mutableStateOf(false) }
    var nuevaFecha by remember { mutableStateOf(LocalDate.now().toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "myCiclo / EVA", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (diaDelCiclo != null) {
                    Text(text = "Día del ciclo actual", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Día $diaDelCiclo",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "No hay inicio de ciclo registrado",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { mostrarDialogo = true }) {
            Text("Registrar inicio de regla")
        }

        AnimatedVisibility(visible = mensajeError != null) {
            mensajeError?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Registrar inicio de ciclo") },
            text = {
                OutlinedTextField(
                    value = nuevaFecha,
                    onValueChange = { nuevaFecha = it },
                    label = { Text("Fecha (YYYY-MM-DD)") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.registrarNuevoInicio(nuevaFecha)
                    mostrarDialogo = false
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}