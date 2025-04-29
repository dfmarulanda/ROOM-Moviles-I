package com.example.materialexample.data

import androidx.room.*
import com.example.materialexample.model.Tarea
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad Tarea
 */
@Dao
interface TareaDao {
    /**
     * Obtiene todas las tareas ordenadas por ID
     */
    @Query("SELECT * FROM tareas ORDER BY id ASC")
    fun obtenerTodas(): Flow<List<Tarea>>

    /**
     * Obtiene una tarea por su ID
     */
    @Query("SELECT * FROM tareas WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Tarea?

    /**
     * Inserta una nueva tarea
     */
    @Insert
    suspend fun insertar(tarea: Tarea)

    /**
     * Actualiza una tarea existente
     */
    @Update
    suspend fun actualizar(tarea: Tarea)

    /**
     * Elimina una tarea por su ID
     */
    @Query("DELETE FROM tareas WHERE id = :id")
    suspend fun eliminarPorId(id: Int)

    /**
     * Elimina una tarea
     */
    @Delete
    suspend fun eliminar(tarea: Tarea)
} 