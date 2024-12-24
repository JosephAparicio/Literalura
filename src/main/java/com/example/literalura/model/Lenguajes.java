package com.example.literalura.model;

public enum Lenguajes {
    ESPANOL("es", "Español"),
    INGLES("en", "Inglés"),
    ITALIANO("it", "Italiano"),
    FRANCES("fr", "Francés"),
    PORTUGUES("pt", "Portugués");

    private String lenguaje;
    private String tipoLenguaje;

    Lenguajes (String lenguaje, String tipoLenguaje){
        this.lenguaje = lenguaje;
        this.tipoLenguaje = tipoLenguaje;
    }

    public static Lenguajes fromString(String text) {
        for (Lenguajes Lenguajes : Lenguajes.values()) {
            if (Lenguajes.lenguaje.equalsIgnoreCase(text)){
                return Lenguajes;
            }
        }
        throw new IllegalArgumentException("Ningún lenguaje encontrado: " + text);
    }

    public static Lenguajes fromTipoLenguaje(String text) {
        for (Lenguajes Lenguajes : Lenguajes.values()) {
            if (Lenguajes.tipoLenguaje.equalsIgnoreCase(text.trim())){
                return Lenguajes;
            }
        }
        throw new IllegalArgumentException("Ningún lenguaje encontrado: " + text);
    }
}
