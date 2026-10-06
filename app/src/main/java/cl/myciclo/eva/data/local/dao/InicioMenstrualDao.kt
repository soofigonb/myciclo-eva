package cl.myciclo.eva.data.dao

import androidx.room.*
import com.equipo.myciclo.data.local.entities.InicioMenstrualEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InicioMenstrualDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarInicio(inicio: InicioMenstrualEntity)

    @Query("SELECT * FROM inicios_menstruales ORDER BY fechaIso DESC")
    fun obtenerTodosInicios(): Flow<List<InicioMenstrualEntity>>

    @Query("SELECT * FROM inicios_menstruales ORDER BY fechaIso DESC LIMIT 1")
    suspend fun obtenerUltimoInicio(): InicioMenstrualEntity?

    @Delete
    suspend fun eliminarInicio(inicio: InicioMenstrualEntity)
}