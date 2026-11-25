package cl.myciclo.eva.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BienvenidaScreen(
    onIniciarSesion: () -> Unit,
    onCrearCuenta: () -> Unit
) {
    val morado = Color(0xFF56328B)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF0F5),
                        Color(0xFFFFDDE8)
                    )
                )
            )
    ) {
        val altoDisponible = maxHeight

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = altoDisponible)
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "MyCiclo",
                style = MaterialTheme.typography.titleMedium,
                color = morado
            )

            Column(
                modifier = Modifier.padding(vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "EVA",
                    fontSize = 88.sp,
                    fontWeight = FontWeight.Light,
                    color = morado
                )

                Text(
                    text = "🌿",
                    fontSize = 42.sp
                )

                Text(
                    text = "Tu ciclo, en equilibrio",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = morado
                )

                Text(
                    text = "Un espacio para conocer tu cuerpo y acompañar el seguimiento de tu ciclo.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF665773)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onIniciarSesion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = morado,
                        contentColor = Color.White
                    )
                ) {
                    Text("Iniciar sesión")
                }

                OutlinedButton(
                    onClick = onCrearCuenta,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                ) {
                    Text("Crear cuenta", color = morado)
                }

                Text(
                    text = "Conoce tu cuerpo. Toma decisiones informadas.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF665773)
                )
            }
        }
    }
}