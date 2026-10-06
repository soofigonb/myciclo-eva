package cl.myciclo.eva.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Inicio : Screen("inicio", "Inicio")
    object Calendario : Screen("calendario", "Calendario")
    object Historial : Screen("historial", "Historial")
    object Aprender : Screen("aprender", "Aprender")
    object ComoUtilizar : Screen("como_utilizar", "Cómo utilizar EVA")
    object GuiaPatrones : Screen("guia_patrones", "Guía visual de patrones")
    object FasesCiclo : Screen("fases_ciclo", "Fases del ciclo")
    object PreguntasFrecuentes : Screen("preguntas_frecuentes", "Preguntas frecuentes")
    object Ajustes : Screen("ajustes", "Ajustes")
}