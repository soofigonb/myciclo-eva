package cl.myciclo.eva.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import cl.myciclo.eva.ui.navigation.Screen
import androidx.compose.material3.NavigationBar
import cl.myciclo.eva.ui.theme.MoradoEva
import cl.myciclo.eva.ui.theme.RosaAcentoEva

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Inicio to Icons.Default.Home,
        Screen.Calendario to Icons.Default.DateRange,
        Screen.Historial to Icons.Default.List,
        Screen.Ajustes to Icons.Default.Settings
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 10.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { (screen, icon) ->
            NavigationBarItem(
                icon = { Icon(imageVector = icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
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
                    selectedIconColor = Color.White,
                    selectedTextColor = MoradoEva,
                    indicatorColor = MoradoEva,
                    unselectedIconColor = Color(0xFFB8A9BE),
                    unselectedTextColor = Color(0xFFB8A9BE)
                )
            )
        }
    }
}