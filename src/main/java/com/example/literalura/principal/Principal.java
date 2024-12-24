package com.example.literalura.principal;

import com.example.literalura.dto.Datos;
import com.example.literalura.dto.DatosAutor;
import com.example.literalura.model.Autor;
import com.example.literalura.model.Lenguajes;
import com.example.literalura.model.Libro;
import com.example.literalura.repository.AutorRepository;
import com.example.literalura.repository.LibroRepository;
import com.example.literalura.service.ConsumoAPI;
import com.example.literalura.service.ConvierteDatos;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private final String URL_BASE = "https://gutendex.com/books/";
    private LibroRepository repository;
    private AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.repository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar libro por titulo 
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    mostrarLibrosBuscados();
                    break;
                case 3:
                    listaAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivos();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida, ingrese un número del 1 al 5");
            }
        }
    }

    private Datos getDatosResult() {
        System.out.println("Introduce el nombre del libro: ");
        var nombreLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "%20"));
        System.out.println(json);
        Datos datos = conversor.obtenerDatos(json, Datos.class);
        return datos;
    }

    private void buscarLibroPorTitulo() {
        Datos datosResults = getDatosResult();

        // Validar si hay resultados
        if (datosResults.resultados().isEmpty()) {
            System.out.println("No se encontraron libros con el título proporcionado.");
            return;
        }

        var datosLibro = datosResults.resultados().get(0); // Obtener el primer resultado
        String tituloLibro = datosLibro.titulo();
        // Verificar si el libro ya existe en la base de datos
        if (repository.existsByTitulo(tituloLibro)) {
            System.out.println("El libro con el título '" + tituloLibro + "' ya existe en la base de datos.");
            return;
        }
        Libro libro = new Libro(datosLibro);
        System.out.println("Libro encontrado: " + tituloLibro);
        List<Autor> autores = datosLibro.autor().stream()
                .map(datosAutor -> {
                    Autor autor = new Autor(datosAutor);
                    autor.setLibro(libro);
                    return autor;
                })
                .toList();
        libro.setDatosAutor(autores);
        repository.save(libro);
        System.out.printf("""
                        
                        --------------- LIBRO ---------------
                            ID: %d
                            TITULO: %s
                            AUTOR: %s
                            LENGUAJE: %s
                            TOTAL DESCARGAS: %d
                            -----------------------------------\n
                        """,
                datosLibro.id(),
                tituloLibro,
                datosLibro.autor().stream().map(DatosAutor::nombre).collect(Collectors.joining(", ")),
                datosLibro.lenguaje().get(0),
                datosLibro.totalDeDescargas());
    }

    private void mostrarLibrosBuscados() {
        List<Libro> libros = repository.findAll();
        libros.stream()
                .sorted(Comparator.comparing(Libro::getTitulo))
                .forEach(l -> System.out.printf("""
                            
                            --------------- LIBRO ---------------
                            ID: %d
                            TITULO: %s
                            AUTOR: %s
                            LENGUAJE: %s
                            TOTAL DESCARGAS: %d
                            -----------------------------------
                            """,
                        l.getIdLibro(),
                        l.getTitulo(),
                        l.getDatosAutor().stream().map(a -> a.getNombre()).collect(Collectors.joining(", ")),
                        l.getLenguaje(),
                        l.getTotalDesargas()));
    }

    private void listaAutoresRegistrados() {
        List<Autor> autorList = autorRepository.findAll();
        autorList.stream()
                .sorted(Comparator.comparing(Autor::getNombre))
                .forEach(a -> System.out.printf("""
                            
                            --------------- AUTOR ---------------
                            AUTOR: %s
                            FECHA DE NACIMIENTO: %d
                            FECHA DE FALLECIMIENTO: %d
                            -----------------------------------
                            """,
                        a.getNombre(),
                        a.getFechaNacimiento(),
                        a.getFechaFallecimiento()));
    }

    private void listarAutoresVivos() {
        System.out.println("Ingrese año: ");
        if (teclado.hasNextInt()) {
            var fecha = teclado.nextInt();
            System.out.println("Año ingresado: " + fecha);
            List<Autor> autor = autorRepository.fechaDeNacimientoYFallecimiento(fecha);
            if (autor.isEmpty()) {
                System.out.println("Autor no encontrado");
            } else {
                autor.stream()
                        .forEach(a -> System.out.printf("""
                                    
                                    --------------- AUTOR ---------------
                                    ID: %d
                                    TITULO: %s
                                    AUTOR: %s
                                    FECHA DE NACIMIENTO: %d
                                    FECHA DE FALLECIMIENTO: %d
                                    -----------------------------------
                                    """,
                                a.getId(),
                                a.getLibro().getTitulo(),
                                a.getNombre(),
                                a.getFechaNacimiento(),
                                a.getFechaFallecimiento()));
            }
        } else {
            System.out.println("ERROR: no puedes escribir texto, solo números enteros");
            teclado.nextLine();
        }
    }

    private void listarLibrosPorIdioma() {
        var opcion = -1;

        while (opcion != 0) {
            try {
                var menu = """
                        Selecciona el idioma del libro que deseas buscar:
                        1. Español
                        2. Inglés
                        3. Italiano
                        4. Francés
                        5. Portugués
                    
                        0. Salir
                    """;

                System.out.println(menu);
                if (teclado.hasNextInt()) {
                    opcion = teclado.nextInt();
                    teclado.nextLine();

                    switch (opcion) {
                        case 1:
                            buscarLibrosPorIdioma(Lenguajes.ESPANOL, "Español");
                            break;
                        case 2:
                            buscarLibrosPorIdioma(Lenguajes.INGLES, "Inglés");
                            break;
                        case 3:
                            buscarLibrosPorIdioma(Lenguajes.ITALIANO, "Italiano");
                            break;
                        case 4:
                            buscarLibrosPorIdioma(Lenguajes.FRANCES, "Francés");
                            break;
                        case 5:
                            buscarLibrosPorIdioma(Lenguajes.PORTUGUES, "Portugués");
                            break;
                        case 0:
                            System.out.println("Saliendo de la búsqueda por idioma...");
                            break;
                        default:
                            System.out.println("Opción no válida. Por favor, elige un número entre 0 y 5.");
                            break;
                    }
                } else {
                    System.out.println("Entrada inválida. Por favor, ingrese un número.");
                    teclado.nextLine();
                }
            } catch (Exception e) {
                System.out.println("Se produjo un error. Por favor, inténtelo de nuevo.");
                teclado.nextLine();
            }
        }
    }

    private void buscarLibrosPorIdioma(Lenguajes lenguaje, String idioma) {
        List<Libro> librosPorIdioma = repository.findByLenguaje(Lenguajes.valueOf(lenguaje.name()));
        if (librosPorIdioma.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma: " + idioma);
        } else {
            System.out.println("Libros del idioma " + idioma + " encontrados:");
            System.out.println("La cantidad de libros del mismo idioma: " + librosPorIdioma.size());
            librosPorIdioma.forEach(l -> System.out.printf("""
                        
                        --------------- LIBRO ---------------
                        ID: %d
                        TITULO: %s
                        AUTOR: %s
                        LENGUAJE: %s
                        TOTAL DESCARGAS: %d
                        -----------------------------------
                        """,
                    l.getIdLibro(),
                    l.getTitulo(),
                    l.getDatosAutor().stream().map(a -> a.getNombre()).collect(Collectors.joining(", ")),
                    l.getLenguaje(),
                    l.getTotalDesargas()));
        }
    }
}
