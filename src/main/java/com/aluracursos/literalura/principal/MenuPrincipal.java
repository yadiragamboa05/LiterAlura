package com.aluracursos.literalura.principal;

public class MenuPrincipal {
    public static void exibirMenu() {
        System.out.println("""
                        *************************************************
                        Elije la opción a través de su número =]:
                        \n
                        1- Guardar libro
                        2- Buscar libro por título
                        3- Listar libros registrados
                        4- Listar autores registrados
                        5- Listar autores vivos en un determinado año
                        6- Listar libros por idioma
                        7- Listar Top 10 libros más descargados
                        8- Buscar autor por nombre
                        9- Buscar autores por rango de Años
                        10- Listar Autores Sin Fecha de Fallecimiento
                        11- Mostrar estadísticas de los libros guardados
                        12- Mostrar estadísticas de los autores guardados
                        13- Mostrar autor más popular
                        \n
                        0- Salir
                        *************************************************
                        """);
    }
}
