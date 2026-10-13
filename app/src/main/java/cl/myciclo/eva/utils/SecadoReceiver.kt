package cl.myciclo.eva.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SecadoReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACCION_SECADO) {
            NotificacionHelper.mostrar(context)
        }
    }

    companion object {
        const val ACCION_SECADO = "cl.myciclo.eva.AVISO_SECADO"
    }
}