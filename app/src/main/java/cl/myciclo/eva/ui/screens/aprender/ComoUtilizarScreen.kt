package cl.myciclo.eva.ui.screens.aprender

import androidx.compose.runtime.Composable

@Composable
fun ComoUtilizarScreen(onVolver: () -> Unit) {
    ContenidoEducativo(
        titulo = "📖 Cómo utilizar EVA",
        introduccion = "EVA es un dispositivo reutilizable que permite observar una muestra de saliva seca con un microscopio de aumento 50x.",
        secciones = listOf(
            "1. Preparar la muestra" to
                    "Toma una pequeña muestra de saliva desde una lengua limpia y deposítala en el portaobjeto del dispositivo.",

            "2. Dejar secar" to
                    "Espera hasta que la muestra esté completamente seca. La guía de MyCiclo indica un tiempo aproximado de una hora.",

            "3. Observar" to
                    "Enciende EVA y observa la muestra con el aumento 50x. Busca patrones de gotas o estructuras de cristalización similares a helechos.",

            "4. Registrar" to
                    "Anota la fecha y el patrón observado para relacionarlo con el seguimiento de tu ciclo.",

            "🧼 Cuidar el dispositivo" to
                    "Según la guía, el lente y el portaobjeto son de vidrio y se limpian con un paño, no con agua. EVA cuenta con batería recargable y conexión USB-C.",

            "💜 Recordar" to
                    "EVA observa cambios en la saliva; no mide hormonas directamente. No es un método anticonceptivo ni reemplaza la evaluación de un profesional de salud."
        ),
        onVolver = onVolver
    )
}