package cl.myciclo.eva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cl.myciclo.eva.data.local.MyCicloDatabase
import cl.myciclo.eva.data.local.SesionManager
import cl.myciclo.eva.data.repository.AuthRepository
import cl.myciclo.eva.ui.navigation.AppNavigation
import cl.myciclo.eva.ui.theme.MyCicloEVATheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = MyCicloDatabase.getDatabase(
            applicationContext
        )

        val authRepository = AuthRepository(
            usuarioDao = database.usuarioDao()
        )

        val sesionManager = SesionManager(
            applicationContext
        )

        setContent {
            MyCicloEVATheme {
                AppNavigation(
                    authRepository = authRepository,
                    sesionManager = sesionManager
                )
            }
        }
    }
}