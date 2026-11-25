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

// BASE DE DATOS:
// Declaro las entidades que forman sus tablas.
@Database(
    entities = [
        InicioMenstrualEntity::class,
        RegistroDiarioEntity::class,
        UsuarioEntity::class
    ],

    // Versión de la estructura de las tablas.
    version = 3,

    // En esta configuración no se exportan archivos del esquema.
    exportSchema = false
)
abstract class MyCicloDatabase : RoomDatabase() {

    // Estas funciones permiten obtener los DAO.
    // Room genera sus implementaciones.
    abstract fun inicioMenstrualDao(): InicioMenstrualDao

    abstract fun registroDiarioDao(): RegistroDiarioDao

    abstract fun usuarioDao(): UsuarioDao

    companion object {

        // Nombre de la base principal.
        private const val BASE_PRINCIPAL = "myciclo_database"

        // Recuerdo las instancias por nombre para reutilizarlas.
        private val instancias =
            mutableMapOf<String, MyCicloDatabase>()

        // Al pasar de versión 1 a 2, agrego las cuentas.
        // Las tablas anteriores se conservan.
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

                // Evito dos cuentas con exactamente el mismo
                // correo almacenado.
                db.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS index_usuarios_correo
                    ON usuarios (correo)
                    """.trimIndent()
                )
            }
        }

        // Al pasar de versión 2 a 3, agrego datos del perfil.
        // Las cuentas existentes se conservan.
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

        // La base principal se utiliza para las cuentas
        // y los registros del modo invitada.
        fun getDatabase(context: Context): MyCicloDatabase {
            return abrirBase(
                context = context,
                nombre = BASE_PRINCIPAL
            )
        }

        // Elijo una base de ciclos según la cuenta.
        // Esto mantiene separados sus registros.
        fun getDatabasePorUsuario(
            context: Context,
            usuarioId: Long
        ): MyCicloDatabase {
            require(usuarioId >= 0L) {
                "El identificador de usuario no es válido."
            }

            // El cero representa el modo invitada.
            // Una cuenta utiliza un nombre que incluye su identificador.
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

            // Protejo la búsqueda y creación de instancias
            // frente a llamadas simultáneas.
            return synchronized(this) {

                // Si ya existe esta instancia, la reutilizo.
                // Si no existe, ejecuto el bloque para prepararla.
                instancias.getOrPut(nombre) {

                    // Indico a Room qué clase y nombre debe utilizar.
                    Room.databaseBuilder(
                        context.applicationContext,
                        MyCicloDatabase::class.java,
                        nombre
                    )

                        // Registro los pasos para actualizar
                        // bases que tengan versiones anteriores.
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