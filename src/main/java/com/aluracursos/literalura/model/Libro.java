package com.aluracursos.literalura.model;

import com.aluracursos.literalura.service.ConsultaHuggingFace;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "libros")
public class Libro {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(unique = true)
    private String titulo;
    private Double numeroDeDescargas;

    @Enumerated(EnumType.STRING)
    private Idioma idioma;

    @Column(columnDefinition = "TEXT")
    private String resumen;

    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "libros_autores",
            joinColumns = @JoinColumn(name = "libro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private List<Autor> autores = new ArrayList<>();

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Double getNumeroDeDescargas() {
        return numeroDeDescargas;
    }

    public void setNumeroDeDescargas(Double numeroDeDescargas) {
        this.numeroDeDescargas = numeroDeDescargas;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public void setIdioma(Idioma idioma) {
        this.idioma = idioma;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }

    public Libro() {

    }

    public Libro(DatosLibros datosLibros) {
        this.titulo = datosLibros.titulo();
        this.numeroDeDescargas = datosLibros.numeroDeDescargas();

        if (datosLibros.autor() != null && !datosLibros.autor().isEmpty()) {
            this.autores = datosLibros.autor().stream()
                    .map(Autor::new)
                    .collect(Collectors.toList());
        }

        if (datosLibros.idiomas() != null && !datosLibros.idiomas().isEmpty()) {
            this.idioma = Idioma.fromString(datosLibros.idiomas().get(0));
        } else {
            this.idioma = Idioma.DESCONOCIDO; // O manejar un valor por defecto
        }

        String resumenOriginal = (datosLibros.resumen() != null && !datosLibros.resumen().isEmpty())
                ? datosLibros.resumen().get(0)
                : "Sin resumen disponible";

        this.resumen = ConsultaHuggingFace.obtenerTraduccion(resumenOriginal);
    }

    @Override
    public String toString() {

        String nombresAutores = (autores != null)
                ? autores.stream()
                .map(Autor::getNombre)
                .collect(java.util.stream.Collectors.joining(", "))
                : "Sin autor registrado";

        return "\n--------- LIBRO ---------\n" +
                " Título='" + titulo + "\n" +
                " Autor: " + nombresAutores + "\n" +
                " Idioma: " + idioma + "\n" +
                " Resumen: " + resumen + "\n" +
                " Número de Descargas: " + numeroDeDescargas + "\n" +
                "-------------------------\n";
    }
}
