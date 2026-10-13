package cl.myciclo.eva.data.local.dao

import androidx.room.*
import cl.myciclo.eva.data.local.entities.InicioMenstrualEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class InicioMenstrualDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertarInicio(inicio: InicioMenstrualEntity)

    @Query("SELECT * FROM inicios_menstruales ORDER BY fechaIso DESC")
    abstract fun obtenerTodosInicios(): Flow<List<InicioMenstrualEntity>>

    @Query("SELECT * FROM inicios_menstruales ORDER BY fechaIso DESC LIMIT 1")
    abstract suspend fun obtenerUltimoInicio(): InicioMenstrualEntity?

    @Query("SELECT COUNT(*) FROM inicios_menstruales WHERE fechaIso = :fechaIso")
    abstract suspend fun contarPorFecha(fechaIso: String): Int

    @Transaction
    open suspend fun insertarSiNoExiste(
        inicio: InicioMenstrualEntity
    ): Boolean {
        if (contarPorFecha(inicio.fechaIso) > 0) return false

        insertarInicio(inicio)
        return true
    }

    @Delete
    abstract suspend fun eliminarInicio(inicio: InicioMenstrualEntity)
}