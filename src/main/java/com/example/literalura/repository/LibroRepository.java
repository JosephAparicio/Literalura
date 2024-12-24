package com.example.literalura.repository;

import com.example.literalura.model.Lenguajes;
import com.example.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    Optional<Libro> findByTituloContainsIgnoreCase(String nombreLibro);
    List<Libro> findByLenguaje(Lenguajes lenguaje);
    boolean existsByTitulo(String titulo);
}
