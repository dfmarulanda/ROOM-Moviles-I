package com.example.materialexample.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modelo que representa una tarea
 * @property id Identificador único de la tarea
 * @property descripcion Descripción de la tarea
 * @property completada Estado de la tarea
 */
@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val descripcion: String,
    var completada: Boolean
) 