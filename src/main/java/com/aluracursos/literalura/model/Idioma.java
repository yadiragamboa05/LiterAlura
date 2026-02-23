package com.aluracursos.literalura.model;

public enum Idioma {
    EN("en", "Inglés"),
    ES("es", "Español"),
    FR("fr", "Francés"),
    PT("pt", "Portugués"),
    DESCONOCIDO("unknown", "Desconocido");

    private String idiomaApi;

    private String idiomaEspanol;

    Idioma(String idiomaApi, String idiomaEspanol) {
        this.idiomaApi = idiomaApi;
        this.idiomaEspanol = idiomaEspanol;
    }

    public static Idioma fromString(String text) {
        for (Idioma idioma : Idioma.values()) {
            if (idioma.idiomaApi.equalsIgnoreCase(text)) {
                return idioma;
            }
        }
        return Idioma.DESCONOCIDO;
    }

    public static Idioma fromEspanol(String text) {
        for (Idioma idioma : Idioma.values()) {
            if (idioma.idiomaEspanol.equalsIgnoreCase(text)){
                return idioma;
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }
}