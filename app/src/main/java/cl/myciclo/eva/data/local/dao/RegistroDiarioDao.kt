package cl.myciclo.eva.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.myciclo.eva.data.local.entities.RegistroDiarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDiarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRegistro(registro: RegistroDiarioEntity)

    @Query(value = "SELECT * FROM registros_diarios ORDER BY fechaIso DESC")
    fun obtenerTodosLosRegistros(): Flow<List<RegistroDiarioEntity>>

    @Query(value = "SELECT * FROM registros_diarios WHERE fechaIso = :fecha LIMIT 1")
    suspend fun obtenerRegistroPorFecha(fecha: String): RegistroDiarioEntity?

    @Delete
    suspend fun eliminarRegistro(registro: RegistroDiarioEntity)
}