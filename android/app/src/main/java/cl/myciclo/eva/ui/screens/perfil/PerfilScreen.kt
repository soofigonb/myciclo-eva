package cl.myciclo.eva.ui.screens.perfil

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.ContextThemeWrapper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import cl.myciclo.eva.R
import cl.myciclo.eva.ui.screens.auth.CuentaEstado
import cl.myciclo.eva.ui.screens.auth.CuentaViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@Composable
fun PerfilScreen(
    estado: CuentaEstado,
    viewModel: CuentaViewModel,
    onHistorial: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val perfil = estado.perfil
    val morado = Color(0xFF56328B)

    var nombre by rememberSaveable(perfil?.id) {
        mutableStateOf(perfil?.nombre.orEmpty())
    }

    var fechaNacimiento by rememberSaveable(perfil?.id) {
        mutableStateOf(perfil?.fechaNacimiento.orEmpty())
    }

    var foto by rememberSaveable(perfil?.id) {
        mutableStateOf(perfil?.fotoPerfilPath)
    }

    var editando by rememberSaveable(perfil?.id) {
        mutableStateOf(false)
    }

    var confirmarSalida by rememberSaveable {
        mutableStateOf(false)
    }

    var ultimoGuardadoAtendido by rememberSaveable(perfil?.id) {
        mutableStateOf(estado.perfilGuardado)
    }

    var procesandoFoto by remember {
        mutableStateOf(false)
    }

    var errorFoto by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(perfil, editando) {
        if (!editando) {
            nombre = perfil?.nombre.orEmpty()
            fechaNacimiento = perfil?.fechaNacimiento.orEmpty()
            foto = perfil?.fotoPerfilPath
        }
    }

    // Finaliza la edición cuando se confirma un nuevo guardado.
    LaunchedEffect(estado.perfilGuardado) {
        if (estado.perfilGuardado > ultimoGuardadoAtendido) {
            ultimoGuardadoAtendido = estado.perfilGuardado
            editando = false
            errorFoto = null
            focusManager.clearFocus()
        }
    }

    val galeria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                procesandoFoto = true
                errorFoto = null

                try {
                    val ruta = withContext(Dispatchers.IO) {
                        val carpeta = File(
                            context.filesDir,
                            "perfiles_eva"
                        )

                        check(carpeta.exists() || carpeta.mkdirs()) {
                            "No se pudo preparar la carpeta de fotografías."
                        }

                        val archivo = File.createTempFile(
                            "perfil_galeria_",
                            ".img",
                            carpeta
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

                            val opciones = BitmapFactory.Options().apply {
                                inJustDecodeBounds = true
                            }

                            BitmapFactory.decodeFile(
                                archivo.absolutePath,
                                opciones
                            )

                            require(
                                opciones.outWidth > 0 &&
                                        opciones.outHeight > 0
                            ) {
                                "El archivo no es una imagen válida."
                            }

                            archivo.absolutePath
                        } catch (e: Exception) {
                            archivo.delete()
                            throw e
                        }
                    }

                    foto = ruta
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    errorFoto = "No se pudo cargar la fotografía."
                } finally {
                    procesandoFoto = false
                }
            }
        }
    }

    val camara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            scope.launch {
                procesandoFoto = true
                errorFoto = null

                try {
                    val ruta = withContext(Dispatchers.IO) {
                        val carpeta = File(
                            context.filesDir,
                            "perfiles_eva"
                        )

                        check(carpeta.exists() || carpeta.mkdirs()) {
                            "No se pudo preparar la carpeta de fotografías."
                        }

                        val archivo = File.createTempFile(
                            "perfil_camara_",
                            ".jpg",
                            carpeta
                        )

                        try {
                            archivo.outputStream().use { salida ->
                                check(
                                    bitmap.compress(
                                        Bitmap.CompressFormat.JPEG,
                                        90,
                                        salida
                                    )
                                ) {
                                    "No se pudo guardar la fotografía."
                                }
                            }

                            archivo.absolutePath
                        } catch (e: Exception) {
                            archivo.delete()
                            throw e
                        }
                    }

                    foto = ruta
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    errorFoto = "No se pudo guardar la fotografía."
                } finally {
                    procesandoFoto = false
                }
            }
        }
    }

    fun abrirCamara() {
        focusManager.clearFocus()
        errorFoto = null

        try {
            camara.launch()
        } catch (e: Exception) {
            errorFoto =
                "No se pudo abrir la cámara. Comprueba que esté disponible."
        }
    }

    val permisoCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            abrirCamara()
        } else {
            errorFoto =
                "Permite el acceso a la cámara para tomar una fotografía."
        }
    }

    val habilitado = !estado.procesando && !procesandoFoto

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF0F5))
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mi perfil 🌷",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = morado
        )

        if (perfil == null) {
            Text(
                text = "Estás en modo invitada. " +
                        "Crea una cuenta para tener un perfil personal."
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AvatarPerfil(ruta = foto)

                Text(
                    text = perfil.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = morado
                )

                if (editando) {
                    OutlinedButton(
                        onClick = {
                            focusManager.clearFocus()
                            errorFoto = null

                            try {
                                galeria.launch("image/*")
                            } catch (e: Exception) {
                                errorFoto = "No se pudo abrir la galería."
                            }
                        },
                        enabled = habilitado,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🖼️ Elegir desde galería")
                    }

                    OutlinedButton(
                        onClick = {
                            val concedido =
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED

                            if (concedido) {
                                abrirCamara()
                            } else {
                                permisoCamara.launch(
                                    Manifest.permission.CAMERA
                                )
                            }
                        },
                        enabled = habilitado,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("📸 Tomar fotografía")
                    }

                    if (foto != null) {
                        TextButton(
                            onClick = {
                                foto = null
                                errorFoto = null
                            },
                            enabled = habilitado
                        ) {
                            Text("Quitar fotografía")
                        }
                    }

                    Text(
                        text = "Pulsa Guardar cambios para conservar " +
                                "tu fotografía de perfil.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF665773)
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFFCFE)
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Datos personales",
                        fontWeight = FontWeight.Bold,
                        color = morado
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = {
                            nombre = it
                            viewModel.limpiarErrorNombrePerfil()
                        },
                        label = { Text("Nombre") },
                        enabled = editando && habilitado,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = estado.errorNombrePerfil != null,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            if (estado.errorNombrePerfil != null) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Nombre con error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        supportingText = {
                            ErrorPerfil(
                                mensaje = estado.errorNombrePerfil
                            )
                        }
                    )

                    Text(
                        text = "Correo electrónico",
                        fontWeight = FontWeight.Bold
                    )

                    Text(text = perfil.correo)

                    val fechaLegible = runCatching {
                        LocalDate.parse(fechaNacimiento).format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        )
                    }.getOrDefault(
                        "Seleccionar fecha de nacimiento"
                    )

                    OutlinedButton(
                        onClick = {
                            focusManager.clearFocus()

                            val inicial = runCatching {
                                LocalDate.parse(fechaNacimiento)
                            }.getOrDefault(
                                LocalDate.now().minusYears(18)
                            )

                            val contextoCalendario = ContextThemeWrapper(
                                context,
                                R.style.CalendarioPerfilEVA
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
                                    fechaNacimiento = LocalDate.of(
                                        anio,
                                        mes + 1,
                                        dia
                                    ).toString()

                                    viewModel.limpiarErrorFechaNacimiento()
                                },
                                inicial.year,
                                inicial.monthValue - 1,
                                inicial.dayOfMonth
                            ).apply {
                                datePicker.maxDate =
                                    System.currentTimeMillis()

                                datePicker.firstDayOfWeek = Calendar.MONDAY

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
                        enabled = editando && habilitado,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor =
                                if (estado.errorFechaNacimiento != null) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    morado
                                }
                        )
                    ) {
                        Text("📅 $fechaLegible")
                    }

                    ErrorPerfil(
                        mensaje = estado.errorFechaNacimiento
                    )

                    Text(
                        text = "La fecha de nacimiento es obligatoria " +
                                "al guardar el perfil.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (editando) {
                Button(
                    onClick = {
                        focusManager.clearFocus()

                        viewModel.guardarPerfil(
                            nombre = nombre,
                            fechaNacimiento = fechaNacimiento,
                            fotoPerfilPath = foto
                        )
                    },
                    enabled = habilitado,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = morado
                    )
                ) {
                    Text(
                        text = if (estado.procesando) {
                            "Guardando…"
                        } else {
                            "Guardar cambios"
                        }
                    )
                }

                TextButton(
                    onClick = {
                        nombre = perfil.nombre
                        fechaNacimiento =
                            perfil.fechaNacimiento.orEmpty()
                        foto = perfil.fotoPerfilPath
                        errorFoto = null
                        viewModel.limpiarErroresPerfil()
                        editando = false
                        focusManager.clearFocus()
                    },
                    enabled = habilitado,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar edición")
                }
            } else {
                Button(
                    onClick = {
                        viewModel.limpiarErroresPerfil()
                        errorFoto = null
                        editando = true
                    },
                    enabled = habilitado,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = morado
                    )
                ) {
                    Text("Editar perfil")
                }
            }
        }

        if (estado.procesando || procesandoFoto) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        ErrorPerfil(mensaje = errorFoto)
        ErrorPerfil(mensaje = estado.error)

        AnimatedVisibility(
            visible = estado.mensaje != null
        ) {
            Text(
                text = estado.mensaje.orEmpty(),
                color = morado
            )
        }

        OutlinedButton(
            onClick = onHistorial,
            enabled = habilitado,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📖 Ver mi historial")
        }

        OutlinedButton(
            onClick = { confirmarSalida = true },
            enabled = habilitado,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (perfil == null) {
                    "Salir del modo invitada"
                } else {
                    "Cerrar sesión"
                }
            )
        }
    }

    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = {
                if (habilitado) confirmarSalida = false
            },
            title = {
                Text("¿Salir de EVA?")
            },
            text = {
                Text(
                    "Tus datos guardados se conservarán en el dispositivo."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::cerrarSesion,
                    enabled = habilitado
                ) {
                    Text("Salir")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { confirmarSalida = false },
                    enabled = habilitado
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ErrorPerfil(mensaje: String?) {
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
private fun AvatarPerfil(ruta: String?) {
    val bitmap by produceState<Bitmap?>(
        initialValue = null,
        key1 = ruta
    ) {
        value = null

        value = withContext(Dispatchers.IO) {
            if (ruta == null) {
                null
            } else {
                runCatching {
                    val dimensiones = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }

                    BitmapFactory.decodeFile(
                        ruta,
                        dimensiones
                    )

                    var escala = 1

                    while (
                        dimensiones.outWidth / escala > 512 ||
                        dimensiones.outHeight / escala > 512
                    ) {
                        escala *= 2
                    }

                    BitmapFactory.decodeFile(
                        ruta,
                        BitmapFactory.Options().apply {
                            inSampleSize = escala
                        }
                    )
                }.getOrNull()
            }
        }
    }

    Box(
        modifier = Modifier
            .size(140.dp)
            .clip(CircleShape)
            .background(Color(0xFFEADDFF)),
        contentAlignment = Alignment.Center
    ) {
        val imagen = bitmap

        if (imagen == null) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Perfil sin fotografía disponible",
                modifier = Modifier.size(72.dp),
                tint = Color(0xFF56328B)
            )
        } else {
            Image(
                bitmap = imagen.asImageBitmap(),
                contentDescription = "Fotografía de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}