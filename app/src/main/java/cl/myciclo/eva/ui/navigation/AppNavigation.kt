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
import cl.myciclo.eva.ui.screens.aprender.AprenderScreen
import cl.myciclo.eva.ui.screens.aprender.ComoUtilizarScreen
import cl.myciclo.eva.ui.screens.aprender.GuiaPatronesScreen
import cl.myciclo.eva.ui.screens.aprender.FasesCicloScreen
import cl.myciclo.eva.ui.screens.aprender.PreguntasFrecuentesScreen
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import cl.myciclo.eva.ui.screens.auth.LoginScreen
import cl.myciclo.eva.ui.screens.auth.RegistroScreen
import cl.myciclo.eva.ui.screens.auth.BienvenidaScreen

@Composable
fun AppNavigation(repository: CicloRepository) {
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    val mostrarBarraInferior =
        rutaActual != null &&
                rutaActual != Screen.Bienvenida.route &&
                rutaActual != Screen.Login.route &&
                rutaActual != Screen.Registro.route

    Scaffold(
        bottomBar = {
            if (mostrarBarraInferior) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Bienvenida.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Bienvenida.route) {
                BienvenidaScreen(
                    onIniciarSesion = {
                        navController.navigate(Screen.Login.route) {
                            launchSingleTop = true
                        }
                    },
                    onCrearCuenta = {
                        navController.navigate(Screen.Registro.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    onRegistro = {
                        navController.navigate(Screen.Registro.route) {
                            launchSingleTop = true
                        }
                    },
                    onExplorar = {
                        navController.navigate(Screen.Inicio.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Screen.Registro.route) {
                RegistroScreen(
                    onVolver = {
                        val regresoAlLogin =
                            navController.popBackStack(
                                Screen.Login.route,
                                false
                            )

                        if (!regresoAlLogin) {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Bienvenida.route) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
            composable(Screen.Inicio.route) {
                InicioScreen(repository = repository)
            }
            composable(Screen.Calendario.route) {
                CalendarioScreen(repository = repository)
            }
            composable(Screen.Historial.route) {
                HistorialScreen(repository = repository)
            }
            composable(Screen.Aprender.route) {
                AprenderScreen(
                    onComoUtilizarClick = {
                        navController.navigate(Screen.ComoUtilizar.route)
                    },
                    onGuiaPatronesClick = {
                        navController.navigate(Screen.GuiaPatrones.route)
                    },
                    onFasesCicloClick = {
                        navController.navigate(Screen.FasesCiclo.route)
                    },
                    onPreguntasFrecuentesClick = {
                        navController.navigate(Screen.PreguntasFrecuentes.route)
                    }
                )
            }
            composable(Screen.ComoUtilizar.route) {
                ComoUtilizarScreen(
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.GuiaPatrones.route) {
                GuiaPatronesScreen(
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.FasesCiclo.route) {
                FasesCicloScreen(
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.PreguntasFrecuentes.route) {
                PreguntasFrecuentesScreen(
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Ajustes.route) {
                AjustesScreen()
            }
        }
    }
}