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

// REPOSITORIO:
// Recibo solicitudes de los ViewModel y coordino
// las operaciones de datos y los archivos relacionados.
class CicloRepository(
    private val inicioDao: InicioMenstrualDao,
    private val registroDiarioDao: RegistroDiarioDao,
    context: Context
) {
    // Esta es la ubicación privada de las fotografías de muestras.
    private val carpetaFotografias = File(
        context.applicationContext.filesDir,
        "muestras_eva"
    )

    // Da un turno a la vez a las operaciones que usan este bloqueo
    // dentro de esta misma instancia del repositorio.
    private val cambiosDiarios = Mutex()

    // Estas listas pueden informar cambios a quienes las observan.
    val todosLosInicios: Flow<List<InicioMenstrualEntity>> =
        inicioDao.obtenerTodosInicios()

    val todosLosRegistrosDiarios: Flow<List<RegistroDiarioEntity>> =
        registroDiarioDao.obtenerTodosLosRegistros()

    suspend fun registrarInicioMenstrual(fechaIso: String) {
        // El DAO comprueba si esta fecha ya estaba registrada.
        val insertado = inicioDao.insertarSiNoExiste(
            InicioMenstrualEntity(fechaIso = fechaIso)
        )

        // Si no se insertó porque ya existía, informo el motivo.
        require(insertado) {
            "Ya registraste un inicio menstrual para esta fecha."
        }
    }

    suspend fun obtenerUltimoInicio(): InicioMenstrualEntity? {
        // Pido al DAO el inicio más reciente.
        return inicioDao.obtenerUltimoInicio()
    }

    suspend fun eliminarInicioMenstrual(fechaIso: String) {
        // Eliminar un inicio es una operación distinta
        // de eliminar una bitácora diaria.
        inicioDao.eliminarPorFecha(fechaIso)
    }

    suspend fun obtenerRegistroPorFecha(
        fechaIso: String
    ): RegistroDiarioEntity? {
        // Consulto al DAO y devuelvo el registro encontrado.
        // Puede ser null si no hay registro de esa fecha.
        return registroDiarioDao.obtenerRegistroPorFecha(fechaIso)
    }

    suspend fun registrarDiario(registro: RegistroDiarioEntity) {
        cambiosDiarios.withLock {
            // Busco la versión anterior para conocer su fotografía.
            val anterior = registroDiarioDao.obtenerRegistroPorFecha(
                registro.fechaIso
            )

            // Primero guardo el registro.
            // Si esto falla, todavía no intenté borrar la foto anterior.
            registroDiarioDao.insertarRegistro(registro)

            // Si cambió la foto o se quitó,
            // reviso si la anterior puede limpiarse.
            if (anterior?.fotoPath != registro.fotoPath) {
                limpiarFotografiaSinUso(anterior?.fotoPath)
            }
        }
    }

    suspend fun eliminarRegistroDiario(
        registro: RegistroDiarioEntity
    ) {
        cambiosDiarios.withLock {
            // Consulto la versión que está realmente guardada.
            // Así obtengo su fotografía actual.
            // Si ya no existe, salgo de este bloque.
            val guardado = registroDiarioDao.obtenerRegistroPorFecha(
                registro.fechaIso
            ) ?: return@withLock

            // Primero elimino el registro de la base.
            // Si falla, no continúo con la limpieza de su foto.
            registroDiarioDao.eliminarRegistro(guardado)

            // Después intento limpiar el archivo asociado.
            limpiarFotografiaSinUso(guardado.fotoPath)
        }
    }

    private suspend fun limpiarFotografiaSinUso(ruta: String?) {
        // Sin una ruta con contenido, no hay nada que revisar.
        if (ruta.isNullOrBlank()) return

        // Realizo la limpieza de archivos en el contexto adecuado.
        // Protejo este bloque de la cancelación normal de la tarea.
        // Esto no lo protege de que se termine el proceso de la app.
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                // Solo continúo si corresponde a un archivo permitido.
                val archivo = fotografiaPrivada(ruta)
                    ?: return@withContext

                // Consulto las fotos todavía asociadas
                // a registros de esta base de datos.
                val rutasGuardadas =
                    registroDiarioDao.obtenerRutasFotografias()

                // Compruebo si alguna referencia apunta al mismo archivo.
                val sigueEnUso = rutasGuardadas.any { otraRuta ->
                    fotografiaPrivada(otraRuta) == archivo
                }

                // Si todavía está referenciado, lo conservo.
                if (sigueEnUso) return@withContext

                // Intento borrarlo.
                // Si el sistema no pudo, dejo un aviso para revisarlo.
                if (archivo.exists() && !archivo.delete()) {
                    Log.w(
                        "CicloRepository",
                        "No se pudo eliminar una fotografía sin uso."
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // El cambio en la base ya terminó.
                // Un problema al limpiar la foto se registra,
                // sin presentarlo como un fallo de ese guardado.
                Log.w(
                    "CicloRepository",
                    "No se pudo completar la limpieza de fotografías.",
                    e
                )
            }
        }
    }

    private fun fotografiaPrivada(ruta: String): File? {
        // No acepto rutas vacías.
        if (ruta.isBlank()) return null

        val archivo = File(ruta)

        // Solo proceso rutas completas de archivos.
        // Referencias como content:// no se procesan aquí.
        if (!archivo.isAbsolute) return null

        // Obtengo las rutas resueltas para comparar sus ubicaciones.
        val carpetaReal = carpetaFotografias.canonicalFile
        val archivoReal = archivo.canonicalFile

        // Solo permito archivos directamente dentro de muestras_eva.
        if (archivoReal.parentFile != carpetaReal) return null

        // También compruebo que sea un archivo existente.
        if (!archivoReal.isFile) return null

        return archivoReal
    }
}