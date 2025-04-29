package com.example.materialexample.controller

import android.content.Context
import com.example.materialexample.data.AppDatabase
import com.example.materialexample.model.Tarea
import kotlinx.coroutines.flow.Flow

/**
 * Controlador que maneja la lógica de negocio de las tareas
 */
class TareaController(context: Context) {
    private val tareaDao = AppDatabase.getDatabase(context).tareaDao()

    /**
     * Obtiene todas las tareas
     */
    fun obtenerTareas(): Flow<List<Tarea>> = tareaDao.obtenerTodas()

    /**
     * Agrega una nueva tarea
     */
    suspend fun agregarTarea(descripcion: String) {
        // Room generará automáticamente el ID
        val nuevaTarea = Tarea(id = 0, descripcion = descripcion, completada = false)
        tareaDao.insertar(nuevaTarea)
    }

    /**
     * Marca una tarea como completada
     */
    suspend fun completarTarea(tarea: Tarea) {
        tarea.completada = true
        tareaDao.actualizar(tarea)
    }

    /**
     * Elimina una tarea por su ID
     */
    suspend fun eliminarTarea(id: Int) {
        tareaDao.eliminarPorId(id)
    }
} 