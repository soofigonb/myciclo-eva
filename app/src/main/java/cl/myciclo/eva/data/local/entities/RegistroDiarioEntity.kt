package cl.myciclo.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros_diarios")
data class RegistroDiarioEntity(
    @PrimaryKey val fechaIso: String, // Clave primaria: YYYY-MM-DD
    val patron: String = "", // "Helechos", "Sin Helechos", etc.
    val sintomas: String = "",
    val estadoAnimo: String = "",
    val flujoMenstrual: String = "",
    val flujoVaginal: String = "",
    val notas: String = "",
    val fotoPath: String? = null
)