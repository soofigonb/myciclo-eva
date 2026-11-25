package cl.myciclo.eva.utils

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import android.os.SystemClock
import android.util.Log

data class CredencialProtegida(
    val hash: String,
    val sal: String
)

object ContrasenaSegura {

    private const val ITERACIONES = 600_000
    private const val LONGITUD_BITS = 256
    private const val ALGORITMO = "PBKDF2WithHmacSHA256"

    fun crear(contrasena: String): CredencialProtegida {
        val sal = ByteArray(16)
        SecureRandom().nextBytes(sal)

        val resultado = derivar(
            contrasena = contrasena,
            sal = sal,
            iteraciones = ITERACIONES
        )

        return CredencialProtegida(
            hash = "pbkdf2_sha256:$ITERACIONES:${codificar(resultado)}",
            sal = codificar(sal)
        )
    }

    fun verificar(
        contrasena: String,
        hashGuardado: String,
        salGuardada: String
    ): Boolean {
        val partes = hashGuardado.split(":")

        if (
            partes.size != 3 ||
            partes[0] != "pbkdf2_sha256"
        ) return false

        val iteraciones = partes[1].toIntOrNull()
            ?: return false

        // Evita procesar parámetros inesperados.
        if (iteraciones !in 600_000..2_000_000) return false

        val sal = decodificar(salGuardada) ?: return false
        val esperado = decodificar(partes[2]) ?: return false

        if (sal.size != 16 || esperado.size != 32) return false

        val resultado = derivar(
            contrasena = contrasena,
            sal = sal,
            iteraciones = iteraciones
        )

        return MessageDigest.isEqual(esperado, resultado)
    }

    private fun derivar(
        contrasena: String,
        sal: ByteArray,
        iteraciones: Int
    ): ByteArray {
        val caracteres = contrasena.toCharArray()

        val especificacion = PBEKeySpec(
            caracteres,
            sal,
            iteraciones,
            LONGITUD_BITS
        )

        val inicio = SystemClock.elapsedRealtime()

        return try {
            val fabrica = SecretKeyFactory.getInstance(ALGORITMO)

            val resultado = fabrica
                .generateSecret(especificacion)
                .encoded

            Log.d(
                "EVA_AUTH_TIEMPO",
                "Cálculo: ${SystemClock.elapsedRealtime() - inicio} ms; " +
                        "proveedor: ${fabrica.provider.name}"
            )

            resultado
        } finally {
            especificacion.clearPassword()
            caracteres.fill('\u0000')
        }
    }


    private fun codificar(datos: ByteArray): String {
        return Base64.encodeToString(datos, Base64.NO_WRAP)
    }

    private fun decodificar(texto: String): ByteArray? {
        return try {
            Base64.decode(texto, Base64.NO_WRAP)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}