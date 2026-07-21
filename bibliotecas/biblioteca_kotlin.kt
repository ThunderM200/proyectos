import java.util.Scanner

data class Libro(
    val nombre: String,
    val autor: String,
    val publicacion: String,
    var disponible: Boolean = true
)

data class Estudiante(
    val nombre: String,
    var libroPrestadoIndice: Int? = null
)

class Biblioteca {
    val libros = mutableListOf<Libro>()

    fun crearLibro(nombre: String, autor: String, publicacion: String) {
        libros.add(Libro(nombre, autor, publicacion))
    }

    fun pedirLibro(nombre: String, estudiante: Estudiante): Result<Unit> {
        if (estudiante.libroPrestadoIndice != null) {
            return Result.failure(Exception("--- El estudiante ya tiene un libro prestado."))
        }

        val indice = libros.indexOfFirst { it.nombre == nombre }

        if (indice == -1) {
            return Result.failure(Exception("--- Libro no encontrado."))
        }

        val libro = libros[indice]

        if (!libro.disponible) {
            return Result.failure(Exception("--- Ese libro ya está prestado."))
        }

        libro.disponible = false
        estudiante.libroPrestadoIndice = indice

        return Result.success(Unit)
    }

    fun devolverLibro(estudiante: Estudiante): Result<Unit> {
        val indice = estudiante.libroPrestadoIndice
            ?: return Result.failure(Exception("--- El estudiante no tiene libros prestados."))

        libros[indice].disponible = true
        estudiante.libroPrestadoIndice = null

        return Result.success(Unit)
    }

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
    val scanner = Scanner(System.`in`)

    biblioteca.crearLibro("Rust Programming", "Steve", "2024")
    biblioteca.crearLibro("El Quijote", "Cervantes", "1605")

    val estudiante = Estudiante("Martin")

    while (true) {
        println("1. Mostrar libros")
        println("2. Pedir libro")
        println("3. Devolver libro")
        println("4. Salir")

        val opcion = scanner.nextLine().trim()

        when (opcion) {
            "1" -> biblioteca.mostrarLibros()

            "2" -> {
                println("Nombre del libro:")
                val nombre = scanner.nextLine().trim()

                biblioteca.pedirLibro(nombre, estudiante)
                    .onSuccess { println("--- Libro prestado.\n") }
                    .onFailure { println("${it.message}\n") }
            }

            "3" -> {
                biblioteca.devolverLibro(estudiante)
                    .onSuccess { println("--- Libro devuelto.\n") }
                    .onFailure { println("${it.message}\n") }
            }

            "4" -> break

            else -> println("--- Opción inválida.\n")
        }
    }
}
