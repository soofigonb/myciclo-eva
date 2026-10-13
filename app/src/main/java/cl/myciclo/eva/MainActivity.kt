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

        val database = MyCicloDatabase.getDatabase(
            applicationContext
        )

        val repository = CicloRepository(
            inicioDao = database.inicioMenstrualDao(),
            registroDiarioDao = database.registroDiarioDao(),
            context = applicationContext
        )

        setContent {
            MyCicloEVATheme {
                AppNavigation(repository = repository)
            }
        }
    }
}