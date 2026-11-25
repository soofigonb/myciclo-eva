package cl.myciclo.eva.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import cl.myciclo.eva.MainActivity

object NotificacionHelper {
    private const val CANAL = "secado_eva"
    private const val ID = 1001

    fun crearCanal(context: Context) {
        val canal = NotificationChannel(
            CANAL,
            "Recordatorios EVA",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Avisos para revisar el secado de la muestra."
        }

        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(canal)
    }

    fun puedeNotificar(context: Context): Boolean {
        val permisoConcedido =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

        val manager = context.getSystemService(
            NotificationManager::class.java
        )

        val canal = manager.getNotificationChannel(CANAL)
        val canalActivo =
            canal == null || canal.importance != NotificationManager.IMPORTANCE_NONE

        return permisoConcedido &&
                NotificationManagerCompat.from(context).areNotificationsEnabled() &&
                canalActivo
    }

    fun mostrar(context: Context, prueba: Boolean = false): Boolean {
        crearCanal(context)
        if (!puedeNotificar(context)) return false

        val abrirApp = PendingIntent.getActivity(
            context,
            ID,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val texto = if (prueba) {
            "La prueba funcionó. Ya puedes recibir recordatorios de EVA."
        } else {
            "Revisa si tu muestra está completamente seca antes de observarla con EVA."
        }

        val notificacion = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(
                if (prueba) "Prueba de recordatorio EVA" else "Revisa tu muestra EVA"
            )
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .setContentIntent(abrirApp)
            .setAutoCancel(true)
            .build()

        return try {
            NotificationManagerCompat.from(context).notify(ID, notificacion)
            true
        } catch (e: SecurityException) {
            false
        }
    }

    fun quitar(context: Context) {
        NotificationManagerCompat.from(context).cancel(ID)
    }
}