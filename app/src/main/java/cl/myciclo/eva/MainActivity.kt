package cl.myciclo.eva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cl.myciclo.eva.data.local.MyCicloDatabase
import cl.myciclo.eva.data.repository.CicloRepository
import cl.myciclo.eva.ui.navigation.AppNavigation
import cl.myciclo.eva.ui.theme.MyCicloEVATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Obtención de instancia de la base de datos de Room
        val database = MyCicloDatabase.getDatabase(applicationContext)

        // 2. Creación del repositorio pasando el DAO que pide Room
        val repository = CicloRepository(
            inicioDao = database.inicioMenstrualDao(),
            registroDiarioDao = database.registroDiarioDao()
        )

        setContent {
            MyCicloEVATheme {
                // Pasamos el repositorio a navegación o pantallas principales
                AppNavigation(repository = repository)
            }
        }
    }
}