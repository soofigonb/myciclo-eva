package cl.myciclo.eva.ui.screens.historial

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.myciclo.eva.data.repository.CicloRepository
import cl.myciclo.eva.ui.screens.bitacora.FotoRegistro
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HistorialScreen(
    repository: CicloRepository,
    onEditarRegistro: (String) -> Unit
) {
    val viewModel: HistorialViewModel = viewModel {
        HistorialViewModel(repository)
    }

    val inicios by viewModel.inicios.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    val registros by viewModel.registrosDiarios.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val morado = Color(0xFF56328B)

    val duraciones = remember(inicios) {
        viewModel.calcularDuraciones(inicios)
    }

    val fechasInicio = remember(inicios) {
        inicios
            .map { it.fechaIso }
            .distinct()
            .sortedDescending()
    }

    var fechaRegistroAEliminar by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var inicioAEliminar by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val registroAEliminar = registros.firstOrNull {
        it.fechaIso == fechaRegistroAEliminar
    }

    LaunchedEffect(estado.eliminado) {
        if (estado.eliminado) {
            fechaRegistroAEliminar = null
            inicioAEliminar = null

            Toast.makeText(
                context,
                estado.mensaje ?: "Registro eliminado",
                Toast.LENGTH_SHORT
            ).show()

            viewModel.confirmarMensaje()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF0F5)),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Tu historial 📖",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = morado
                )

                Text(
                    text = "Revisa tus ciclos y las observaciones que has guardado.",
                    color = Color(0xFF665773)
                )
            }
        }

        item {
            Text(
                text = "🌸 Duración de tus ciclos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = morado
            )
        }

        if (duraciones.isEmpty()) {
            item {
                TarjetaHistorial {
                    Text(
                        "Registra al menos dos fechas distintas de inicio " +
                                "menstrual para calcular la duración entre ciclos."
                    )
                }
            }
        } else {
            items(
                items = duraciones,
                key = { "ciclo-${it.fechaInicio}" }
            ) { ciclo ->
                TarjetaHistorial {
                    Text(
                        text = if (ciclo.duracionDias == 1) {
                            "1 día"
                        } else {
                            "${ciclo.duracionDias} días"
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = morado
                    )

                    Text(
                        text = "Ciclo iniciado el ${fechaLegible(ciclo.fechaInicio)}",
                        color = Color(0xFF665773)
                    )
                }
            }
        }

        item {
            Text(
                text = "📅 Inicios menstruales registrados",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = morado
            )
        }

        if (fechasInicio.isEmpty()) {
            item {
                TarjetaHistorial {
                    Text("Aún no hay inicios menstruales registrados.")
                }
            }
        } else {
            items(
                items = fechasInicio,
                key = { "inicio-$it" }
            ) { fecha ->
                TarjetaHistorial {
                    Text(
                        text = fechaLegible(fecha),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = morado
                    )

                    OutlinedButton(
                        onClick = {
                            viewModel.limpiarError()
                            inicioAEliminar = fecha
                        },
                        enabled = !estado.eliminando,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Eliminar inicio menstrual",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        item {
            HorizontalDivider(color = Color(0xFFE5D5EE))
        }

        item {
            Text(
                text = "📝 Bitácora diaria",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = morado
            )
        }

        if (registros.isEmpty()) {
            item {
                TarjetaHistorial {
                    Text(
                        text = "Aún no tienes observaciones guardadas.",
                        fontWeight = FontWeight.Bold,
                        color = morado
                    )

                    Text(
                        "Visita Calendario y selecciona una fecha para " +
                                "completar tu primera bitácora."
                    )
                }
            }
        } else {
            items(
                items = registros,
                key = { "registro-${it.fechaIso}" }
            ) { registro ->
                TarjetaHistorial {
                    Text(
                        text = "📅 ${fechaLegible(registro.fechaIso)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = morado
                    )

                    DatoRegistro("🔬 Patrón", registro.patron)
                    DatoRegistro("😊 Estado de ánimo", registro.estadoAnimo)
                    DatoRegistro("🌿 Síntomas", registro.sintomas)
                    DatoRegistro("🩸 Flujo menstrual", registro.flujoMenstrual)
                    DatoRegistro("💧 Flujo vaginal", registro.flujoVaginal)
                    DatoRegistro("📝 Notas", registro.notas)

                    registro.fotoPath
                        ?.takeIf { it.isNotBlank() }
                        ?.let { ruta ->
                            HorizontalDivider()

                            Text(
                                text = "Fotografía de la muestra",
                                fontWeight = FontWeight.Bold,
                                color = morado
                            )

                            FotoRegistro(ruta = ruta)
                        }

                    Button(
                        onClick = {
                            onEditarRegistro(registro.fechaIso)
                        },
                        enabled = !estado.eliminando,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = morado
                        )
                    ) {
                        Text("Abrir y editar registro")
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.limpiarError()
                            fechaRegistroAEliminar = registro.fechaIso
                        },
                        enabled = !estado.eliminando,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Eliminar registro",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "La duración corresponde a los días entre dos " +
                        "inicios menstruales registrados.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF665773)
            )
        }
    }

    registroAEliminar?.let { registro ->
        AlertDialog(
            onDismissRequest = {
                if (!estado.eliminando) {
                    fechaRegistroAEliminar = null
                    viewModel.limpiarError()
                }
            },
            title = {
                Text("¿Eliminar este registro?")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Se eliminará la bitácora del " +
                                "${fechaLegible(registro.fechaIso)}. " +
                                "Esta acción no se puede deshacer."
                    )

                    Text(
                        "La fecha de inicio menstrual se conserva.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    estado.error?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    if (estado.eliminando) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarRegistro(registro)
                    },
                    enabled = !estado.eliminando
                ) {
                    Text(
                        text = if (estado.eliminando) {
                            "Eliminando…"
                        } else {
                            "Eliminar"
                        },
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        fechaRegistroAEliminar = null
                        viewModel.limpiarError()
                    },
                    enabled = !estado.eliminando
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    inicioAEliminar?.let { fecha ->
        AlertDialog(
            onDismissRequest = {
                if (!estado.eliminando) {
                    inicioAEliminar = null
                    viewModel.limpiarError()
                }
            },
            title = {
                Text("¿Eliminar inicio menstrual?")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Se eliminará el inicio del ${fechaLegible(fecha)}. " +
                                "El día del ciclo y las duraciones se recalcularán. " +
                                "Esta acción no se puede deshacer."
                    )

                    Text(
                        "La bitácora y las fotografías de esa fecha se conservan.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    estado.error?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    if (estado.eliminando) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarInicioMenstrual(fecha)
                    },
                    enabled = !estado.eliminando
                ) {
                    Text(
                        text = if (estado.eliminando) {
                            "Eliminando…"
                        } else {
                            "Eliminar"
                        },
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        inicioAEliminar = null
                        viewModel.limpiarError()
                    },
                    enabled = !estado.eliminando
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun TarjetaHistorial(
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFCFE)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = contenido
        )
    }
}

@Composable
private fun DatoRegistro(
    etiqueta: String,
    valor: String
) {
    if (valor.isNotBlank()) {
        Text(
            text = "$etiqueta: $valor",
            color = Color(0xFF665773)
        )
    }
}

private fun fechaLegible(fechaIso: String): String {
    return runCatching {
        LocalDate.parse(fechaIso).format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )
    }.getOrDefault(fechaIso)
}