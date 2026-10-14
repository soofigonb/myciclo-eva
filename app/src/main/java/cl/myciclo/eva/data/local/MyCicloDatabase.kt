package cl.myciclo.eva.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.myciclo.eva.data.local.dao.InicioMenstrualDao
import cl.myciclo.eva.data.local.dao.RegistroDiarioDao
import cl.myciclo.eva.data.local.dao.UsuarioDao
import cl.myciclo.eva.data.local.entities.InicioMenstrualEntity
import cl.myciclo.eva.data.local.entities.RegistroDiarioEntity
import cl.myciclo.eva.data.local.entities.UsuarioEntity

@Database(
    entities = [
        InicioMenstrualEntity::class,
        RegistroDiarioEntity::class,
        UsuarioEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class MyCicloDatabase : RoomDatabase() {

    abstract fun inicioMenstrualDao(): InicioMenstrualDao

    abstract fun registroDiarioDao(): RegistroDiarioDao

    abstract fun usuarioDao(): UsuarioDao

    companion object {

        private const val BASE_PRINCIPAL = "myciclo_database"

        private val instancias =
            mutableMapOf<String, MyCicloDatabase>()

        // Conserva los registros anteriores y agrega las cuentas.
        private val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS usuarios (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL,
                        correo TEXT NOT NULL,
                        contrasenaHash TEXT NOT NULL,
                        sal TEXT NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS index_usuarios_correo
                    ON usuarios (correo)
                    """.trimIndent()
                )
            }
        }

        // Agrega los nuevos datos del perfil sin borrar cuentas.
        private val MIGRATION_2_3 = object : Migration(2, 3) {

            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE usuarios " +
                            "ADD COLUMN fechaNacimiento TEXT"
                )

                db.execSQL(
                    "ALTER TABLE usuarios " +
                            "ADD COLUMN fotoPerfilPath TEXT"
                )
            }
        }

        /*
         * Base principal:
         * - Catálogo de cuentas locales.
         * - Registros anteriores del modo invitada.
         */
        fun getDatabase(context: Context): MyCicloDatabase {
            return abrirBase(
                context = context,
                nombre = BASE_PRINCIPAL
            )
        }

        /*
         * Cada cuenta tiene su propia base de ciclos y bitácoras.
         * El identificador 0 corresponde al modo invitada.
         */
        fun getDatabasePorUsuario(
            context: Context,
            usuarioId: Long
        ): MyCicloDatabase {
            require(usuarioId >= 0L) {
                "El identificador de usuario no es válido."
            }

            val nombre = if (usuarioId == 0L) {
                BASE_PRINCIPAL
            } else {
                "myciclo_usuario_$usuarioId"
            }

            return abrirBase(
                context = context,
                nombre = nombre
            )
        }

        private fun abrirBase(
            context: Context,
            nombre: String
        ): MyCicloDatabase {
            return synchronized(this) {
                instancias.getOrPut(nombre) {
                    Room.databaseBuilder(
                        context.applicationContext,
                        MyCicloDatabase::class.java,
                        nombre
                    )
                        .addMigrations(
                            MIGRATION_1_2,
                            MIGRATION_2_3
                        )
                        .build()
                }
            }
        }
    }
}