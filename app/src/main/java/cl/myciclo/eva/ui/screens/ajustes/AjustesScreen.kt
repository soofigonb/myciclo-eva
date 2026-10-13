package cl.myciclo.eva.ui.screens.ajustes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import android.widget.Toast

@Composable
fun AjustesScreen() {
    val viewModel: AjustesViewModel = viewModel()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(mensaje) {
        mensaje?.let { texto ->
            Toast.makeText(
                context,
                texto,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val morado = Color(0xFF56328B)
    var accionPendiente by rememberSaveable { mutableStateOf("secado") }

    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            if (accionPendiente == "prueba") {
                viewModel.probarNotificacion(context)
            } else {
                viewModel.programarAvisoSecado(context)
            }
        } else {
            viewModel.informar(
                "No se concedió el permiso. Puedes habilitarlo en los ajustes de notificaciones."
            )
        }
    }

    fun iniciar(prueba: Boolean) {
        accionPendiente = if (prueba) "prueba" else "secado"

        val necesitaPermiso =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED

        if (necesitaPermiso) {
            permisoLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else if (prueba) {
            viewModel.probarNotificacion(context)
        } else {
            viewModel.programarAvisoSecado(context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF0F5))
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Ajustes ⚙️",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = morado
        )

        Text(
            text = "Recordatorios para acompañar el uso de EVA.",
            color = Color(0xFF665773)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFFCFE)
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "⏳ Recordatorio de secado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = morado
                )

                Text(
                    "Programa un aviso para revisar tu muestra dentro de una hora. " +
                            "Antes de observarla, comprueba que esté completamente seca."
                )

                Text(
                    "Android puede retrasar el aviso por ahorro de batería. " +
                            "Si reinicias el teléfono, deberás programarlo nuevamente.",
                    style = MaterialTheme.typography.bodySmall
                )

                Button(
                    onClick = { iniciar(prueba = false) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = morado
                    )
                ) {
                    Text("Recordarme en una hora")
                }

                OutlinedButton(
                    onClick = {
                        viewModel.cancelarAviso(context)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar recordatorio")
                }

                OutlinedButton(
                    onClick = { iniciar(prueba = true) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Probar aviso en 10 segundos")
                }

                TextButton(
                    onClick = {
                        context.startActivity(
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(
                                    Settings.EXTRA_APP_PACKAGE,
                                    context.packageName
                                )
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Abrir ajustes de notificaciones")
                }
            }
        }

        AnimatedVisibility(visible = mensaje != null) {
            mensaje?.let {
                Text(
                    text = it,
                    color = morado,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}