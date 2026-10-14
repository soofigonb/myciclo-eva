package cl.myciclo.eva.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.myciclo.eva.data.local.entities.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun obtenerPorCorreo(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Long): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    fun observarPorId(id: Long): Flow<UsuarioEntity?>

    @Query("UPDATE usuarios SET nombre = :nombre WHERE id = :id")
    suspend fun actualizarNombre(id: Long, nombre: String): Int

    @Query(
        """
        UPDATE usuarios
        SET nombre = :nombre,
            fechaNacimiento = :fechaNacimiento,
            fotoPerfilPath = :fotoPerfilPath
        WHERE id = :id
        """
    )
    suspend fun actualizarPerfil(
        id: Long,
        nombre: String,
        fechaNacimiento: String,
        fotoPerfilPath: String?
    ): Int
}