package cl.myciclo.eva.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Patterns
import cl.myciclo.eva.data.local.dao.UsuarioDao
import cl.myciclo.eva.data.local.entities.UsuarioEntity
import cl.myciclo.eva.utils.ContrasenaSegura
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Locale

// Los datos de contraseña no se exponen a las pantallas.
data class UsuarioPerfil(
    val id: Long,
    val nombre: String,
    val correo: String,
    val fechaNacimiento: String? = null,
    val fotoPerfilPath: String? = null
)

class AuthRepository(
    private val usuarioDao: UsuarioDao
) {

    suspend fun registrar(
        nombre: String,
        correo: String,
        contrasena: String,
        confirmacion: String
    ): UsuarioPerfil {
        val nombreLimpio = nombre.trim()
        val correoLimpio = normalizarCorreo(correo)

        require(nombreLimpio.isNotBlank()) {
            "Ingresa tu nombre."
        }

        require(
            Patterns.EMAIL_ADDRESS
                .matcher(correoLimpio)
                .matches()
        ) {
            "Ingresa un correo válido."
        }

        require(contrasena.isNotBlank()) {
            "Ingresa una contraseña."
        }

        require(contrasena.length >= 8) {
            "Utiliza al menos 8 caracteres."
        }

        require(contrasena == confirmacion) {
            "Las contraseñas no coinciden."
        }

        require(
            usuarioDao.obtenerPorCorreo(correoLimpio) == null
        ) {
            "Ya existe una cuenta local con este correo."
        }

        val credencial = withContext(Dispatchers.Default) {
            ContrasenaSegura.crear(contrasena)
        }

        val usuario = UsuarioEntity(
            nombre = nombreLimpio,
            correo = correoLimpio,
            contrasenaHash = credencial.hash,
            sal = credencial.sal
        )

        val id = try {
            usuarioDao.insertar(usuario)
        } catch (e: SQLiteConstraintException) {
            throw IllegalArgumentException(
                "No se pudo crear la cuenta. " +
                        "Comprueba si el correo ya está registrado.",
                e
            )
        }

        return usuario.copy(id = id).toPerfil()
    }

    suspend fun iniciarSesion(
        correo: String,
        contrasena: String
    ): UsuarioPerfil {
        val correoLimpio = normalizarCorreo(correo)

        require(
            Patterns.EMAIL_ADDRESS
                .matcher(correoLimpio)
                .matches()
        ) {
            "Ingresa un correo válido."
        }

        require(contrasena.isNotBlank()) {
            "Ingresa una contraseña."
        }

        val usuario = usuarioDao.obtenerPorCorreo(
            correoLimpio
        ) ?: throw IllegalArgumentException(
            "Correo o contraseña incorrectos."
        )

        val coincide = withContext(Dispatchers.Default) {
            ContrasenaSegura.verificar(
                contrasena = contrasena,
                hashGuardado = usuario.contrasenaHash,
                salGuardada = usuario.sal
            )
        }

        require(coincide) {
            "Correo o contraseña incorrectos."
        }

        return usuario.toPerfil()
    }

    suspend fun obtenerPerfil(id: Long): UsuarioPerfil? {
        return usuarioDao.obtenerPorId(id)?.toPerfil()
    }

    fun observarPerfil(id: Long): Flow<UsuarioPerfil?> {
        return usuarioDao.observarPorId(id).map { usuario ->
            usuario?.toPerfil()
        }
    }

    // Se conserva para compatibilidad con la pantalla anterior.
    suspend fun actualizarNombre(
        id: Long,
        nombre: String
    ) {
        val nombreLimpio = nombre.trim()

        require(nombreLimpio.isNotBlank()) {
            "Ingresa tu nombre."
        }

        val actualizados = usuarioDao.actualizarNombre(
            id = id,
            nombre = nombreLimpio
        )

        require(actualizados == 1) {
            "No se encontró la cuenta."
        }
    }

    suspend fun actualizarPerfil(
        id: Long,
        nombre: String,
        fechaNacimiento: String,
        fotoPerfilPath: String?
    ) {
        require(id > 0L) {
            "La cuenta seleccionada no es válida."
        }

        val nombreLimpio = nombre.trim()

        require(nombreLimpio.isNotBlank()) {
            "Ingresa tu nombre."
        }

        val fecha = runCatching {
            LocalDate.parse(fechaNacimiento)
        }.getOrNull()

        require(fecha != null) {
            "Selecciona tu fecha de nacimiento."
        }

        require(!fecha.isAfter(LocalDate.now())) {
            "La fecha de nacimiento no puede ser futura."
        }

        val actualizados = usuarioDao.actualizarPerfil(
            id = id,
            nombre = nombreLimpio,
            fechaNacimiento = fecha.toString(),
            fotoPerfilPath = fotoPerfilPath
        )

        require(actualizados == 1) {
            "No se encontró la cuenta."
        }
    }

    private fun normalizarCorreo(correo: String): String {
        return correo.trim().lowercase(Locale.ROOT)
    }

    private fun UsuarioEntity.toPerfil(): UsuarioPerfil {
        return UsuarioPerfil(
            id = id,
            nombre = nombre,
            correo = correo,
            fechaNacimiento = fechaNacimiento,
            fotoPerfilPath = fotoPerfilPath
        )
    }
}