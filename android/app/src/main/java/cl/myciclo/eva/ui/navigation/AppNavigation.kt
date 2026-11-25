package cl.myciclo.eva.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.myciclo.eva.data.local.MyCicloDatabase
import cl.myciclo.eva.data.local.SesionManager
import cl.myciclo.eva.data.repository.AuthRepository
import cl.myciclo.eva.data.repository.CicloRepository
import cl.myciclo.eva.ui.components.BottomNavigationBar
import cl.myciclo.eva.ui.screens.ajustes.AjustesScreen
import cl.myciclo.eva.ui.screens.aprender.AprenderScreen
import cl.myciclo.eva.ui.screens.aprender.ComoUtilizarScreen
import cl.myciclo.eva.ui.screens.aprender.FasesCicloScreen
import cl.myciclo.eva.ui.screens.aprender.GuiaPatronesScreen
import cl.myciclo.eva.ui.screens.aprender.PreguntasFrecuentesScreen
import cl.myciclo.eva.ui.screens.auth.BienvenidaScreen
import cl.myciclo.eva.ui.screens.auth.CuentaEstado
import cl.myciclo.eva.ui.screens.auth.CuentaViewModel
import cl.myciclo.eva.ui.screens.auth.FormularioCuentaScreen
import cl.myciclo.eva.ui.screens.perfil.PerfilScreen
import cl.myciclo.eva.ui.screens.bitacora.RegistroDiarioScreen
import cl.myciclo.eva.ui.screens.calendario.CalendarioScreen
import cl.myciclo.eva.ui.screens.historial.HistorialScreen
import cl.myciclo.eva.ui.screens.inicio.InicioScreen

@Composable
fun AppNavigation(
    authRepository: AuthRepository,
    sesionManager: SesionManager
) {
    val cuentaViewModel: CuentaViewModel = viewModel {
        CuentaViewModel(
            repository = authRepository,
            sesion = sesionManager
        )
    }

    val estado by cuentaViewModel.estado.collectAsStateWithLifecycle()

    if (estado.iniciando) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // Recrea la navegación al cambiar de cuenta.
        key(estado.usuarioId) {
            val usuarioId = estado.usuarioId

            if (usuarioId == null) {
                NavegacionAcceso(
                    estado = estado,
                    viewModel = cuentaViewModel
                )
            } else {
                val context = LocalContext.current.applicationContext

                val repository = remember(usuarioId, context) {
                    val database =
                        MyCicloDatabase.getDatabasePorUsuario(
                            context = context,
                            usuarioId = usuarioId
                        )

                    CicloRepository(
                        inicioDao = database.inicioMenstrualDao(),
                        registroDiarioDao = database.registroDiarioDao(),
                        context = context
                    )
                }

                NavegacionInterior(
                    repository = repository,
                    estado = estado,
                    cuentaViewModel = cuentaViewModel
                )
            }
        }
    }
}

@Composable
private fun NavegacionAcceso(
    estado: CuentaEstado,
    viewModel: CuentaViewModel
) {
    val navController = rememberNavController()

    Scaffold { innerPadding ->
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
                FormularioCuentaScreen(
                    registro = false,
                    estado = estado,
                    viewModel = viewModel,
                    onCambiarFormulario = {
                        navController.navigate(Screen.Registro.route) {
                            launchSingleTop = true
                        }
                    },
                    onVolver = {
                        navController.popBackStack(
                            route = Screen.Bienvenida.route,
                            inclusive = false
                        )
                    }
                )
            }

            composable(Screen.Registro.route) {
                FormularioCuentaScreen(
                    registro = true,
                    estado = estado,
                    viewModel = viewModel,
                    onCambiarFormulario = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Bienvenida.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },
                    onVolver = {
                        navController.popBackStack(
                            route = Screen.Bienvenida.route,
                            inclusive = false
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun NavegacionInterior(
    repository: CicloRepository,
    estado: CuentaEstado,
    cuentaViewModel: CuentaViewModel
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Inicio.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Inicio.route) {
                InicioScreen(
                    repository = repository
                )
            }

            composable(Screen.Calendario.route) {
                CalendarioScreen(
                    repository = repository,
                    onRegistroClick = { fechaIso ->
                        navController.navigate(
                            Screen.Bitacora.crearRuta(fechaIso)
                        ) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route = Screen.Bitacora.route,
                arguments = listOf(
                    navArgument("fecha") {
                        type = NavType.StringType
                    }
                )
            ) { entrada ->
                val fechaIso = requireNotNull(
                    entrada.arguments?.getString("fecha")
                )

                RegistroDiarioScreen(
                    repository = repository,
                    fechaIso = fechaIso,
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Historial.route) {
                HistorialScreen(
                    repository = repository,
                    onEditarRegistro = { fechaIso ->
                        navController.navigate(
                            Screen.Bitacora.crearRuta(fechaIso)
                        ) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.Perfil.route) {
                PerfilScreen(
                    estado = estado,
                    viewModel = cuentaViewModel,
                    onHistorial = {
                        navController.navigate(Screen.Historial.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.Aprender.route) {
                AprenderScreen(
                    onComoUtilizarClick = {
                        navController.navigate(
                            Screen.ComoUtilizar.route
                        )
                    },
                    onGuiaPatronesClick = {
                        navController.navigate(
                            Screen.GuiaPatrones.route
                        )
                    },
                    onFasesCicloClick = {
                        navController.navigate(
                            Screen.FasesCiclo.route
                        )
                    },
                    onPreguntasFrecuentesClick = {
                        navController.navigate(
                            Screen.PreguntasFrecuentes.route
                        )
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