package com.example.literalura.model;

import com.example.literalura.dto.DatosLibro;
import jakarta.persistence.*;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer idLibro;

    @Column(unique = true)
    private String titulo;

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Autor> datosAutor;

    @Enumerated(EnumType.STRING)
    private Lenguajes lenguaje;
    private Integer totalDesargas;

    public Libro() {
    }

    public Libro(DatosLibro datosLibro){
        this.idLibro = datosLibro.id();
        this.titulo = datosLibro.titulo();
        this.datosAutor = datosLibro.autor().stream()
                .map(d -> new Autor(d)) // Convertir DatosAutor a Autor
                .collect(Collectors.toList());
        this.lenguaje = Lenguajes.fromString(datosLibro.lenguaje().get(0).trim());
        this.totalDesargas = datosLibro.totalDeDescargas();
    }

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor=" + datosAutor +
                ", lenguaje=" + lenguaje +
                ", totalDesargas=" + totalDesargas +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(Integer idLibro) {
        this.idLibro = idLibro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<Autor> getDatosAutor() {
        return datosAutor;
    }

    public void setDatosAutor(List<Autor> datosAutor) {
        this.datosAutor = datosAutor;
    }

    public Lenguajes getLenguaje() {
        return lenguaje;
    }

    public void setLenguaje(Lenguajes lenguaje) {
        this.lenguaje = lenguaje;
    }

    public Integer getTotalDesargas() {
        return totalDesargas;
    }

    public void setTotalDesargas(Integer totalDesargas) {
        this.totalDesargas = totalDesargas;
    }
}
