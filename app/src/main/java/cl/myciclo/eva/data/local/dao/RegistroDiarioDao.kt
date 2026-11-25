package cl.myciclo.eva.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.myciclo.eva.data.local.entities.RegistroDiarioEntity
import kotlinx.coroutines.flow.Flow

// DAO:
// Aquí declaro las operaciones sobre la tabla.
// Room genera el código que las realiza.
@Dao
interface RegistroDiarioDao {

    // Inserta el registro.
    // Si ya existe la misma clave primaria, sustituye esa fila.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRegistro(registro: RegistroDiarioEntity)

    // Obtiene todos los registros, desde la fecha más reciente.
    // El flujo permite informar los cambios de la tabla.
    @Query("SELECT * FROM registros_diarios ORDER BY fechaIso DESC")
    fun obtenerTodosLosRegistros(): Flow<List<RegistroDiarioEntity>>

    // Busca el registro de una fecha específica.
    // :fecha recibe el valor del parámetro de la función.
    // LIMIT 1 limita la respuesta a una fila.
    // Si no existe, la respuesta puede ser null.
    @Query(
        "SELECT * FROM registros_diarios " +
                "WHERE fechaIso = :fecha LIMIT 1"
    )
    suspend fun obtenerRegistroPorFecha(
        fecha: String
    ): RegistroDiarioEntity?

    // Obtiene las rutas de fotografías asociadas a los registros.
    // El repositorio las utiliza para revisar si una foto sigue en uso.
    @Query(
        "SELECT fotoPath FROM registros_diarios " +
                "WHERE fotoPath IS NOT NULL"
    )
    suspend fun obtenerRutasFotografias(): List<String>

    // Elimina la fila identificada por la clave de esta entidad.
    @Delete
    suspend fun eliminarRegistro(registro: RegistroDiarioEntity)
}