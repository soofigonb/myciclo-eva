package cl.myciclo.eva.ui.screens.aprender

import androidx.compose.runtime.Composable

@Composable
fun PreguntasFrecuentesScreen(onVolver: () -> Unit) {
    ContenidoEducativo(
        titulo = "💬 Preguntas frecuentes",
        introduccion = "Conoce el propósito de EVA, cómo se utiliza y cuáles son sus límites.",
        secciones = listOf(
            "¿Qué es EVA?" to
                    "Es un test de ovulación en saliva reutilizable que incorpora un microscopio de aumento 50x para observar patrones de cristalización.",

            "¿EVA mide mis hormonas?" to
                    "No. Permite observar cambios en la saliva que pueden estar relacionados con los efectos del estrógeno; no mide directamente sus niveles.",

            "¿Cuánto debe secarse la muestra?" to
                    "La guía de MyCiclo indica aproximadamente una hora. La muestra debe estar completamente seca antes de observarla.",

            "¿Los helechos indican el día exacto de ovulación?" to
                    "No. El patrón puede ser compatible con cambios cercanos al período fértil, pero no determina una fecha exacta ni confirma que la ovulación ya ocurrió.",

            "¿Qué significan los ocho días de seguimiento?" to
                    "El material de MyCiclo indica realizar el seguimiento desde el primer patrón de helechos y durante los ocho días siguientes. No debe interpretarse como una garantía sobre días fértiles o no fértiles.",

            "¿Puedo usar EVA para evitar un embarazo?" to
                    "No. EVA no es un método anticonceptivo.",

            "¿Cómo limpio el dispositivo?" to
                    "La guía indica limpiar el lente y el portaobjeto con un paño, sin utilizar agua.",

            "¿Todos los ciclos duran 28 días?" to
                    "No. La duración del ciclo y el momento de la ovulación pueden variar.",

            "¿Qué hago si tengo dudas?" to
                    "Consulta las instrucciones del dispositivo y a un profesional de salud si tienes dudas sobre tu ciclo, fertilidad o interpretación de los resultados."
        ),
        onVolver = onVolver
    )
}