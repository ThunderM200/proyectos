package main

import (
	"bufio"
	"errors"
	"fmt"
	"os"
	"strings"
)

// Libro representa la información de cada obra
type Libro struct {
	Nombre      string
	Autor       string
	Publicacion string
	Disponible  bool
}

// Estudiante representa al usuario.
// LibroPrestado usa *int para emular el Option<usize> de Rust (nil significa "ninguno").
type Estudiante struct {
	Nombre        string
	LibroPrestado *int
}

// Biblioteca almacena la colección de libros
type Biblioteca struct {
	Libros []Libro
}

// CrearLibro añade un nuevo libro a la biblioteca
func (b *Biblioteca) CrearLibro(nombre, autor, publicacion string) {
	nuevoLibro := Libro{
		Nombre:      nombre,
		Autor:       autor,
		Publicacion: publicacion,
		Disponible:  true,
	}
	b.Libros = append(b.Libros, nuevoLibro)
}

// PedirLibro gestiona el préstamo de un libro a un estudiante
func (b *Biblioteca) PedirLibro(nombre string, estudiante *Estudiante) error {
	if estudiante.LibroPrestado != nil {
		return errors.New("--- El estudiante ya tiene un libro prestado.")
	}

	for i := range b.Libros {
		if b.Libros[i].Nombre == nombre {
			if !b.Libros[i].Disponible {
				return errors.New("--- Ese libro ya está prestado.")
			}

			b.Libros[i].Disponible = false
			indice := i
			estudiante.LibroPrestado = &indice
			return nil
		}
	}

	return errors.New("--- Libro no encontrado.")
}

// DevolverLibro gestiona la devolución del libro prestado
func (b *Biblioteca) DevolverLibro(estudiante *Estudiante) error {
	if estudiante.LibroPrestado == nil {
		return errors.New("--- El estudiante no tiene libros prestados.")
	}

	indice := *estudiante.LibroPrestado
	b.Libros[indice].Disponible = true
	estudiante.LibroPrestado = nil

	return nil
}

// MostrarLibros imprime la lista de libros en consola
func (b *Biblioteca) MostrarLibros() {
	fmt.Println("\n=== Biblioteca ===")
	for _, libro := range b.Libros {
		estado := "Disponible"
		if !libro.Disponible {
			estado = "Prestado"
		}
		fmt.Printf("%s - %s (%s) [%s]\n", libro.Nombre, libro.Autor, libro.Publicacion, estado)
	}
	fmt.Println()
}

func main() {
	biblioteca := Biblioteca{
		Libros: make([]Libro, 0),
	}

	biblioteca.CrearLibro("Rust Programming", "Steve", "2024")
	biblioteca.CrearLibro("El Quijote", "Cervantes", "1605")

	estudiante := Estudiante{
		Nombre:        "Martin",
		LibroPrestado: nil,
	}

	reader := bufio.NewReader(os.Stdin)

	for {
		fmt.Println("1. Mostrar libros")
		fmt.Println("2. Pedir libro")
		fmt.Println("3. Devolver libro")
		fmt.Println("4. Salir")

		opcion, err := reader.ReadString('\n')
		if err != nil {
			continue
		}
		opcion = strings.TrimSpace(opcion)

		switch opcion {
			case "1":
				biblioteca.MostrarLibros()

			case "2":
				fmt.Println("Nombre del libro:")
				nombre, err := reader.ReadString('\n')
				if err != nil {
					continue
				}
				nombre = strings.TrimSpace(nombre)

				err = biblioteca.PedirLibro(nombre, &estudiante)
				if err != nil {
					fmt.Printf("%s\n\n", err)
				} else {
					fmt.Println("--- Libro prestado.\n")
				}

			case "3":
				err = biblioteca.DevolverLibro(&estudiante)
				if err != nil {
					fmt.Printf("%s\n\n", err)
				} else {
					fmt.Println("--- Libro devuelto.\n")
				}

			case "4":
				return

			default:
				fmt.Println("--- Opción inválida.\n")
		}
	}
}
