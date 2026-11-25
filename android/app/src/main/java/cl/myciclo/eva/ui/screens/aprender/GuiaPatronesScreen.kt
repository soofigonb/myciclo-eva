package cl.myciclo.eva.ui.screens.aprender

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.myciclo.eva.R

private val MoradoPatrones = Color(0xFF56328B)
private val FondoPatrones = Color(0xFFFFF0F5)
private val TarjetaPatrones = Color(0xFFFFFCFE)
private val TextoPatrones = Color(0xFF665773)

private data class EjemploPatronEVA(
    val titulo: String,
    val descripcion: String,
    @DrawableRes val imagen: Int
)

@Composable
fun GuiaPatronesScreen(onVolver: () -> Unit) {
    val ejemplos = listOf(
        EjemploPatronEVA(
            titulo = "💧 Sin helechos visibles",
            descripcion = "En este ejemplo no se distinguen estructuras " +
                    "similares a helechos. Registra lo que observes en tu " +
                    "muestra: su ausencia no permite asegurar que no exista " +
                    "posibilidad de embarazo.",
            imagen = R.drawable.eva_sin_helechos
        ),
        EjemploPatronEVA(
            titulo = "🌿 Patrón de helechos",
            descripcion = "Se observan formas de cristalización ramificadas. " +
                    "Según el material de MyCiclo, su aparición puede " +
                    "relacionarse con cambios en la saliva asociados al " +
                    "aumento de estrógenos.",
            imagen = R.drawable.eva_patron_helechos
        ),
        EjemploPatronEVA(
            titulo = "🌿 Patrón de helechos marcado",
            descripcion = "Este ejemplo muestra estructuras ramificadas " +
                    "más visibles. Úsalo como referencia visual: el patrón " +
                    "no confirma por sí solo que la ovulación ya ocurrió.",
            imagen = R.drawable.eva_helechos_marcado
        )
    )

    val muestras = listOf(
        R.drawable.eva_muestra_01,
        R.drawable.eva_muestra_02,
        R.drawable.eva_muestra_03,
        R.drawable.eva_muestra_04,
        R.drawable.eva_muestra_05
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPatrones),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TextButton(onClick = onVolver) {
                Text(
                    text = "← Volver",
                    color = MoradoPatrones
                )
            }

            Text(
                text = "🔬 Guía de patrones",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MoradoPatrones
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Al secarse la saliva pueden aparecer distintas " +
                        "formas. EVA permite observarlas con aumento.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextoPatrones
            )
        }

        item {
            Text(
                text = "Ejemplos observados con EVA",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MoradoPatrones
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Compara las formas visibles en estas imágenes " +
                        "de referencia.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoPatrones
            )
        }

        items(
            items = ejemplos,
            key = { it.imagen }
        ) { ejemplo ->
            TarjetaEjemploPatron(
                titulo = ejemplo.titulo,
                descripcion = ejemplo.descripcion,
                imagen = ejemplo.imagen
            )
        }

        item {
            TarjetaTextoPatrones(
                titulo = "👀 No todas las muestras son iguales",
                texto = "Los patrones pueden variar entre muestras. " +
                        "Observa la presencia de estructuras ramificadas " +
                        "y evita asumir que todas deben verse idénticas."
            )
        }

        item {
            Text(
                text = "Más muestras de referencia",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MoradoPatrones
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Estas cinco fotografías del material de MyCiclo " +
                        "muestran la diversidad visual de las muestras. " +
                        "Su numeración identifica cada fotografía.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoPatrones
            )
        }

        items(
            count = muestras.size,
            key = { indice -> "muestra-$indice" }
        ) { indice ->
            TarjetaEjemploPatron(
                titulo = "Muestra ${indice + 1}",
                descripcion = null,
                imagen = muestras[indice]
            )
        }

        item {
            TarjetaTextoPatrones(
                titulo = "📝 Seguimiento con EVA",
                texto = "La guía de MyCiclo indica comenzar el seguimiento " +
                        "al observar el primer patrón de helechos y mantenerlo " +
                        "durante los ocho días siguientes. Esta es una " +
                        "indicación de seguimiento de EVA; no determina " +
                        "un día exacto de ovulación."
            )
        }

        item {
            TarjetaTextoPatrones(
                titulo = "💜 Interpretar con cuidado",
                texto = "Un patrón de helechos no confirma por sí solo " +
                        "que la ovulación ya ocurrió. EVA no es un método " +
                        "anticonceptivo. Consulta a un profesional ante " +
                        "dudas sobre salud o fertilidad."
            )
        }

        item {
            Text(
                text = "Fuente de las imágenes y del contenido: guía EVA " +
                        "proporcionada por MyCiclo.",
                style = MaterialTheme.typography.bodySmall,
                color = TextoPatrones
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Volver a Aprender",
                    color = MoradoPatrones
                )
            }
        }
    }
}

@Composable
private fun TarjetaEjemploPatron(
    titulo: String,
    descripcion: String?,
    @DrawableRes imagen: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = TarjetaPatrones
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
                color = MoradoPatrones
            )

            Image(
                painter = painterResource(id = imagen),
                contentDescription = titulo,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        color = Color.Black,
                        shape = RoundedCornerShape(12.dp)
                    )
            )

            if (descripcion != null) {
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoPatrones
                )
            }
        }
    }
}

@Composable
private fun TarjetaTextoPatrones(
    titulo: String,
    texto: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = TarjetaPatrones
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MoradoPatrones
            )

            Text(
                text = texto,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoPatrones
            )
        }
    }
}