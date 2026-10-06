package cl.myciclo.eva.ui.screens.aprender

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun ContenidoEducativo(
    titulo: String,
    introduccion: String,
    secciones: List<Pair<String, String>>,
    onVolver: () -> Unit
) {
    val morado = Color(0xFF56328B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF0F5))
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = morado
        )

        Text(
            text = introduccion,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF665773)
        )

        secciones.forEach { (subtitulo, contenido) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFFCFE)
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = subtitulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = morado
                    )

                    Text(
                        text = contenido,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF665773)
                    )
                }
            }
        }

        Text(
            text = "Fuente: MyCiclo · Guía de apoyo para estudiantes.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF665773)
        )

        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = morado,
                contentColor = Color.White
            )
        ) {
            Text("Volver a Aprender")
        }
    }
}