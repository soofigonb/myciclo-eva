package cl.myciclo.eva.ui.screens.ajustes

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.myciclo.eva.utils.NotificacionHelper
import cl.myciclo.eva.utils.SecadoReceiver
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AjustesViewModel : ViewModel() {
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()

    private var prueba: Job? = null

    private fun avisoPendiente(context: Context): PendingIntent {
        val intent = Intent(context, SecadoReceiver::class.java).apply {
            action = SecadoReceiver.ACCION_SECADO
        }

        return PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun informar(texto: String) {
        _mensaje.value = texto
    }

    fun programarAvisoSecado(context: Context) {
        val appContext = context.applicationContext
        NotificacionHelper.crearCanal(appContext)

        if (!NotificacionHelper.puedeNotificar(appContext)) {
            informar("Activa las notificaciones de EVA en los ajustes del teléfono.")
            return
        }

        try {
            val alarmManager = appContext.getSystemService(
                AlarmManager::class.java
            )

            alarmManager.setAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 60 * 60 * 1000L,
                avisoPendiente(appContext)
            )

            informar("Recordatorio programado para aproximadamente una hora.")
        } catch (e: Exception) {
            informar("No se pudo programar el recordatorio. Intenta nuevamente.")
        }
    }

    fun probarNotificacion(context: Context) {
        val appContext = context.applicationContext
        NotificacionHelper.crearCanal(appContext)

        if (!NotificacionHelper.puedeNotificar(appContext)) {
            informar("Activa las notificaciones de EVA en los ajustes del teléfono.")
            return
        }

        prueba?.cancel()
        prueba = viewModelScope.launch {
            informar("Prueba iniciada. Mantén esta pantalla abierta durante 10 segundos.")
            delay(10_000)

            val enviada = NotificacionHelper.mostrar(
                appContext,
                prueba = true
            )

            informar(
                if (enviada) {
                    "Notificación enviada. Revisa la barra de notificaciones."
                } else {
                    "No se pudo mostrar el aviso. Revisa los permisos."
                }
            )
        }
    }

    fun cancelarAviso(context: Context) {
        prueba?.cancel()

        val appContext = context.applicationContext
        val alarmManager = appContext.getSystemService(
            AlarmManager::class.java
        )

        alarmManager.cancel(avisoPendiente(appContext))
        NotificacionHelper.quitar(appContext)
        informar("Recordatorio cancelado.")
    }
}