package cl.myciclo.eva.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import cl.myciclo.eva.ui.navigation.Screen
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Inicio to Icons.Default.Home,
        Screen.Calendario to Icons.Default.DateRange,
        Screen.Historial to Icons.Default.List,
        Screen.Aprender to Icons.Default.Info,
        Screen.Ajustes to Icons.Default.Settings
    )

    NavigationBar {
        val navBackStackEntry = navController.currentBackStackEntryAsState().value
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { (screen, icon) ->
            NavigationBarItem(
                icon = { Icon(imageVector = icon, contentDescription = screen.title) },
                label = {
                    Text(
                        text = screen.title,
                        fontSize = 11.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Inicio.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF7B2CBF),
                    selectedTextColor = Color(0xFF7B2CBF),
                    indicatorColor = Color(0xFFE9D5FF),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}