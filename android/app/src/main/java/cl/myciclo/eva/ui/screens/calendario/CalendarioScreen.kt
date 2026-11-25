package cl.myciclo.eva.ui.screens.calendario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.myciclo.eva.data.local.entities.RegistroDiarioEntity
import cl.myciclo.eva.data.repository.CicloRepository
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarioScreen(
    repository: CicloRepository,
    onRegistroClick: (String) -> Unit = {}
) {
    val viewModel: CalendarioViewModel = viewModel {
        CalendarioViewModel(repository)
    }

    val inicios by viewModel.inicios.collectAsStateWithLifecycle()
    val registros by viewModel.registrosDiarios.collectAsStateWithLifecycle()

    val fechasInicio = remember(inicios) {
        inicios.map { it.fechaIso }.toSet()
    }

    ContenidoCalendario(
        fechasInicio = fechasInicio,
        registros = registros,
        onRegistroClick = onRegistroClick
    )
}

@Composable
private fun ContenidoCalendario(
    fechasInicio: Set<String>,
    registros: List<RegistroDiarioEntity>,
    onRegistroClick: (String) -> Unit
) {
    val morado = Color(0xFF56328B)
    val textoSuave = Color(0xFF665773)
    val lilaHoy = Color(0xFFEADDFF)
    val rosaRegistro = Color(0xFFFFD8E4)

    var mesIso by rememberSaveable {
        mutableStateOf(YearMonth.now().toString())
    }

    val mes = remember(mesIso) {
        YearMonth.parse(mesIso)
    }

    val hoy = LocalDate.now()

    val formatoMes = remember {
        DateTimeFormatter.ofPattern(
            "MMMM 'de' yyyy",
            Locale.forLanguageTag("es-CL")
        )
    }

    val tituloMes = mes.format(formatoMes).replaceFirstChar {
        it.titlecase(Locale.forLanguageTag("es-CL"))
    }

    val fechasRegistro = remember(registros) {
        registros.map { it.fechaIso }.toSet()
    }

    val registrosDelMes = remember(registros, mes) {
        registros.filter { registro ->
            runCatching {
                YearMonth.from(LocalDate.parse(registro.fechaIso)) == mes
            }.getOrDefault(false)
        }.sortedByDescending { it.fechaIso }
    }

    val espaciosIniciales = mes.atDay(1).dayOfWeek.value - 1
    val cantidadFilas = (espaciosIniciales + mes.lengthOfMonth() + 6) / 7

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
                    text = "Tu calendario 🗓️",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = morado
                )

                Text(
                    text = "Selecciona un día para registrar o revisar tus observaciones.",
                    color = textoSuave
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF7F2FA)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                mesIso = mes.minusMonths(1).toString()
                            }
                        ) {
                            Text("‹", color = morado)
                        }

                        Text(
                            text = tituloMes,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = morado
                        )

                        TextButton(
                            onClick = {
                                mesIso = mes.plusMonths(1).toString()
                            }
                        ) {
                            Text("›", color = morado)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"
                        ).forEach { dia ->
                            Text(
                                text = dia,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = textoSuave
                            )
                        }
                    }

                    repeat(cantidadFilas) { fila ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            repeat(7) { columna ->
                                val numeroDia =
                                    fila * 7 + columna - espaciosIniciales + 1

                                if (numeroDia !in 1..mes.lengthOfMonth()) {
                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                    )
                                } else {
                                    val fecha = mes.atDay(numeroDia)
                                    val fechaIso = fecha.toString()
                                    val esHoy = fecha == hoy
                                    val futura = fecha.isAfter(hoy)

                                    val tieneRegistro =
                                        fechaIso in fechasRegistro ||
                                                fechaIso in fechasInicio

                                    val fondo = when {
                                        esHoy -> lilaHoy
                                        tieneRegistro -> rosaRegistro
                                        else -> Color.White
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(2.dp)
                                            .height(48.dp)
                                            .clip(CircleShape)
                                            .background(fondo)
                                            .clickable(enabled = !futura) {
                                                onRegistroClick(fechaIso)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = numeroDia.toString(),
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (futura) {
                                                Color(0xFFAAA1B1)
                                            } else {
                                                morado
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    TextButton(
                        onClick = {
                            mesIso = YearMonth.now().toString()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Volver al mes actual", color = morado)
                    }
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LeyendaCalendario(
                    color = lilaHoy,
                    texto = "Hoy"
                )

                LeyendaCalendario(
                    color = rosaRegistro,
                    texto = "Con registros"
                )
            }
        }

        item {
            Text(
                text = "Los días futuros están deshabilitados.",
                style = MaterialTheme.typography.bodySmall,
                color = textoSuave
            )
        }

        item {
            Text(
                text = "📖 Bitácoras de este mes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = morado
            )
        }

        if (registrosDelMes.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFFCFE)
                    )
                ) {
                    Text(
                        text = "No hay bitácoras guardadas en este mes. " +
                                "Selecciona una fecha hasta hoy para agregar una.",
                        modifier = Modifier.padding(18.dp),
                        color = textoSuave
                    )
                }
            }
        } else {
            items(
                items = registrosDelMes,
                key = { it.fechaIso }
            ) { registro ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onRegistroClick(registro.fechaIso)
                        },
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
                            text = "📅 ${fechaCalendario(registro.fechaIso)}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = morado
                        )

                        Text(
                            text = "🔬 Patrón: ${registro.patron}",
                            color = textoSuave
                        )

                        if (registro.estadoAnimo.isNotBlank()) {
                            Text(
                                text = "😊 Ánimo: ${registro.estadoAnimo}",
                                color = textoSuave
                            )
                        }

                        if (registro.flujoMenstrual.isNotBlank()) {
                            Text(
                                text = "🩸 Flujo menstrual: ${registro.flujoMenstrual}",
                                color = textoSuave
                            )
                        }

                        if (registro.notas.isNotBlank()) {
                            Text(
                                text = "📝 ${registro.notas}",
                                color = textoSuave
                            )
                        }

                        if (!registro.fotoPath.isNullOrBlank()) {
                            Text(
                                text = "📸 Fotografía adjunta",
                                style = MaterialTheme.typography.bodySmall,
                                color = morado
                            )
                        }

                        Text(
                            text = "Toca para abrir y editar",
                            style = MaterialTheme.typography.labelLarge,
                            color = morado
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeyendaCalendario(
    color: Color,
    texto: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(color)
        )

        Text(
            text = texto,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF665773)
        )
    }
}

private fun fechaCalendario(fechaIso: String): String {
    return runCatching {
        LocalDate.parse(fechaIso).format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )
    }.getOrDefault(fechaIso)
}

@Preview(showBackground = true)
@Composable
fun CalendarioScreenPreview() {
    MaterialTheme {
        val hoy = LocalDate.now()
        val fechaMuestra = hoy.minusDays(2).toString()

        ContenidoCalendario(
            fechasInicio = setOf(fechaMuestra),
            registros = listOf(
                RegistroDiarioEntity(
                    fechaIso = fechaMuestra,
                    patron = "Gotas",
                    estadoAnimo = "Tranquila",
                    flujoMenstrual = "Ligero",
                    notas = "Registro de ejemplo."
                )
            ),
            onRegistroClick = {}
        )
    }
}