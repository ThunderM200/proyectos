use std::io;

struct Libro {
    nombre: String,
    autor: String,
    publicacion: String,
    disponible: bool,
}

struct Estudiante {
    nombre: String,
    libro_prestado: Option<usize>,
}

struct Biblioteca {
    libros: Vec<Libro>,
}

impl Biblioteca {

    fn crear_libro(
        &mut self,
        nombre: String,
        autor: String,
        publicacion: String,
    ) {
        self.libros.push(Libro {
            nombre,
            autor,
            publicacion,
            disponible: true,
        });
    }

    fn pedir_libro(
        &mut self,
        nombre: &str,
        estudiante: &mut Estudiante,
    ) -> Result<(), String> {

        if estudiante.libro_prestado.is_some() {
            return Err(String::from(
                "--- El estudiante ya tiene un libro prestado.",
            ));
        }

        for (i, libro) in self.libros.iter_mut().enumerate() {

            if libro.nombre == nombre {

                if !libro.disponible {
                    return Err(String::from(
                        "--- Ese libro ya está prestado.",
                    ));
                }

                libro.disponible = false;
                estudiante.libro_prestado = Some(i);

                return Ok(());
            }
        }

        Err(String::from("--- Libro no encontrado."))
    }

    fn devolver_libro(
        &mut self,
        estudiante: &mut Estudiante,
    ) -> Result<(), String> {

        let indice = match estudiante.libro_prestado {
            Some(i) => i,
            None => {
                return Err(String::from(
                    "--- El estudiante no tiene libros prestados.",
                ))
            }
        };

        self.libros[indice].disponible = true;
        estudiante.libro_prestado = None;

        Ok(())
    }

    fn mostrar_libros(&self) {

        println!("\n=== Biblioteca ===");

        for libro in &self.libros {

            println!(
                "{} - {} ({}) [{}]",
                libro.nombre,
                libro.autor,
                libro.publicacion,
                if libro.disponible {
                    "Disponible"
                } else {
                    "Prestado"
                }
            );
        }

        println!();
    }
}

fn main() {

    let mut biblioteca = Biblioteca {
        libros: Vec::new(),
    };

    biblioteca.crear_libro(
        "Rust Programming".to_string(),
        "Steve".to_string(),
        "2024".to_string(),
    );

    biblioteca.crear_libro(
        "El Quijote".to_string(),
        "Cervantes".to_string(),
        "1605".to_string(),
    );

    let mut estudiante = Estudiante {
        nombre: "Martin".to_string(),
        libro_prestado: None,
    };

    loop {

        println!("1. Mostrar libros");
        println!("2. Pedir libro");
        println!("3. Devolver libro");
        println!("4. Salir");

        let mut opcion = String::new();
        io::stdin().read_line(&mut opcion).unwrap();

        match opcion.trim() {

            "1" => {
                biblioteca.mostrar_libros();
            }

            "2" => {

                let mut nombre = String::new();

                println!("Nombre del libro:");

                io::stdin()
                    .read_line(&mut nombre)
                    .unwrap();

                match biblioteca.pedir_libro(
                    nombre.trim(),
                    &mut estudiante,
                ) {
                    Ok(_) => println!("--- Libro prestado.\n"),
                    Err(e) => println!("{}\n", e),
                }
            }

            "3" => {

                match biblioteca.devolver_libro(
                    &mut estudiante,
                ) {
                    Ok(_) => println!("--- Libro devuelto.\n"),
                    Err(e) => println!("{}\n", e),
                }
            }

            "4" => break,

            _ => println!("--- Opción inválida.\n"),
        }
    }
}
