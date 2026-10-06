package cl.myciclo.eva.ui.screens.aprender

import androidx.compose.runtime.Composable

@Composable
fun FasesCicloScreen(onVolver: () -> Unit) {
    ContenidoEducativo(
        titulo = "🔄 Fases del ciclo",
        introduccion = "El ciclo se cuenta desde el primer día de sangrado hasta el día anterior a la siguiente menstruación. Su duración puede variar.",
        secciones = listOf(
            "🩸 Menstruación" to
                    "Disminuyen los niveles de estrógeno y progesterona y se desprende el endometrio, produciendo el sangrado. La menstruación ocurre al comienzo de la fase folicular.",

            "🌱 Fase folicular" to
                    "Los folículos del ovario comienzan a desarrollarse. A medida que uno se vuelve dominante, aumenta la producción de estrógeno. Estos cambios pueden favorecer la aparición de cristalizaciones en la saliva.",

            "🌸 Ovulación" to
                    "El aumento de la hormona luteinizante, conocida como LH, desencadena la liberación de un óvulo. El momento de la ovulación puede variar entre ciclos; EVA no confirma por sí sola que haya ocurrido.",

            "🌙 Fase lútea" to
                    "Después de la ovulación, el cuerpo lúteo produce progesterona. Si no se establece un embarazo, los niveles hormonales disminuyen y comienza una nueva menstruación.",

            "📅 Cada ciclo puede ser diferente" to
                    "No todas las personas tienen ciclos de 28 días ni ovulan en la misma fecha. Registrar tus observaciones puede ayudarte a conocer los cambios de tu ciclo."
        ),
        onVolver = onVolver
    )
}