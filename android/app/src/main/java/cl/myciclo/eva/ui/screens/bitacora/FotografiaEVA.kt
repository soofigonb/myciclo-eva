package cl.myciclo.eva.ui.screens.bitacora

import android.content.Context
import android.graphics.BitmapFactory
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// Obtengo la carpeta privada de fotografías.
// Si no existe, intento crearla.
internal fun carpetaFotos(context: Context): File {
    return File(context.filesDir, "muestras_eva").apply {
        check(exists() || mkdirs()) {
            "No se pudo preparar la carpeta de fotografías."
        }
    }
}

// Este componente presenta la cámara delantera.
//
// onFoto: entrega la ruta cuando la captura terminó.
// onCerrar: comunica que se quiere cerrar la cámara.
@Composable
internal fun CamaraFrontalEVA(
    onFoto: (String) -> Unit,
    onCerrar: () -> Unit
) {
    val context = LocalContext.current

    // Obtengo el ciclo de vida al que vincularé la cámara.
    val lifecycleOwner = LocalLifecycleOwner.current

    // Indica si el controlador quedó preparado.
    var lista by remember {
        mutableStateOf(false)
    }

    // Evita solicitar otra captura mientras una está en curso.
    var capturando by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    // Conservo el controlador durante las recomposiciones.
    val controller = remember(context) {
        LifecycleCameraController(context).apply {

            // Selecciono la cámara delantera.
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        }
    }

    // Preparo la vinculación y defino cómo retirarla al salir.
    DisposableEffect(controller, lifecycleOwner) {
        val listener = Runnable {
            try {
                // Compruebo que exista la cámara seleccionada.
                if (!controller.hasCamera(
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    )
                ) {
                    error =
                        "Este dispositivo no tiene una cámara delantera disponible."
                } else {
                    // Vinculo el controlador al ciclo de vida.
                    controller.bindToLifecycle(lifecycleOwner)

                    // Ya puedo habilitar la solicitud de captura.
                    lista = true
                }
            } catch (e: Exception) {
                error = "No se pudo abrir la cámara delantera."
            }
        }

        // Cuando termina la inicialización, ejecuto la preparación.
        controller.initializationFuture.addListener(
            listener,
            ContextCompat.getMainExecutor(context)
        )

        // Cuando se abandona este efecto,
        // retiro la vinculación del controlador.
        onDispose {
            controller.unbind()
        }
    }

    Dialog(
        onDismissRequest = {
            // Evito cerrar mediante esta acción
            // mientras se está realizando la captura.
            if (!capturando) onCerrar()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Fotografiar muestra EVA",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Alinea el lente de la cámara delantera " +
                            "con el visor de EVA. Mantén el teléfono estable " +
                            "y ajusta la posición hasta distinguir la muestra. " +
                            "Revisa que la imagen no esté borrosa.",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Integro una vista Android dentro de Compose
                // para mostrar lo que está viendo la cámara.
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            this.controller = controller
                            scaleType = PreviewView.ScaleType.FIT_CENTER
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                // Si existe un error, lo presento.
                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    // Solo permito capturar si está preparada
                    // y no hay otra captura en curso.
                    enabled = lista && !capturando,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        error = null

                        try {
                            // Creo un archivo con nombre único
                            // dentro de la carpeta privada.
                            val archivo = File.createTempFile(
                                "eva_",
                                ".jpg",
                                carpetaFotos(context)
                            )

                            capturando = true

                            // Solicito la captura e indico
                            // dónde se debe guardar.
                            controller.takePicture(
                                ImageCapture.OutputFileOptions
                                    .Builder(archivo)
                                    .build(),
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {

                                    override fun onImageSaved(
                                        resultado: ImageCapture.OutputFileResults
                                    ) {
                                        // La imagen ya quedó guardada.
                                        capturando = false

                                        // Devuelvo su ruta a la pantalla.
                                        onFoto(archivo.absolutePath)
                                    }

                                    override fun onError(
                                        exception: ImageCaptureException
                                    ) {
                                        // Si falló, intento retirar el archivo
                                        // y permito volver a capturar.
                                        archivo.delete()
                                        capturando = false
                                        error =
                                            "No se pudo capturar. Intenta nuevamente."
                                    }
                                }
                            )
                        } catch (e: Exception) {
                            capturando = false
                            error = "No se pudo iniciar la captura."
                        }
                    }
                ) {
                    Text(
                        if (capturando) {
                            "Capturando…"
                        } else {
                            "Tomar fotografía"
                        }
                    )
                }

                TextButton(
                    enabled = !capturando,
                    onClick = onCerrar,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}

// Muestra una fotografía existente a partir de su ruta.
@Composable
internal fun FotoRegistro(ruta: String) {

    // Preparo la imagen y publico el resultado para mostrarlo.
    val bitmap by produceState<android.graphics.Bitmap?>(
        initialValue = null,

        // Si cambia la ruta, vuelvo a cargar la imagen.
        key1 = ruta
    ) {
        value = withContext(Dispatchers.IO) {
            runCatching {

                // Primero leo el tamaño de la imagen
                // sin cargar todos sus píxeles.
                val dimensiones = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }

                BitmapFactory.decodeFile(ruta, dimensiones)

                // Calculo una reducción para no cargar
                // una imagen innecesariamente grande en memoria.
                var escala = 1

                while (
                    dimensiones.outWidth / escala > 1200 ||
                    dimensiones.outHeight / escala > 1200
                ) {
                    escala *= 2
                }

                // Cargo una versión reducida en memoria.
                // El archivo original no cambia de tamaño.
                BitmapFactory.decodeFile(
                    ruta,
                    BitmapFactory.Options().apply {
                        inSampleSize = escala
                    }
                )
            }.getOrNull()
        }
    }

    val foto = bitmap

    if (foto != null) {
        // Si se pudo cargar, muestro la fotografía.
        Image(
            bitmap = foto.asImageBitmap(),
            contentDescription = "Fotografía de la muestra seleccionada",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        )
    } else {
        Text("Cargando fotografía o archivo no disponible.")
    }
}