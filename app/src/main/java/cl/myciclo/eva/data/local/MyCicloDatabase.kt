package cl.myciclo.eva.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.myciclo.eva.data.dao.InicioMenstrualDao
import cl.myciclo.eva.data.entities.InicioMenstrualEntity
import com.equipo.myciclo.data.local.entities.RegistroDiarioEntity

@Database(
    entities = [InicioMenstrualEntity::class,
                RegistroDiarioEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MyCicloDatabase : RoomDatabase() {
    abstract fun inicioMenstrualDao(): InicioMenstrualDao

    companion object {
        @Volatile
        private var INSTANCE: MyCicloDatabase? = null

        fun getDatabase(context: Context): MyCicloDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyCicloDatabase::class.java,
                    "myciclo_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}