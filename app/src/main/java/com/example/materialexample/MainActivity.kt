package com.example.materialexample

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.materialexample.controller.TareaController
import com.example.materialexample.model.Tarea
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var tareaController: TareaController
    private lateinit var listaTareas: ListView
    private lateinit var editTextTarea: EditText
    private lateinit var botonAgregar: Button
    private lateinit var adaptador: ArrayAdapter<String>
    private var tareasActuales: List<Tarea> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar el controlador
        tareaController = TareaController(this)

        // Obtener referencias a las vistas
        listaTareas = findViewById(R.id.listaTareas)
        editTextTarea = findViewById(R.id.editTextTarea)
        botonAgregar = findViewById(R.id.botonAgregar)

        // Configurar el adaptador para la lista
        adaptador = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        listaTareas.adapter = adaptador

        // Observar cambios en la lista de tareas
        lifecycleScope.launch {
            tareaController.obtenerTareas().collectLatest { tareas ->
                tareasActuales = tareas
                actualizarLista(tareas)
            }
        }

        // Configurar el botón de agregar
        botonAgregar.setOnClickListener {
            val descripcion = editTextTarea.text.toString()
            if (descripcion.isNotBlank()) {
                lifecycleScope.launch {
                    tareaController.agregarTarea(descripcion)
                    editTextTarea.text.clear()
                }
            }
        }

        // Configurar el clic largo para eliminar
        listaTareas.setOnItemLongClickListener { _, _, position, _ ->
            val tarea = tareasActuales.getOrNull(position)
            tarea?.let {
                lifecycleScope.launch {
                    tareaController.eliminarTarea(it.id)
                }
            }
            true
        }

        // Configurar el clic simple para marcar como completada
        listaTareas.setOnItemClickListener { _, _, position, _ ->
            val tarea = tareasActuales.getOrNull(position)
            tarea?.let {
                lifecycleScope.launch {
                    tareaController.completarTarea(it)
                }
            }
        }
    }

    /**
     * Actualiza la lista de tareas en la interfaz
     */
    private fun actualizarLista(tareas: List<Tarea>) {
        adaptador.clear()
        tareas.forEach { tarea ->
            val estado = if (tarea.completada) "[✓]" else "[ ]"
            adaptador.add("$estado ${tarea.descripcion}")
        }
    }
}
