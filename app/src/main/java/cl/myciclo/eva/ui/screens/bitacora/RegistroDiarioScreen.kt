package cl.myciclo.eva.ui.screens.bitacora

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.myciclo.eva.data.repository.CicloRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
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

    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val volverActual by rememberUpdatedState(onVolver)

    val morado = Color(0xFF56328B)

    // Los registros nuevos requieren elegir un patrón.
    var patron by rememberSaveable(fechaIso) {
        mutableStateOf("")
    }

    var sintomas by rememberSaveable(fechaIso) {
        mutableStateOf("")
    }

    var estadoAnimo by rememberSaveable(fechaIso) {
        mutableStateOf("")
    }

    var flujoMenstrual by rememberSaveable(fechaIso) {
        mutableStateOf("")
    }

    var flujoVaginal by rememberSaveable(fechaIso) {
        mutableStateOf("")
    }

    var notas by rememberSaveable(fechaIso) {
        mutableStateOf("")
    }

    var imagen by rememberSaveable(fechaIso) {
        mutableStateOf<String?>(null)
    }

    var formularioInicializado by rememberSaveable(fechaIso) {
        mutableStateOf(false)
    }

    var mostrarEliminar by rememberSaveable(fechaIso) {
        mutableStateOf(false)
    }

    var mostrarIndicaciones by remember {
        mutableStateOf(false)
    }

    var mostrarCamara by remember {
        mutableStateOf(false)
    }

    var procesandoFoto by remember {
        mutableStateOf(false)
    }

    var errorFoto by remember {
        mutableStateOf<String?>(null)
    }

    val permisoCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            mostrarCamara = true
        } else {
            errorFoto =
                "Permite el acceso a la cámara para tomar una fotografía. " +
                        "También puedes seleccionar una imagen de Galería."
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                procesandoFoto = true
                errorFoto = null

                try {
                    val ruta = withContext(Dispatchers.IO) {
                        val archivo = File.createTempFile(
                            "galeria_",
                            ".img",
                            carpetaFotos(context)
                        )

                        try {
                            val entrada = requireNotNull(
                                context.contentResolver.openInputStream(uri)
                            )

                            entrada.use { input ->
                                archivo.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }

                            val dimensiones = BitmapFactory.Options().apply {
                                inJustDecodeBounds = true
                            }

                            BitmapFactory.decodeFile(
                                archivo.absolutePath,
                                dimensiones
                            )

                            require(
                                dimensiones.outWidth > 0 &&
                                        dimensiones.outHeight > 0
                            ) {
                                "La imagen no es válida."
                            }

                            archivo.absolutePath
                        } catch (e: Exception) {
                            archivo.delete()
                            throw e
                        }
                    }

                    imagen = ruta
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    errorFoto =
                        "No se pudo cargar la fotografía. " +
                                "Intenta seleccionar otra imagen."
                } finally {
                    procesandoFoto = false
                }
            }
        }
    }

    // Carga una sola vez para conservar las ediciones del formulario.
    LaunchedEffect(estado.cargando, estado.registro) {
        if (
            !estado.cargando &&
            !estado.eliminado &&
            estado.error == null &&
            !formularioInicializado
        ) {
            estado.registro?.let { registro ->
                patron = registro.patron
                sintomas = registro.sintomas
                estadoAnimo = registro.estadoAnimo
                flujoMenstrual = registro.flujoMenstrual
                flujoVaginal = registro.flujoVaginal
                notas = registro.notas
                imagen = registro.fotoPath
            }

            formularioInicializado = true
        }
    }

    LaunchedEffect(estado.eliminado) {
        if (estado.eliminado) {
            mostrarEliminar = false

            Toast.makeText(
                context,
                "Registro diario eliminado",
                Toast.LENGTH_LONG
            ).show()

            volverActual()
        }
    }

    val fechaSeleccionada = remember(fechaIso) {
        runCatching {
            LocalDate.parse(fechaIso)
        }.getOrNull()
    }

    val fechaValida = fechaSeleccionada != null &&
            !fechaSeleccionada.isAfter(LocalDate.now())

    val puedeEditar = !estado.cargando &&
            !estado.guardando &&
            !estado.eliminando &&
            !estado.guardado &&
            !estado.eliminado &&
            !procesandoFoto &&
            !mostrarEliminar &&
            formularioInicializado

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFF0F5))
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
            ErrorBitacora(
                mensaje = estado.error
                    ?: "Selecciona una fecha válida que no sea futura."
            )

            Button(onClick = onVolver) {
                Text("Volver")
            }
        } else if (
            !formularioInicializado &&
            estado.error != null
        ) {
            ErrorBitacora(mensaje = estado.error)

            Button(
                onClick = { viewModel.cargarRegistro() }
            ) {
                Text("Reintentar")
            }

            TextButton(onClick = onVolver) {
                Text("Volver")
            }
        } else {
            TarjetaBitacora(titulo = "📅 Fecha de registro") {
                Text(
                    text = requireNotNull(fechaSeleccionada).format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Un registro por día. Si ya existe, " +
                            "guardar actualizará sus datos.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            TarjetaBitacora(
                titulo = "🔬 Patrón observado (EVA 50x)"
            ) {
                Text(
                    text = "Selecciona el patrón que observaste.",
                    style = MaterialTheme.typography.bodySmall
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Gotas", "Helechos").forEach { opcion ->
                        Button(
                            onClick = {
                                patron = opcion
                                viewModel.limpiarErrorPatron()
                            },
                            enabled = puedeEditar,
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

                ErrorBitacora(
                    mensaje = estado.errorPatron
                )
            }

            Text(
                text = "Los siguientes campos y la fotografía son opcionales.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF665773)
            )

            TarjetaBitacora(
                titulo = "😊 Estado de ánimo y síntomas"
            ) {
                OutlinedTextField(
                    value = estadoAnimo,
                    onValueChange = { estadoAnimo = it },
                    label = { Text("Estado de ánimo (opcional)") },
                    enabled = puedeEditar,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = sintomas,
                    onValueChange = { sintomas = it },
                    label = { Text("Síntomas físicos (opcional)") },
                    enabled = puedeEditar,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            TarjetaBitacora(titulo = "🩸 Registro de flujos") {
                OutlinedTextField(
                    value = flujoMenstrual,
                    onValueChange = { flujoMenstrual = it },
                    label = { Text("Flujo menstrual (opcional)") },
                    enabled = puedeEditar,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = flujoVaginal,
                    onValueChange = { flujoVaginal = it },
                    label = { Text("Flujo vaginal (opcional)") },
                    enabled = puedeEditar,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            TarjetaBitacora(titulo = "📝 Detalles y notas") {
                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it },
                    label = {
                        Text("Observaciones adicionales (opcional)")
                    },
                    enabled = puedeEditar,
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
                    onClick = {
                        focusManager.clearFocus()
                        errorFoto = null
                        mostrarIndicaciones = true
                    },
                    enabled = puedeEditar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("📸 Cámara", color = morado)
                }

                OutlinedButton(
                    onClick = {
                        focusManager.clearFocus()
                        errorFoto = null

                        try {
                            galleryLauncher.launch("image/*")
                        } catch (e: Exception) {
                            errorFoto = "No se pudo abrir la galería."
                        }
                    },
                    enabled = puedeEditar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🖼️ Galería", color = morado)
                }
            }

            if (procesandoFoto) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Preparando fotografía…")
            }

            ErrorBitacora(mensaje = errorFoto)

            imagen?.let { ruta ->
                TarjetaBitacora(
                    titulo = "Fotografía seleccionada"
                ) {
                    FotoRegistro(ruta = ruta)

                    Text(
                        text = "Revisa que la muestra se vea con claridad " +
                                "antes de guardar.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    OutlinedButton(
                        onClick = {
                            imagen = null
                            errorFoto = null
                        },
                        enabled = puedeEditar,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Quitar fotografía")
                    }

                    Text(
                        text = "Puedes usar Cámara o Galería para reemplazarla. " +
                                "Guarda el registro para confirmar el cambio.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (!mostrarEliminar) {
                ErrorBitacora(mensaje = estado.error)
            }

            Button(
                onClick = {
                    focusManager.clearFocus()

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
                enabled = puedeEditar,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = morado
                )
            ) {
                Text(
                    text = when {
                        estado.eliminando -> "Eliminando…"
                        estado.guardando -> "Guardando…"
                        procesandoFoto -> "Preparando fotografía…"
                        estado.registro != null ->
                            "Actualizar registro diario"

                        else -> "Guardar registro diario"
                    }
                )
            }

            if (estado.registro != null) {
                OutlinedButton(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.limpiarError()
                        mostrarEliminar = true
                    },
                    enabled = puedeEditar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar registro")
                }
            }

            TextButton(
                onClick = onVolver,
                enabled = !estado.guardando &&
                        !estado.eliminando &&
                        !estado.eliminado &&
                        !procesandoFoto,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver", color = morado)
            }
        }
    }

    if (mostrarIndicaciones) {
        AlertDialog(
            onDismissRequest = {
                mostrarIndicaciones = false
            },
            title = {
                Text("Antes de fotografiar 🔬")
            },
            text = {
                Text(
                    "1. Comprueba que la muestra esté completamente seca.\n\n" +
                            "2. Enciende EVA para observarla.\n\n" +
                            "3. Alinea la cámara delantera con el visor.\n\n" +
                            "4. Mantén el teléfono estable y revisa " +
                            "que el patrón sea visible."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarIndicaciones = false

                        val permitido =
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED

                        if (permitido) {
                            mostrarCamara = true
                        } else {
                            permisoCamara.launch(
                                Manifest.permission.CAMERA
                            )
                        }
                    }
                ) {
                    Text("Abrir cámara delantera")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarIndicaciones = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (mostrarCamara) {
        CamaraFrontalEVA(
            onFoto = { ruta ->
                imagen = ruta
                errorFoto = null
                mostrarCamara = false
            },
            onCerrar = {
                mostrarCamara = false
            }
        )
    }

    if (mostrarEliminar && !estado.eliminado) {
        AlertDialog(
            onDismissRequest = {
                if (!estado.eliminando) {
                    mostrarEliminar = false
                    viewModel.limpiarError()
                }
            },
            title = {
                Text("¿Eliminar registro diario?")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Se eliminarán las observaciones y la fotografía " +
                                "asociada al registro de esta fecha. " +
                                "El inicio menstrual se conservará."
                    )

                    Text("Esta acción no se puede deshacer.")

                    if (estado.eliminando) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    ErrorBitacora(mensaje = estado.error)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::eliminarRegistro,
                    enabled = !estado.eliminando,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        text = if (estado.eliminando) {
                            "Eliminando…"
                        } else {
                            "Eliminar"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarEliminar = false
                        viewModel.limpiarError()
                    },
                    enabled = !estado.eliminando
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (estado.guardado) {
        AlertDialog(
            onDismissRequest = onVolver,
            title = {
                Text("Registro guardado 💜")
            },
            text = {
                Text(
                    "Tus observaciones quedaron guardadas para esta fecha."
                )
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
private fun ErrorBitacora(mensaje: String?) {
    AnimatedVisibility(visible = mensaje != null) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = mensaje.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
        }
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