package cl.myciclo.eva.ui.screens.aprender

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AprenderScreen(modifier: Modifier = Modifier.Companion) {
    val morado = Color(0xFF56328B)
    val fondoRosado = Color(0xFFFFF0F5)

    // Organiza el contenido verticalmente y permite desplazarlo.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(fondoRosado)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Aprender",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = morado
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Guías para usar EVA",
            style = MaterialTheme.typography.bodyLarge,
            color = morado
        )

        Spacer(modifier = Modifier.height(24.dp))

        TarjetaEducativa(
            titulo = "Cómo utilizar EVA",
            descripcion = "Conoce las funciones principales y aprende a utilizar EVA."
        )

        Spacer(modifier = Modifier.height(12.dp))

        TarjetaEducativa(
            titulo = "Guía visual de patrones",
            descripcion = "Consulta las referencias visuales de los patrones."
        )

        Spacer(modifier = Modifier.height(12.dp))

        TarjetaEducativa(
            titulo = "Fases del ciclo",
            descripcion = "Descubre las etapas del ciclo y qué sucede en cada una."
        )

        Spacer(modifier = Modifier.height(12.dp))

        TarjetaEducativa(
            titulo = "Preguntas frecuentes",
            descripcion = "Resuelve tus dudas sobre EVA y su uso."
        )
    }
}

// Reutiliza el mismo diseño para todas las tarjetas.
@Composable
private fun TarjetaEducativa(
    titulo: String,
    descripcion: String
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
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF56328B)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF665773)
            )
        }
    }
}