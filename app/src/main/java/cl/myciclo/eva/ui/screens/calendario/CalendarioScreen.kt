package cl.myciclo.eva.ui.screens.calendario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.myciclo.eva.data.repository.CicloRepository
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarioScreen(
    repository: CicloRepository,
    onRegistroClick: (String) -> Unit = {}
) {
    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel { CalendarioViewModel(repository) }

    val inicios by viewModel.inicios.collectAsStateWithLifecycle()
    val registros by viewModel.registrosDiarios.collectAsStateWithLifecycle()

    val currentMonth = remember { YearMonth.now() }
    val daysInMonth = remember { currentMonth.lengthOfMonth() }
    val firstDayOfWeek = remember { currentMonth.atDay(1).dayOfWeek.value }
    val today = remember { LocalDate.now() }

    val iniciosSet = remember(inicios) { inicios.map { it.fechaIso }.toSet() }
    val registrosMap = remember(registros) { registros.associateBy { it.fechaIso } }

    val lilaSuaveContainer = Color(0xFFEADDFF) // Lila para el día actual
    val rosaRegistroContainer = Color(0xFFFFD8E4) // Rosa para días con registros
    val fondoTarjetaLila = Color(0xFFF7F2FA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Calendario del Ciclo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "${currentMonth.month.name} ${currentMonth.year}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // PARTE SUPERIOR: Cuadrícula del Calendario
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(310.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = fondoTarjetaLila)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom").forEach { day ->
                        Text(
                            text = day,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(38.dp),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(firstDayOfWeek - 1) {
                        Box(modifier = Modifier.size(38.dp))
                    }

                    items(daysInMonth) { dayIndex ->
                        val dayNumber = dayIndex + 1
                        val cellDate = currentMonth.atDay(dayNumber)
                        val dateIso = cellDate.toString()

                        val esHoy = (cellDate == today)
                        val tieneRegistro = registrosMap.containsKey(dateIso) || iniciosSet.contains(dateIso)

                        // Lógica exacta: Hoy es lila, días anteriores con registro son rosa, resto blanco
                        val backgroundColor = when {
                            esHoy -> lilaSuaveContainer
                            tieneRegistro -> rosaRegistroContainer
                            else -> Color.White
                        }

                        val textColor = when {
                            esHoy || tieneRegistro -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(backgroundColor)
                                .clickable {
                                    onRegistroClick(dateIso)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayNumber.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // PARTE INFERIOR: Últimos registros de bitácora (dos tarjetas)
        Text(
            text = "Últimos Registros de Bitácora",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (inicios.isEmpty() && registros.isEmpty()) {
            Text("No se han registrado datos todavía.", style = MaterialTheme.typography.bodySmall)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (registros.isNotEmpty()) {
                    items(registros) { registro ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = fondoTarjetaLila)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "📅 Fecha: ${registro.fechaIso}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "Ánimo: ${registro.estadoAnimo} | Flujo: ${registro.flujoMenstrual}", style = MaterialTheme.typography.bodySmall)
                                if (registro.notas.isNotBlank()) {
                                    Text(text = "Notas: ${registro.notas}", style = MaterialTheme.typography.bodySmall)
                                }
                                if (!registro.fotoPath.isNullOrBlank()) {
                                    Text(text = "📸 [Fotografía adjunta registrada]", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CalendarioScreenPreview() {
    MaterialTheme {
        val fondoTarjetaLila = Color(0xFFF7F2FA)
        val lilaSuaveContainer = Color(0xFFEADDFF)
        val rosaRegistroContainer = Color(0xFFFFD8E4)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Calendario del Ciclo",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "OCTOBER 2026",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Tarjeta superior simulando que hoy es 12, con registros en días pasados (ej: 5 y 10) y blanco del 13 en adelante
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = fondoTarjetaLila)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom").forEach { day ->
                            Text(
                                text = day,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(38.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(31) { index ->
                            val dayNum = index + 1
                            val bg = when {
                                dayNum == 12 -> lilaSuaveContainer // Hoy (12 de octubre) en lila
                                dayNum == 5 || dayNum == 10 -> rosaRegistroContainer // Días pasados con registro en rosa
                                else -> Color.White // Del 13 en adelante y días sin registro en blanco
                            }
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(bg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayNum.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Últimos Registros de Bitácora",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Dos tarjetas de registro abajo
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = fondoTarjetaLila)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "📅 Fecha: 2026-10-10", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(text = "Ánimo: Feliz | Flujo: Moderado", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Notas: Observación con helechos clara.", style = MaterialTheme.typography.bodySmall)
                        Text(text = "📸 [Fotografía adjunta registrada]", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = fondoTarjetaLila)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "📅 Fecha: 2026-10-05", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(text = "Ánimo: Tranquila | Flujo: Ligero", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Notas: Excelente día de práctica con Compose.", style = MaterialTheme.typography.bodySmall)
                        Text(text = "📸 [Fotografía adjunta registrada]", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}