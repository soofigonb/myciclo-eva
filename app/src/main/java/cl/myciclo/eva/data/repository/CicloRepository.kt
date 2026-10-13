package cl.myciclo.eva.data.repository

import android.content.Context
import android.util.Log
import cl.myciclo.eva.data.local.dao.InicioMenstrualDao
import cl.myciclo.eva.data.local.dao.RegistroDiarioDao
import cl.myciclo.eva.data.local.entities.InicioMenstrualEntity
import cl.myciclo.eva.data.local.entities.RegistroDiarioEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

class CicloRepository(
    private val inicioDao: InicioMenstrualDao,
    private val registroDiarioDao: RegistroDiarioDao,
    context: Context
) {
    private val carpetaFotografias = File(
        context.applicationContext.filesDir,
        "muestras_eva"
    )

    // Evita que guardar y eliminar fotografías se ejecuten a la vez.
    private val cambiosDiarios = Mutex()

    val todosLosInicios: Flow<List<InicioMenstrualEntity>> =
        inicioDao.obtenerTodosInicios()

    val todosLosRegistrosDiarios: Flow<List<RegistroDiarioEntity>> =
        registroDiarioDao.obtenerTodosLosRegistros()

    suspend fun registrarInicioMenstrual(fechaIso: String) {
        val insertado = inicioDao.insertarSiNoExiste(
            InicioMenstrualEntity(fechaIso = fechaIso)
        )

        require(insertado) {
            "Ya registraste un inicio menstrual para esta fecha."
        }
    }

    suspend fun obtenerUltimoInicio(): InicioMenstrualEntity? {
        return inicioDao.obtenerUltimoInicio()
    }

    suspend fun eliminarInicioMenstrual(fechaIso: String) {
        inicioDao.eliminarPorFecha(fechaIso)
    }

    suspend fun obtenerRegistroPorFecha(
        fechaIso: String
    ): RegistroDiarioEntity? {
        return registroDiarioDao.obtenerRegistroPorFecha(fechaIso)
    }

    suspend fun registrarDiario(registro: RegistroDiarioEntity) {
        cambiosDiarios.withLock {
            val anterior = registroDiarioDao.obtenerRegistroPorFecha(
                registro.fechaIso
            )

            // Primero guarda el registro. Si falla, conserva la foto anterior.
            registroDiarioDao.insertarRegistro(registro)

            if (anterior?.fotoPath != registro.fotoPath) {
                limpiarFotografiaSinUso(anterior?.fotoPath)
            }
        }
    }

    suspend fun eliminarRegistroDiario(
        registro: RegistroDiarioEntity
    ) {
        cambiosDiarios.withLock {
            // Consulta la versión guardada para obtener su foto actual.
            val guardado = registroDiarioDao.obtenerRegistroPorFecha(
                registro.fechaIso
            ) ?: return@withLock

            // Primero elimina el registro. Si falla, conserva la fotografía.
            registroDiarioDao.eliminarRegistro(guardado)

            limpiarFotografiaSinUso(guardado.fotoPath)
        }
    }

    private suspend fun limpiarFotografiaSinUso(ruta: String?) {
        if (ruta.isNullOrBlank()) return

        // Completa esta limpieza breve aunque la pantalla se cierre.
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                val archivo = fotografiaPrivada(ruta)
                    ?: return@withContext

                val rutasGuardadas =
                    registroDiarioDao.obtenerRutasFotografias()

                val sigueEnUso = rutasGuardadas.any { otraRuta ->
                    fotografiaPrivada(otraRuta) == archivo
                }

                if (sigueEnUso) return@withContext

                if (archivo.exists() && !archivo.delete()) {
                    Log.w(
                        "CicloRepository",
                        "No se pudo eliminar una fotografía sin uso."
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // El cambio en Room ya terminó correctamente.
                // Un fallo de limpieza no se presenta como fallo del guardado.
                Log.w(
                    "CicloRepository",
                    "No se pudo completar la limpieza de fotografías.",
                    e
                )
            }
        }
    }

    private fun fotografiaPrivada(ruta: String): File? {
        if (ruta.isBlank()) return null

        val archivo = File(ruta)

        // Ignora referencias antiguas como content:// y rutas externas.
        if (!archivo.isAbsolute) return null

        val carpetaReal = carpetaFotografias.canonicalFile
        val archivoReal = archivo.canonicalFile

        // Solo permite archivos directamente dentro de muestras_eva.
        if (archivoReal.parentFile != carpetaReal) return null
        if (!archivoReal.isFile) return null

        return archivoReal
    }
}