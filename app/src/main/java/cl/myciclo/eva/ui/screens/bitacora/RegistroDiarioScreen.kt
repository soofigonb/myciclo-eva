package cl.myciclo.eva.ui.screens.bitacora

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.myciclo.eva.data.repository.CicloRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun RegistroDiarioScreen(
    repository: CicloRepository,
    fechaIso: String,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: RegistroDiarioViewModel = viewModel(
        key = "registro-$fechaIso"
    ) {
        RegistroDiarioViewModel(repository, fechaIso)
    }

    val estado by viewModel.estado.collectAsState()

    val morado = Color(0xFF56328B)
    val fondoRosa = Color(0xFFFFF0F5)

    var patron by remember(fechaIso) { mutableStateOf("Gotas") }
    var sintomas by remember(fechaIso) { mutableStateOf("") }
    var estadoAnimo by remember(fechaIso) {
        mutableStateOf("Tranquila")
    }
    var flujoMenstrual by remember(fechaIso) {
        mutableStateOf("Ninguno")
    }
    var flujoVaginal by remember(fechaIso) {
        mutableStateOf("Normal")
    }
    var notas by remember(fechaIso) { mutableStateOf("") }
    var imagen by remember(fechaIso) {
        mutableStateOf<String?>(null)
    }

    // Ambos lanzadores se registran antes de mostrar estados de carga.
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imagen = uri.toString()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            // Pendiente: guardar el archivo de la fotografía.
            imagen = "Cámara EVA (Captura exitosa)"
        }
    }

    LaunchedEffect(estado.registro) {
        estado.registro?.let { registro ->
            patron = registro.patron
            sintomas = registro.sintomas
            estadoAnimo = registro.estadoAnimo
            flujoMenstrual = registro.flujoMenstrual
            flujoVaginal = registro.flujoVaginal
            notas = registro.notas
            imagen = registro.fotoPath
        }
    }

    val fechaSeleccionada = remember(fechaIso) {
        runCatching { LocalDate.parse(fechaIso) }.getOrNull()
    }

    val fechaValida = fechaSeleccionada != null &&
            !fechaSeleccionada.isAfter(LocalDate.now())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(fondoRosa)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Bitácora diaria 📖",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = morado
        )

        Text(
            text = "Registro de observaciones y síntomas",
            style = MaterialTheme.typography.bodyLarge,
            color = morado
        )

        if (estado.cargando) {
            CircularProgressIndicator()
        } else if (!fechaValida) {
            Text(
                text = estado.error
                    ?: "Selecciona una fecha válida que no sea futura.",
                color = MaterialTheme.colorScheme.error
            )

            Button(onClick = onVolver) {
                Text("Volver")
            }
        } else {
            TarjetaBitacora(titulo = "📅 Fecha de registro") {
                Text(
                    text = fechaSeleccionada!!.format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Un registro por día. Si ya existe, guardar actualizará sus datos.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            TarjetaBitacora(
                titulo = "🔬 Patrón observado (EVA 50x)"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Gotas", "Helechos").forEach { opcion ->
                        Button(
                            onClick = { patron = opcion },
                            enabled = !estado.guardando,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (patron == opcion) {
                                    morado
                                } else {
                                    Color(0xFFF0E1FA)
                                },
                                contentColor = if (patron == opcion) {
                                    Color.White
                                } else {
                                    morado
                                }
                            )
                        ) {
                            Text(opcion)
                        }
                    }
                }
            }

            TarjetaBitacora(
                titulo = "😊 Estado de ánimo y síntomas"
            ) {
                OutlinedTextField(
                    value = estadoAnimo,
                    onValueChange = { estadoAnimo = it },
                    label = { Text("Estado de ánimo") },
                    enabled = !estado.guardando,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = sintomas,
                    onValueChange = { sintomas = it },
                    label = { Text("Síntomas físicos") },
                    enabled = !estado.guardando,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            TarjetaBitacora(titulo = "🩸 Registro de flujos") {
                OutlinedTextField(
                    value = flujoMenstrual,
                    onValueChange = { flujoMenstrual = it },
                    label = { Text("Flujo menstrual") },
                    enabled = !estado.guardando,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = flujoVaginal,
                    onValueChange = { flujoVaginal = it },
                    label = { Text("Flujo vaginal") },
                    enabled = !estado.guardando,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            TarjetaBitacora(titulo = "📝 Detalles y notas") {
                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it },
                    label = { Text("Observaciones adicionales") },
                    enabled = !estado.guardando,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    maxLines = 5
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { cameraLauncher.launch() },
                    enabled = !estado.guardando,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("📸 Cámara", color = morado)
                }

                OutlinedButton(
                    onClick = {
                        galleryLauncher.launch("image/*")
                    },
                    enabled = !estado.guardando,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🖼️ Galería", color = morado)
                }
            }

            imagen?.let { referencia ->
                TarjetaBitacora(titulo = "Fotografía seleccionada") {
                    Text(referencia)
                    Text(
                        text = "La visualización y el guardado permanente de fotografías se completarán en el siguiente paso.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            estado.error?.let { mensaje ->
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    viewModel.guardarRegistro(
                        patron = patron,
                        estadoAnimo = estadoAnimo,
                        sintomas = sintomas,
                        flujoMenstrual = flujoMenstrual,
                        flujoVaginal = flujoVaginal,
                        notas = notas,
                        fotoPath = imagen
                    )
                },
                enabled = !estado.guardando && !estado.guardado,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = morado
                )
            ) {
                Text(
                    text = when {
                        estado.guardando -> "Guardando…"
                        estado.registro != null ->
                            "Actualizar registro diario"
                        else -> "Guardar registro diario"
                    }
                )
            }

            TextButton(
                onClick = onVolver,
                enabled = !estado.guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver", color = morado)
            }
        }
    }

    if (estado.guardado) {
        AlertDialog(
            onDismissRequest = onVolver,
            title = { Text("Registro guardado 💜") },
            text = {
                Text("Tus observaciones quedaron guardadas para esta fecha.")
            },
            confirmButton = {
                TextButton(onClick = onVolver) {
                    Text("Volver")
                }
            }
        )
    }
}

@Composable
private fun TarjetaBitacora(
    titulo: String,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFCFE)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF56328B)
            )

            contenido()
        }
    }
}