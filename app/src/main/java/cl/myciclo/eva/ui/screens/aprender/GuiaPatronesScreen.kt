package cl.myciclo.eva.ui.screens.aprender

import androidx.compose.runtime.Composable

@Composable
fun GuiaPatronesScreen(onVolver: () -> Unit) {
    ContenidoEducativo(
        titulo = "🔬 Guía de patrones",
        introduccion = "Al secarse la saliva pueden aparecer distintas formas. EVA permite observarlas con aumento.",
        secciones = listOf(
            "💧 Gotas o ausencia de helechos" to
                    "La guía describe muestras con patrones de gotas. Si no distingues estructuras similares a helechos, registra lo observado. Su ausencia no permite asegurar que no exista posibilidad de embarazo.",

            "🌿 Estructuras similares a helechos" to
                    "Son formas de cristalización ramificadas. Según el material de MyCiclo, su aparición puede relacionarse con cambios en la saliva asociados al aumento de estrógenos.",

            "👀 No todas las muestras son iguales" to
                    "Los patrones pueden variar entre muestras. Observa la presencia de estructuras ramificadas y evita asumir que todas deben verse idénticas.",

            "📝 Seguimiento con EVA" to
                    "La guía de MyCiclo indica comenzar el seguimiento al observar el primer patrón de helechos y mantenerlo durante los ocho días siguientes. Esta es una indicación de seguimiento de EVA; no determina un día exacto de ovulación.",

            "💜 Interpretar con cuidado" to
                    "Un patrón de helechos no confirma por sí solo que la ovulación ya ocurrió. EVA no es un método anticonceptivo. Consulta a un profesional ante dudas sobre salud o fertilidad."
        ),
        onVolver = onVolver
    )
}