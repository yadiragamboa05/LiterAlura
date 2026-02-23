package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.AutorRepository;
import com.aluracursos.literalura.repository.LibroRepository;
import com.aluracursos.literalura.service.ConsumoAPI;
import com.aluracursos.literalura.service.ConvierteDatos;

import java.util.*;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    final String URL_BASE = "https://gutendex.com/books/?search=";
    ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository librosRepositorio;
    private AutorRepository autoresRepositorio;
    List<Libro> libros;
    List<Autor> autores;
    Optional<Libro> libroBuscado;
    Optional<Autor> autorBuscado;

    public Principal(LibroRepository librosRepository, AutorRepository autorRepository) {
        this.librosRepositorio = librosRepository;
        this.autoresRepositorio = autorRepository;
    }

    public void muestraElMenu() {
        var opcion = -1;

        while (opcion != 0) {
            try {
                MenuPrincipal.exibirMenu();

                if (teclado.hasNextInt()) {
                    opcion = teclado.nextInt();
                    teclado.nextLine();
                } else {
                    System.out.println("Formato no válido. Por favor, ingresa solo números.");
                    esperar(teclado);
                    teclado.nextLine();
                    continue;
                }

                switch (opcion) {
                    case 1:
                        buscarLibroWeb();
                        break;
                    case 2:
                        buscarLibroPorTitulo();
                        break;
                    case 3:
                        listarLibrosRegistrados();
                        break;
                    case 4:
                        listarAutoresRegistrados();
                        break;
                    case 5:
                        listarAutoresVivosEnAnno();
                        break;
                    case 6:
                        listarLibrosPorIdioma();
                        break;
                    case 7:
                        buscarTop10LibrosMasDescargados();
                        break;
                    case 8:
                        buscarAutorPorNombre();
                        break;
                    case 9:
                        buscarAutoresPorRangoDeAnnos();
                        break;
                    case 10:
                        listarAutoresSinFechaFallecimiento();
                        break;
                    case 11:
                        mostrarEstadisticasLibros();
                        break;
                    case 12:
                        mostrarEstadisticasAutores();
                        break;
                    case 13:
                        mostrarAutorMasPopular();
                        break;
                    case 0:
                        System.out.println("Cerrando la aplicación...");
                        break;
                    default:
                        System.out.println("Opción inválida");
                        esperar(teclado);
                        continue;
                }
            } catch (Exception e) {
                System.out.println("Ocurrió un error inesperado. Intenta de nuevo.");
                teclado.nextLine();
                esperar(teclado);
            }
        }
    }

    private DatosLibros getDatosLibro() {
        System.out.println("Escribe el nombre del libro que deseas buscar: ");
        var nombreLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + nombreLibro.replace(" ", "+"));

        Datos datosBusqueda = conversor.obtenerDatos(json, Datos.class);

        if (datosBusqueda != null && datosBusqueda.resultados() != null && !datosBusqueda.resultados().isEmpty()) {
            return datosBusqueda.resultados().get(0);
        }

        return null;
    }

    private void buscarLibroWeb() {
        try {
            DatosLibros datos = getDatosLibro();

            if (datos != null) {
                Optional<Libro> libroExistente = librosRepositorio.findByTituloContainsIgnoreCase(datos.titulo());
                if (libroExistente.isPresent()) {
                    System.out.println("El libro ya está registrado.");
                    return;
                }

                Libro libro = new Libro(datos);
                List<Autor> autoresConsolidados = new ArrayList<>();

                for (Autor autor : libro.getAutores()) {
                    Optional<Autor> autorBd = autoresRepositorio.findByNombreContainsIgnoreCase(autor.getNombre());

                    if (autorBd.isPresent()) {
                        autoresConsolidados.add(autorBd.get());
                    } else {
                        Autor autorGuardado = autoresRepositorio.save(autor);
                        autoresConsolidados.add(autorGuardado);
                    }
                }

                libro.setAutores(autoresConsolidados);

                librosRepositorio.save(libro);
                System.out.println("Libro registrado con éxito: " + libro);
            } else {
                System.out.println("Libro no encontrado");
            }
        } catch (Exception e) {
            System.out.println("Error al registrar el libro: " + e.getMessage());
        }
        esperar(teclado);
    }

    private void buscarLibroPorTitulo() {
        System.out.println("Escribe el nombre del libro que deseas buscar: ");
        var nombreLibro = teclado.nextLine();

        libroBuscado = librosRepositorio.findByTituloContainsIgnoreCase(nombreLibro);

        if (libroBuscado.isPresent()) {
            System.out.println("El libro buscado es: \n" + libroBuscado.get());
        } else {
            System.out.println("Libro no encontrado");
        }
        esperar(teclado);
    }

    private void listarLibrosRegistrados() {
        libros = librosRepositorio.findAll();
        libros.stream()
                .sorted(Comparator.comparing(Libro::getTitulo))
                .forEach(System.out::println);
        esperar(teclado);
    }

    private void listarAutoresRegistrados() {
        autores = autoresRepositorio.findAll();
        autores.stream()
                .sorted(Comparator.comparing(Autor::getNombre))
                .forEach(System.out::println);
        esperar(teclado);
    }

    private void listarAutoresVivosEnAnno() {
        System.out.println("Ingrese el año de autor(es) que desea buscar: ");
        try {
            var numeroDeAnno = teclado.nextInt();
            teclado.nextLine();

            autores = autoresRepositorio.listarAutoresVivosEnAnno(numeroDeAnno);

            if (autores.isEmpty()) {
                System.out.println("No se encontraron autores registrados que estuvieran vivos en el año " + numeroDeAnno);
            } else {
                System.out.println("--- Autores vivos en el año " + numeroDeAnno + " ---");
                autores.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Formato de año no válido.");
            teclado.nextLine();
        }
        esperar(teclado);
    }

    private void listarLibrosPorIdioma() {
        IdiomaMenuPrincipal.exibirIdiomaMenu();
        var entrada = teclado.nextLine().toLowerCase();
        Idioma idiomaSeleccionado = Idioma.DESCONOCIDO;

        try {
            idiomaSeleccionado = Idioma.fromEspanol(entrada);
        } catch (IllegalArgumentException e) {
            idiomaSeleccionado = Idioma.fromString(entrada);
        }

        if (idiomaSeleccionado == Idioma.DESCONOCIDO) {
            System.out.println("No se reconoció el idioma: " + entrada);
            esperar(teclado);
            return;
        }

        List<Libro> librosPorIdioma = librosRepositorio.findByIdioma(idiomaSeleccionado);

        if (librosPorIdioma.isEmpty()) {
            System.out.println("No se encontraron libros registrados en " + entrada);
        } else {
            System.out.println("--- Libros en " + entrada.toUpperCase() + " ---");
            librosPorIdioma.forEach(System.out::println);
        }
        esperar(teclado);
    }

    private void buscarTop10LibrosMasDescargados() {
        List<Libro> top10Libros = librosRepositorio.findTop10ByOrderByNumeroDeDescargasDesc();
        if (top10Libros.isEmpty()){
            System.out.println("No se encontraron libros");
            esperar(teclado);
        } else {
            top10Libros.forEach(System.out::println);
            esperar(teclado);
        }
    }

    private void buscarAutorPorNombre() {
        System.out.println("Escribe el nombre del autor que deseas buscar: ");
        var nombreAutor = teclado.nextLine();

        autorBuscado = autoresRepositorio.findByNombreContainsIgnoreCase(nombreAutor);

        if (autorBuscado.isPresent()) {
            System.out.println("El autor buscado es: \n" + autorBuscado.get());
            esperar(teclado);
        } else {
            System.out.println("Autor no encontrado");
            esperar(teclado);
        }
    }

    private void buscarAutoresPorRangoDeAnnos() {
        System.out.println("Ingrese el año de inicio:");
        var inicio = teclado.nextInt();
        System.out.println("Ingrese el año de fin:");
        var fin = teclado.nextInt();
        teclado.nextLine();

        List<Autor> autoresRango = autoresRepositorio.findByFechaDeNacimientoBetween(inicio, fin);
        if (autoresRango.isEmpty()) {
            System.out.println("No se encontraron autores en ese rango.");
            esperar(teclado);
        } else {
            autoresRango.forEach(System.out::println);
            esperar(teclado);
        }
    }

    private void listarAutoresSinFechaFallecimiento() {
        List<Autor> autoresSinMuerte = autoresRepositorio.findByFechaDeFallecimientoIsNull();

        if(autoresSinMuerte.isEmpty()){
            System.out.println("Autores no encontrados");
            esperar(teclado);
        } else {
            autoresSinMuerte.forEach(System.out::println);
            esperar(teclado);
        }
    }

    private void mostrarEstadisticasLibros() {
        List<Libro> librosRegistrados = librosRepositorio.findAll();

        if (librosRegistrados.isEmpty()) {
            System.out.println("No hay suficientes datos para generar estadísticas.");
            return;
        }

        DoubleSummaryStatistics est = librosRegistrados.stream()
                .mapToDouble(Libro::getNumeroDeDescargas)
                .summaryStatistics();

        System.out.println("\n----- ESTADÍSTICAS DE TU BIBLIOTECA -----");
        System.out.println("Cantidad de libros: " + est.getCount());
        System.out.println("Promedio de descargas: " + String.format("%.2f", est.getAverage()));
        System.out.println("Máximo de descargas: " + est.getMax());
        System.out.println("Mínimo de descargas: " + est.getMin());
        System.out.println("-----------------------------------------\n");
        esperar(teclado);
    }

    public void mostrarEstadisticasAutores() {
        List<Autor> autores = autoresRepositorio.findAll();

        IntSummaryStatistics est = autores.stream()
                .filter(a -> a.getFechaDeNacimiento() != null)
                .mapToInt(Autor::getFechaDeNacimiento)
                .summaryStatistics(); //

        System.out.println("\n------ ESTADÍSTICAS DE TUS AUTORES GUARDADOS ------");
        System.out.println("Autor más antiguo nacido en: " + est.getMin());
        System.out.println("Autor más reciente nacido en: " + est.getMax());
        System.out.println("Promedio de años de nacimiento: " + (int)est.getAverage());
        System.out.println("-----------------------------------------------------\n");
        esperar(teclado);
    }

    private void mostrarAutorMasPopular() {
        List<Autor> todosLosAutores = autoresRepositorio.findAll();

        Optional<Autor> topAutor = todosLosAutores.stream()
                .max(Comparator.comparingDouble(a -> a.getLibros().stream()
                        .mapToDouble(Libro::getNumeroDeDescargas)
                        .sum()));

        if (topAutor.isPresent()) {
            Autor a = topAutor.get();
            double totalDescargas = a.getLibros().stream()
                    .mapToDouble(Libro::getNumeroDeDescargas)
                    .sum();
            System.out.println("\n--- EL AUTOR MÁS POPULAR EN TU BD ---");
            System.out.println(" Autor: " + a.getNombre() + " \n Total Descargas: " + totalDescargas);
            System.out.println("-------------------------------------\n");
            esperar(teclado);
        } else {
            System.out.println("Aún no hay suficientes datos.");
            esperar(teclado);
        }
    }

    private static void esperar(Scanner teclado) {
        System.out.println("\nPresione Enter para volver al menú principal...");
        teclado.nextLine();
    }
}
