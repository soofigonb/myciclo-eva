
package cl.myciclo.eva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cl.myciclo.eva.data.MyCicloDatabase
import cl.myciclo.eva.data.repository.CicloRepository
import cl.myciclo.eva.ui.navigation.AppNavigation
import cl.myciclo.eva.ui.theme.MyCicloEvaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización de la base de datos y repositorio del módulo de Jonathan
        val database = MyCicloDatabase.getDatabase(applicationContext)
        val repository = CicloRepository(database.inicioMenstrualDao())

        setContent {
            MyCicloEvaTheme {
                AppNavigation(repository = repository)
            }
        }
    }
}
