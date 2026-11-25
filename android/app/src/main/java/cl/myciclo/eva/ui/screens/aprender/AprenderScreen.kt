package cl.myciclo.eva.ui.screens.aprender

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AprenderScreen(
    modifier: Modifier = Modifier,
    onComoUtilizarClick: () -> Unit,
    onGuiaPatronesClick: () -> Unit,
    onFasesCicloClick: () -> Unit,
    onPreguntasFrecuentesClick: () -> Unit
) {
    val morado = Color(0xFF56328B)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFF0F5))
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Aprender",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = morado
                )

                Text(
                    text = "Guías para usar EVA",
                    style = MaterialTheme.typography.bodyLarge,
                    color = morado
                )
            }

            Text(text = "🌿", fontSize = 32.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TarjetaEducativa(
            emoji = "📖",
            titulo = "Cómo utilizar EVA",
            descripcion = "Aprende a preparar, observar y registrar tu muestra.",
            onClick = onComoUtilizarClick
        )

        TarjetaEducativa(
            emoji = "🔬",
            titulo = "Guía visual de patrones",
            descripcion = "Conoce qué observar en las muestras de saliva.",
            onClick = onGuiaPatronesClick
        )

        TarjetaEducativa(
            emoji = "🔄",
            titulo = "Fases del ciclo",
            descripcion = "Descubre los cambios que ocurren durante tu ciclo.",
            onClick = onFasesCicloClick
        )

        TarjetaEducativa(
            emoji = "💬",
            titulo = "Preguntas frecuentes",
            descripcion = "Resuelve tus dudas sobre EVA y su uso.",
            onClick = onPreguntasFrecuentesClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Conocimiento es bienestar 💜",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = morado
        )
    }
}

@Composable
private fun TarjetaEducativa(
    emoji: String,
    titulo: String,
    descripcion: String,
    onClick: () -> Unit
) {
    val morado = Color(0xFF56328B)

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFCFE)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(0xFFF0E1FA),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 26.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = morado
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF665773)
                )
            }

            Text(
                text = "›",
                fontSize = 28.sp,
                color = morado
            )
        }
    }
}