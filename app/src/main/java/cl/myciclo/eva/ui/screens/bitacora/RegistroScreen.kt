package cl.myciclo.eva.ui.screens.bitacora

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegistroScreen(
    modifier: Modifier = Modifier
) {
    val morado = Color(0xFF56328B)
    val fondoRosa = Color(0xFFFFF0F5)
    val tarjetaBlanca = Color(0xFFFFFCFE)

    var fecha by remember { mutableStateOf("10/10/2026") }
    var patronHelechos by remember { mutableStateOf("Gotas") }
    var sintomas by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(fondoRosa)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bitácora Diaria",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = morado
                )
                Text(
                    text = "Registro de observaciones y síntomas",
                    style = MaterialTheme.typography.bodyLarge,
                    color = morado
                )
            }
            Text(text = "📖", fontSize = 34.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Tarjeta de Fecha
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = tarjetaBlanca),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📅 Fecha de Registro",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = morado
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // Tarjeta de Patrón EVA
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = tarjetaBlanca),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🔬 Patrón Observado (EVA 50x)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = morado
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { patronHelechos = "Gotas" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (patronHelechos == "Gotas") morado else Color(0xFFF0E1FA)
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Gotas",
                            color = if (patronHelechos == "Gotas") Color.White else morado
                        )
                    }
                    Button(
                        onClick = { patronHelechos = "Helechos" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (patronHelechos == "Helechos") morado else Color(0xFFF0E1FA)
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Helechos",
                            color = if (patronHelechos == "Helechos") Color.White else morado
                        )
                    }
                }
            }
        }

        // Tarjeta de Síntomas y Notas
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = tarjetaBlanca),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📝 Detalles y Notas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = morado
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = sintomas,
                    onValueChange = { sintomas = it },
                    label = { Text("Síntomas físicos (ej. cólicos)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it },
                    label = { Text("Observaciones adicionales") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )
            }
        }

        OutlinedButton(
            onClick = { /* TODO: Cámara o galería */ },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = morado)
        ) {
            Text("📷 Adjuntar fotografía de la muestra", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = { /* TODO: Guardar en Room */ },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = morado)
        ) {
            Text("Guardar Registro Diario", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun RegistroScreenPreview() {
    RegistroScreen()
}