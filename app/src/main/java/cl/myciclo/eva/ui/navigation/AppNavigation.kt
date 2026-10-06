package cl.myciclo.eva.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.myciclo.eva.data.repository.CicloRepository
import cl.myciclo.eva.ui.components.BottomNavigationBar
import cl.myciclo.eva.ui.screens.ajustes.AjustesScreen
import cl.myciclo.eva.ui.screens.calendario.CalendarioScreen
import cl.myciclo.eva.ui.screens.historial.HistorialScreen
import cl.myciclo.eva.ui.screens.inicio.InicioScreen

@Composable
fun AppNavigation(repository: CicloRepository) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Inicio.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Inicio.route) {
                InicioScreen(repository = repository)
            }
            composable(Screen.Calendario.route) {
                CalendarioScreen(repository = repository)
            }
            composable(Screen.Historial.route) {
                HistorialScreen(repository = repository)
            }
            composable(Screen.Ajustes.route) {
                AjustesScreen()
            }
        }
    }
}