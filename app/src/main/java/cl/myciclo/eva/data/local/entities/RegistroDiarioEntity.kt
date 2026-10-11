package cl.myciclo.eva.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros_diarios")
data class RegistroDiarioEntity(
    @PrimaryKey val fechaIso: String, // Clave primaria: YYYY-MM-DD
    val patron: String = "",          // "Gotas", "Helechos"
    val sintomas: String = "",        // Síntomas físicos
    val estadoAnimo: String = "",     // Estado de ánimo
    val flujoMenstrual: String = "",  // Flujo menstrual
    val flujoVaginal: String = "",    // Flujo vaginal
    val notas: String = "",           // Notas u observaciones
    val fotoPath: String? = null      // Ruta o URI de la fotografía adjunta
)