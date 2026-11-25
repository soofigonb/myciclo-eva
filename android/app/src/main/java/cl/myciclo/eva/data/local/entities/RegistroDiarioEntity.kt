package cl.myciclo.eva.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// ENTIDAD:
// Esta clase describe una fila de la tabla de bitácoras.
@Entity(tableName = "registros_diarios")
data class RegistroDiarioEntity(

    // La fecha identifica el registro dentro de esta base.
    // No pueden existir dos filas con la misma fecha.
    @PrimaryKey
    val fechaIso: String,

    // La selección observada.
    // El ViewModel comprueba el valor antes de guardar.
    val patron: String = "",

    // Datos escritos en el formulario.
    val sintomas: String = "",
    val estadoAnimo: String = "",
    val flujoMenstrual: String = "",
    val flujoVaginal: String = "",
    val notas: String = "",

    // Se guarda la ruta del archivo, no los píxeles de la imagen.
    // null permite que el registro no tenga fotografía.
    val fotoPath: String? = null
)