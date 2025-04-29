# Ejemplo de Room Database con MVC

Este proyecto es un ejemplo de implementación de una base de datos local usando Room en Android, siguiendo el patrón MVC (Model-View-Controller). La aplicación permite gestionar una lista de tareas con persistencia de datos.

## Características

- Crear, leer, actualizar y eliminar tareas (CRUD)
- Persistencia de datos local con Room
- Patrón de diseño MVC
- Interfaz de usuario simple y funcional
- Uso de corrutinas y Flow para operaciones asíncronas

## Estructura del Proyecto

```
app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/materialexample/
│   │   │       ├── model/
│   │   │       │   └── Tarea.kt
│   │   │       ├── data/
│   │   │       │   ├── TareaDao.kt
│   │   │       │   └── AppDatabase.kt
│   │   │       ├── controller/
│   │   │       │   └── TareaController.kt
│   │   │       └── MainActivity.kt
│   │   └── res/
│   │       └── layout/
│   │           └── activity_main.xml
```

## Guía Paso a Paso

### 1. Configuración Inicial

1. Agrega las dependencias de Room en `build.gradle.kts`:
```kotlin
plugins {
    id("com.google.devtools.ksp") version "2.0.0-1.0.22"
}

dependencies {
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
}
```

### 2. Creación del Modelo (Tarea.kt)

```kotlin
@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val descripcion: String,
    var completada: Boolean
)
```

- `@Entity`: Marca la clase como una entidad de Room
- `@PrimaryKey`: Indica que `id` es la clave primaria
- `autoGenerate = true`: Room generará automáticamente los IDs

### 3. Creación del DAO (TareaDao.kt)

```kotlin
@Dao
interface TareaDao {
    @Query("SELECT * FROM tareas ORDER BY id ASC")
    fun obtenerTodas(): Flow<List<Tarea>>

    @Query("SELECT * FROM tareas WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Tarea?

    @Insert
    suspend fun insertar(tarea: Tarea)

    @Update
    suspend fun actualizar(tarea: Tarea)

    @Query("DELETE FROM tareas WHERE id = :id")
    suspend fun eliminarPorId(id: Int)
}
```

- `@Dao`: Marca la interfaz como un DAO de Room
- `@Query`: Define consultas SQL personalizadas
- `@Insert`, `@Update`, `@Delete`: Anotaciones para operaciones CRUD

### 4. Creación de la Base de Datos (AppDatabase.kt)

```kotlin
@Database(entities = [Tarea::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tareaDao(): TareaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

- `@Database`: Marca la clase como una base de datos Room
- Patrón Singleton para asegurar una única instancia

### 5. Creación del Controlador (TareaController.kt)

```kotlin
class TareaController(context: Context) {
    private val tareaDao = AppDatabase.getDatabase(context).tareaDao()

    fun obtenerTareas(): Flow<List<Tarea>> = tareaDao.obtenerTodas()

    suspend fun agregarTarea(descripcion: String) {
        val nuevaTarea = Tarea(id = 0, descripcion = descripcion, completada = false)
        tareaDao.insertar(nuevaTarea)
    }

    suspend fun completarTarea(tarea: Tarea) {
        tarea.completada = true
        tareaDao.actualizar(tarea)
    }

    suspend fun eliminarTarea(id: Int) {
        tareaDao.eliminarPorId(id)
    }
}
```

### 6. Implementación de la Vista (MainActivity.kt)

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var tareaController: TareaController
    private var tareasActuales: List<Tarea> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tareaController = TareaController(this)

        // Observar cambios en la lista de tareas
        lifecycleScope.launch {
            tareaController.obtenerTareas().collectLatest { tareas ->
                tareasActuales = tareas
                actualizarLista(tareas)
            }
        }

        // Configurar botones y listeners
        // ...
    }
}
```

## Cómo Usar la Aplicación

1. **Agregar Tarea**:
   - Escribe la descripción en el campo de texto
   - Presiona el botón "Agregar Tarea"

2. **Marcar como Completada**:
   - Toca una tarea para marcarla como completada
   - Se mostrará un [✓] al inicio de la tarea

3. **Eliminar Tarea**:
   - Mantén presionada una tarea para eliminarla

## Conceptos Importantes

1. **Room Database**:
   - ORM (Object-Relational Mapping) para SQLite
   - Compile-time verification de consultas SQL
   - Conversión automática de objetos a tablas

2. **Corrutinas y Flow**:
   - Operaciones asíncronas sin bloqueo
   - Flujos de datos reactivos
   - Manejo automático del ciclo de vida

3. **Patrón MVC**:
   - Model: Estructura de datos (Tarea)
   - View: Interfaz de usuario (MainActivity)
   - Controller: Lógica de negocio (TareaController)

## Mejoras Posibles

1. Agregar validación de datos
2. Implementar búsqueda y filtrado
3. Agregar categorías a las tareas
4. Implementar notificaciones
5. Agregar sincronización con backend

## Recursos Adicionales

- [Documentación oficial de Room](https://developer.android.com/training/data-storage/room)
- [Guía de corrutinas](https://developer.android.com/kotlin/coroutines)
- [Documentación de Flow](https://developer.android.com/kotlin/flow) 