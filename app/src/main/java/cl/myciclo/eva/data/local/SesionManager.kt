package cl.myciclo.eva.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException

// Una única instancia de DataStore para este archivo.
private val Context.sesionDataStore by preferencesDataStore(
    name = "sesion_eva"
)

class SesionManager(context: Context) {

    private val dataStore =
        context.applicationContext.sesionDataStore

    private val usuarioIdKey = longPreferencesKey(
        "usuario_id"
    )

    /*
     * null: sin sesión.
     * 0: modo invitada.
     * Mayor que 0: identificador de una cuenta local.
     */
    val usuarioId: Flow<Long?> = dataStore.data
        .catch { error ->
            if (error is IOException) {
                emit(emptyPreferences())
            } else {
                throw error
            }
        }
        .map { preferencias ->
            preferencias[usuarioIdKey]?.takeIf {
                it >= 0L
            }
        }
        .distinctUntilChanged()

    suspend fun iniciarSesion(usuarioId: Long) {
        require(usuarioId > 0L) {
            "La cuenta seleccionada no es válida."
        }

        dataStore.edit { preferencias ->
            preferencias[usuarioIdKey] = usuarioId
        }
    }

    suspend fun entrarComoInvitada() {
        dataStore.edit { preferencias ->
            preferencias[usuarioIdKey] = 0L
        }
    }

    suspend fun cerrarSesion() {
        dataStore.edit { preferencias ->
            preferencias.remove(usuarioIdKey)
        }
    }
}