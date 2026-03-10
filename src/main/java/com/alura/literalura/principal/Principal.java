package com.alura.literalura.principal;

import com.alura.literalura.model.*;
import com.alura.literalura.repository.LibroRepository;
import com.alura.literalura.service.ConsumoAPI;
import com.alura.literalura.service.ConvierteDatos;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository repositorio;
    private static final String URL_BASE = "https://gutendex.com/books/?search=";

    public Principal(LibroRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void mostrarMenu() {
        int opcion = -1;
        while (opcion != 0) {
            String menu = """
                    ****************************
                    Elija la opción a través de su número:
                    1 - Buscar libro por título
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    0 - Salir
                    ****************************
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1 -> buscarLibroPorTitulo();
                case 2 -> listarLibrosRegistrados();
                case 3 -> listarAutoresRegistrados();
                case 4 -> listarAutoresVivosPorAnio();
                case 5 -> listarLibrosPorIdioma();
                case 0 -> System.out.println("Cerrando la aplicación...");
                default -> System.out.println("Opción inválida");
            }
        }
    }

    private void buscarLibroPorTitulo() {
        System.out.println("Ingrese el nombre del libro que desea buscar:");
        String nombreLibro = teclado.nextLine();
        String json = consumoApi.obtenerDatos(URL_BASE + nombreLibro.replace(" ", "+"));
        DatosRespuesta datos = conversor.obtenerDatos(json, DatosRespuesta.class);

        if (datos.resultados().isEmpty()) {
            System.out.println("Libro no encontrado.");
            return;
        }

        DatosLibro datosLibro = datos.resultados().get(0);
        Optional<Libro> libroExistente = repositorio.findByTituloContainsIgnoreCase(datosLibro.titulo());

        if (libroExistente.isPresent()) {
            System.out.println("El libro ya está registrado en la base de datos.");
            return;
        }

        Libro libro = new Libro(datosLibro);
        List<Autor> autores = datosLibro.autores().stream()
                .map(Autor::new)
                .toList();
        libro.setAutores(autores);
        repositorio.save(libro);
        System.out.println(libro);
    }

    private void listarLibrosRegistrados() {
        List<Libro> libros = repositorio.findAll();
        libros.forEach(System.out::println);
    }

    private void listarAutoresRegistrados() {
        List<Libro> libros = repositorio.findAll();
        libros.stream()
                .flatMap(l -> l.getAutores().stream())
                .distinct()
                .forEach(System.out::println);
    }

    private void listarAutoresVivosPorAnio() {
        System.out.println("Ingrese el año que desea buscar:");
        int anio = teclado.nextInt();
        teclado.nextLine();
        List<Libro> libros = repositorio.findAll();
        libros.stream()
                .flatMap(l -> l.getAutores().stream())
                .distinct()
                .filter(a -> a.getAnioNacimiento() != null && a.getAnioNacimiento() <= anio
                        && (a.getAnioFallecimiento() == null || a.getAnioFallecimiento() >= anio))
                .forEach(System.out::println);
    }

    private void listarLibrosPorIdioma() {
        System.out.println("""
            Ingrese el idioma para buscar los libros:
            es - Español
            en - Inglés
            fr - Francés
            pt - Portugués
            """);
        String idioma = teclado.nextLine();
        List<Libro> libros = repositorio.findAll();
        libros.stream()
                .filter(l -> l.getIdioma().equals(idioma))
                .forEach(System.out::println);
    }
}