package com.equipo.myciclo.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inicios_menstruales")
data class InicioMenstrualEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fechaIso: String // Formato YYYY-MM-DD
)