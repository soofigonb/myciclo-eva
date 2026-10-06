package cl.myciclo.eva.ui.screens.ajustes

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun AjustesScreen() {
    val viewModel = remember { AjustesViewModel() }
    val context = LocalContext.current

    val launcherPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            viewModel.programarAvisoSecado(context)
            Toast.makeText(context, "Aviso de secado programado en 1 hora", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Ajustes y Recordatorios", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Aviso de Secado EVA (1 Hora)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Activa un temporizador de 1 hora para revisar el patrón de saliva en el microscopio EVA.")
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        launcherPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        viewModel.programarAvisoSecado(context)
                        Toast.makeText(context, "Aviso de secado programado en 1 hora", Toast.LENGTH_LONG).show()
                    }
                }) {
                    Text("Iniciar Temporizador de 1 hora")
                }
            }
        }
    }
}