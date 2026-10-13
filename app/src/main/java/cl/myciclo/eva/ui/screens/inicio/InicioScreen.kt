package cl.myciclo.eva.ui.screens.inicio

import android.app.DatePickerDialog
import android.content.res.Configuration
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import cl.myciclo.eva.R
import cl.myciclo.eva.data.repository.CicloRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun InicioScreen(repository: CicloRepository) {
    val viewModel: InicioViewModel = viewModel {
        InicioViewModel(repository)
    }

    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val morado = Color(0xFF56328B)
    val textoSuave = Color(0xFF665773)

    var mostrarDialogo by rememberSaveable {
        mutableStateOf(false)
    }

    val formatoFecha = remember {
        DateTimeFormatter.ofPattern(
            "d 'de' MMMM 'de' yyyy",
            Locale.forLanguageTag("es-CL")
        )
    }

    val formatoFechaCorta = remember {
        DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }

    val fechaSeleccionada = remember(estado.fecha) {
        runCatching {
            LocalDate.parse(estado.fecha)
        }.getOrElse {
            LocalDate.now()
        }
    }

    LaunchedEffect(estado.guardado) {
        if (estado.guardado) {
            mostrarDialogo = false

            Toast.makeText(
                context,
                "Inicio menstrual guardado",
                Toast.LENGTH_SHORT
            ).show()

            viewModel.confirmarGuardado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF0F5))
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "MyCiclo · EVA 🌷",
                style = MaterialTheme.typography.titleMedium,
                color = morado
            )

            Text(
                text = "Conoce tu ciclo",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = morado
            )

            Text(
                text = LocalDate.now().format(formatoFecha),
                style = MaterialTheme.typography.bodyMedium,
                color = textoSuave
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF0E1FA)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "🌸 Tu seguimiento",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = morado
                )

                when {
                    estado.cargando -> {
                        CircularProgressIndicator(color = morado)
                    }

                    estado.errorCarga != null -> {
                        Text(
                            text = estado.errorCarga.orEmpty(),
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    estado.diaDelCiclo != null -> {
                        Text(
                            text = "Día ${estado.diaDelCiclo}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = morado
                        )

                        Text(
                            text = "Desde el último inicio menstrual registrado.",
                            color = textoSuave
                        )

                        estado.ultimoInicio?.let { fecha ->
                            Text(
                                text = "Inicio: ${
                                    LocalDate.parse(fecha).format(formatoFecha)
                                }",
                                style = MaterialTheme.typography.bodySmall,
                                color = textoSuave
                            )
                        }
                    }

                    else -> {
                        Text(
                            text = "Comienza tu seguimiento",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = morado
                        )

                        Text(
                            text = "Registra el primer día de tu menstruación para comenzar.",
                            color = textoSuave
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                viewModel.prepararFormulario()
                mostrarDialogo = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = morado
            )
        ) {
            Text("📅 Registrar inicio menstrual")
        }

        Text(
            text = "Un paso a la vez 💜",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = morado
        )

        TarjetaInicio(
            titulo = "🗓️ Consulta tu calendario",
            descripcion = "Selecciona una fecha para registrar o revisar tus observaciones."
        )

        TarjetaInicio(
            titulo = "📖 Completa tu bitácora",
            descripcion = "Anota el patrón observado, tus síntomas y cómo te sientes."
        )

        TarjetaInicio(
            titulo = "🔬 Aprende sobre EVA",
            descripcion = "Visita Aprender para consultar las guías de uso y patrones."
        )

        Text(
            text = "El día del ciclo se calcula a partir de tus registros; no confirma la ovulación.",
            style = MaterialTheme.typography.bodySmall,
            color = textoSuave
        )
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = {
                if (!estado.guardando) {
                    mostrarDialogo = false
                }
            },
            title = {
                Text("Registrar inicio menstrual 🌸")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Indica el primer día de tu menstruación."
                    )

                    OutlinedButton(
                        onClick = {
                            // Conserva el contexto de la actividad para abrir
                            // el diálogo y cambia su idioma y tema.
                            val contextoCalendario = ContextThemeWrapper(
                                context,
                                R.style.ThemeOverlay_MyCicloEVA_DatePicker
                            ).apply {
                                applyOverrideConfiguration(
                                    Configuration().apply {
                                        setLocale(
                                            Locale.forLanguageTag("es-CL")
                                        )
                                    }
                                )
                            }

                            DatePickerDialog(
                                contextoCalendario,
                                { _, anio, mes, dia ->
                                    val fecha = LocalDate.of(
                                        anio,
                                        mes + 1,
                                        dia
                                    )

                                    viewModel.cambiarFecha(
                                        fecha.toString()
                                    )
                                },
                                fechaSeleccionada.year,
                                fechaSeleccionada.monthValue - 1,
                                fechaSeleccionada.dayOfMonth
                            ).apply {
                                datePicker.maxDate =
                                    System.currentTimeMillis()

                                setButton(
                                    DatePickerDialog.BUTTON_POSITIVE,
                                    "Seleccionar",
                                    this
                                )

                                setButton(
                                    DatePickerDialog.BUTTON_NEGATIVE,
                                    "Cancelar",
                                    this
                                )
                            }.show()
                        },
                        enabled = !estado.guardando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = morado
                        )
                    ) {
                        Text(
                            text = "📅 ${
                                fechaSeleccionada.format(formatoFechaCorta)
                            }"
                        )
                    }

                    Text(
                        text = "Toca la fecha para seleccionar año, mes y día.",
                        style = MaterialTheme.typography.bodySmall,
                        color = textoSuave
                    )

                    AnimatedVisibility(
                        visible = estado.errorFecha != null
                    ) {
                        Text(
                            text = estado.errorFecha.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (estado.guardando) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::registrarNuevoInicio,
                    enabled = !estado.guardando
                ) {
                    Text(
                        text = if (estado.guardando) {
                            "Guardando…"
                        } else {
                            "Guardar"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogo = false
                    },
                    enabled = !estado.guardando
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun TarjetaInicio(
    titulo: String,
    descripcion: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFCFE)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF56328B)
            )

            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF665773)
            )
        }
    }
}