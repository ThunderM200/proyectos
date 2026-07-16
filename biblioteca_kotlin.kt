import java.lang.Exception

// Representa la información de cada obra
data class Libro(
    val nombre: String,
    val autor: String,
    val publicacion: String,
    var disponible: Boolean = true
)

// Representa al usuario.
// libroPrestado es un tipo "Int?" (nullable) para emular el "Option<usize>" de Rust.
data class Estudiante(
    val nombre: String,
    var libroPrestado: Int? = null
)

// Almacena la colección de libros y sus operaciones
class Biblioteca {
    private val libros = mutableListOf<Libro>()

    // Registra un nuevo libro en el sistema
    fun crearLibro(nombre: String, autor: String, publicacion: String) {
        libros.add(Libro(nombre, autor, publicacion))
    }

    // Gestiona el préstamo del libro solicitado
    fun pedirLibro(nombre: String, estudiante: Estudiante): Result<Unit> {
        if (estudiante.libroPrestado != null) {
            return Result.failure(Exception("--- El estudiante ya tiene un libro prestado."))
        }

        for ((i, libro) in libros.withIndex()) {
            if (libro.nombre == nombre) {
                if (!libro.disponible) {
                    return Result.failure(Exception("--- Ese libro ya está prestado."))
                }

                libro.disponible = false
                estudiante.libroPrestado = i
                return Result.success(Unit)
            }
        }

        return Result.failure(Exception("--- Libro no encontrado."))
    }

    // Devuelve el libro que el estudiante tiene actualmente asignado
    fun devolverLibro(estudiante: Estudiante): Result<Unit> {
        val indice = estudiante.libroPrestado
            ?: return Result.failure(Exception("--- El estudiante no tiene libros prestados."))

        libros[indice].disponible = true
        estudiante.libroPrestado = null
        return Result.success(Unit)
    }

    // Muestra por consola el inventario actual
    fun mostrarLibros() {
        println("\n=== Biblioteca ===")
        for (libro in libros) {
            val estado = if (libro.disponible) "Disponible" else "Prestado"
            println("${libro.nombre} - ${libro.autor} (${libro.publicacion}) [$estado]")
        }
        println()
    }
}

fun main() {
    val biblioteca = Biblioteca()

    // Carga de libros iniciales
    biblioteca.crearLibro("Rust Programming", "Steve", "2024")
    biblioteca.crearLibro("El Quijote", "Cervantes", "1605")

    val estudiante = Estudiante("Martin")

    while (true) {
        println("1. Mostrar libros")
        println("2. Pedir libro")
        println("3. Devolver libro")
        println("4. Salir")

        val opcion = readlnOrNull()?.trim() ?: break

        when (opcion) {
            "1" -> {
                biblioteca.mostrarLibros()
            }

            "2" -> {
                println("Nombre del libro:")
                val nombre = readlnOrNull()?.trim() ?: ""

                biblioteca.pedirLibro(nombre, estudiante)
                    .onSuccess { println("--- Libro prestado.\n") }
                    .onFailure { exception -> println("${exception.message}\n") }
            }

            "3" -> {
                biblioteca.devolverLibro(estudiante)
                    .onSuccess { println("--- Libro devuelto.\n") }
                    .onFailure { exception -> println("${exception.message}\n") }
            }

            "4" -> break

            else -> println("--- Opción inválida.\n")
        }
    }
}
