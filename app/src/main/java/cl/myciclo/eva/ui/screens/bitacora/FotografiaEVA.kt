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

internal fun carpetaFotos(context: Context): File {
    return File(context.filesDir, "muestras_eva").apply {
        check(exists() || mkdirs()) {
            "No se pudo preparar la carpeta de fotografías."
        }
    }
}

@Composable
internal fun CamaraFrontalEVA(
    onFoto: (String) -> Unit,
    onCerrar: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var lista by remember { mutableStateOf(false) }
    var capturando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val controller = remember(context) {
        LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        }
    }

    DisposableEffect(controller, lifecycleOwner) {
        val listener = Runnable {
            try {
                if (!controller.hasCamera(
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    )
                ) {
                    error = "Este dispositivo no tiene una cámara delantera disponible."
                } else {
                    controller.bindToLifecycle(lifecycleOwner)
                    lista = true
                }
            } catch (e: Exception) {
                error = "No se pudo abrir la cámara delantera."
            }
        }

        controller.initializationFuture.addListener(
            listener,
            ContextCompat.getMainExecutor(context)
        )

        onDispose {
            controller.unbind()
        }
    }

    Dialog(
        onDismissRequest = {
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
                    text = "Alinea el lente de la cámara delantera con el visor de EVA. Mantén el teléfono estable y ajusta la posición hasta distinguir la muestra. Revisa que la imagen no esté borrosa.",
                    style = MaterialTheme.typography.bodyMedium
                )

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

                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    enabled = lista && !capturando,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        error = null

                        try {
                            val archivo = File.createTempFile(
                                "eva_",
                                ".jpg",
                                carpetaFotos(context)
                            )

                            capturando = true

                            controller.takePicture(
                                ImageCapture.OutputFileOptions
                                    .Builder(archivo)
                                    .build(),
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(
                                        resultado: ImageCapture.OutputFileResults
                                    ) {
                                        capturando = false
                                        onFoto(archivo.absolutePath)
                                    }

                                    override fun onError(
                                        exception: ImageCaptureException
                                    ) {
                                        archivo.delete()
                                        capturando = false
                                        error = "No se pudo capturar. Intenta nuevamente."
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
                        if (capturando) "Capturando…" else "Tomar fotografía"
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

@Composable
internal fun FotoRegistro(ruta: String) {
    val bitmap by produceState<android.graphics.Bitmap?>(
        initialValue = null,
        key1 = ruta
    ) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                // Lee las dimensiones antes de cargar la imagen.
                val dimensiones = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }

                BitmapFactory.decodeFile(ruta, dimensiones)

                var escala = 1
                while (
                    dimensiones.outWidth / escala > 1200 ||
                    dimensiones.outHeight / escala > 1200
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

    val foto = bitmap

    if (foto != null) {
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