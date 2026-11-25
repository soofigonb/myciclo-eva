package cl.myciclo.eva.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import cl.myciclo.eva.ui.navigation.Screen

@Composable
fun BottomNavigationBar(navController: NavController) {
    var menuAbierto by remember { mutableStateOf(false) }

    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    val items = listOf(
        Screen.Inicio to Icons.Default.Home,
        Screen.Calendario to Icons.Default.DateRange,
        Screen.Perfil to Icons.Default.Person,
        Screen.Aprender to Icons.Default.Info
    )

    val rutaSeleccionada = when (rutaActual) {
        Screen.Bitacora.route -> Screen.Calendario.route

        Screen.ComoUtilizar.route,
        Screen.GuiaPatrones.route,
        Screen.FasesCiclo.route,
        Screen.PreguntasFrecuentes.route -> Screen.Aprender.route

        else -> rutaActual
    }

    val colores = NavigationBarItemDefaults.colors(
        selectedIconColor = Color(0xFF56328B),
        selectedTextColor = Color(0xFF56328B),
        indicatorColor = Color(0xFFEADDFF),
        unselectedIconColor = Color(0xFF665773),
        unselectedTextColor = Color(0xFF665773)
    )

    fun navegar(screen: Screen) {
        menuAbierto = false

        if (rutaActual != screen.route) {
            navController.navigate(screen.route) {
                popUpTo(Screen.Inicio.route) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    NavigationBar(
        containerColor = Color(0xFFF2EAF7)
    ) {
        items.forEach { (screen, icono) ->
            NavigationBarItem(
                selected = rutaSeleccionada == screen.route,
                onClick = { navegar(screen) },
                icon = {
                    Icon(
                        imageVector = icono,
                        contentDescription = screen.title
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        fontSize = 11.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                },
                colors = colores
            )
        }

        NavigationBarItem(
            selected = menuAbierto ||
                    rutaActual == Screen.Ajustes.route ||
                    rutaActual == Screen.Historial.route,
            onClick = { menuAbierto = !menuAbierto },
            icon = {
                Box {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Abrir más opciones"
                    )

                    DropdownMenu(
                        expanded = menuAbierto,
                        onDismissRequest = { menuAbierto = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Ajustes") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null
                                )
                            },
                            onClick = { navegar(Screen.Ajustes) }
                        )

                        DropdownMenuItem(
                            text = { Text("Historial") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.List,
                                    contentDescription = null
                                )
                            },
                            onClick = { navegar(Screen.Historial) }
                        )
                    }
                }
            },
            label = {
                Text(
                    text = "Más",
                    fontSize = 11.sp,
                    maxLines = 1,
                    softWrap = false
                )
            },
            colors = colores
        )
    }
}