package cl.myciclo.eva.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Inicio : Screen("inicio", "Inicio")
    object Calendario : Screen("calendario", "Calendario")
    object Historial : Screen("historial", "Historial")
    object Ajustes : Screen("ajustes", "Ajustes")
}