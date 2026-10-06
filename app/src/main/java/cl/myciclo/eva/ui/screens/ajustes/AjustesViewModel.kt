package cl.myciclo.eva.ui.screens.ajustes

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import cl.myciclo.eva.utils.SecadoReceiver

class AjustesViewModel : ViewModel() {

    fun programarAvisoSecado(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, SecadoReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Programar la alarma para ejecutarse en 1 hora (3,600,000 milisegundos)
        val tiempoDisparo = System.currentTimeMillis() + (60 * 60 * 1000)
        alarmManager.set(AlarmManager.RTC_WAKEUP, tiempoDisparo, pendingIntent)
    }
}