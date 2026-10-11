package cl.myciclo.eva.ui.screens.bitacora

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import cl.myciclo.eva.data.repository.CicloRepository

@Composable
fun RegistroDiarioScreen(
    repository: CicloRepository,
    modifier: Modifier = Modifier
) {
    val viewModel = remember { RegistroDiarioViewModel(repository) }

    val morado = Color(0xFF56328B)
    val fondoRosa = Color(0xFFFFF0F5)
    val tarjetaBlanca = Color(0xFFFFFCFE)

    var fecha by remember { mutableStateOf("10/10/2026") }
    var patron by remember { mutableStateOf("Gotas") }
    var sintomas by remember { mutableStateOf("") }
    var estadoAnimo by remember { mutableStateOf("Tranquila") }
    var flujoMenstrual by remember { mutableStateOf("Ninguno") }
    var flujoVaginal by remember { mutableStateOf("Normal") }
    var notas by remember { mutableStateOf("") }

    // Estado y lanzador para la fotografía de la muestra
    var imagen by remember { mutableStateOf<String?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imagen = uri.toString()
        }
    }

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
                    text = "📅 Fecha de registro",
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
                    text = "🔬 Patrón observado (EVA 50x)",
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
                        onClick = { patron = "Gotas" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (patron == "Gotas") morado else Color(0xFFF0E1FA)
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Gotas",
                            color = if (patron == "Gotas") Color.White else morado
                        )
                    }
                    Button(
                        onClick = { patron = "Helechos" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (patron == "Helechos") morado else Color(0xFFF0E1FA)
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Helechos",
                            color = if (patron == "Helechos") Color.White else morado
                        )
                    }
                }
            }
        }

        // Tarjeta de Estado de ánimo y síntomas
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = tarjetaBlanca),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "😊 Estado de ánimo y síntomas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = morado
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = estadoAnimo,
                    onValueChange = { estadoAnimo = it },
                    label = { Text("Estado de ánimo (ej. Feliz, Cansada)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = sintomas,
                    onValueChange = { sintomas = it },
                    label = { Text("Síntomas físicos (ej. cólicos, dolor)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // Tarjeta de flujos
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = tarjetaBlanca),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🩸 Registro de flujos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = morado
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = flujoMenstrual,
                    onValueChange = { flujoMenstrual = it },
                    label = { Text("Flujo menstrual (ej. Abundante, Escaso)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = flujoVaginal,
                    onValueChange = { flujoVaginal = it },
                    label = { Text("Flujo vaginal (ej. Normal, Elástico)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // Tarjeta de notas y observaciones
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = tarjetaBlanca),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📝 Detalles y notas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = morado
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
            onClick = { galleryLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = morado)
        ) {
            Text(
                text = if (imagen == null) "📷 Adjuntar fotografía de la muestra" else "✅ Fotografía seleccionada",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = {
                viewModel.guardarRegistro(
                    fechaIso = fecha,
                    patron = patron,
                    estadoAnimo = estadoAnimo,
                    sintomas = sintomas,
                    flujoMenstrual = flujoMenstrual,
                    flujoVaginal = flujoVaginal,
                    notas = notas,
                    fotoPath = imagen
                )
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = morado)
        ) {
            Text(
                "Guardar registro diario",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}