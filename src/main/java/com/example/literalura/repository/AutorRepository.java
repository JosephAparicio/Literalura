package com.example.literalura.repository;

import com.example.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    @Query(value = "SELECT * FROM autores a WHERE a.fecha_fallecimiento >= :fechaFallecimiento", nativeQuery = true)
    List<Autor> fechaDeNacimientoYFallecimiento(Integer fechaFallecimiento);

    @Query(value = "SELECT * FROM autores a WHERE LOWER(a.nombre) LIKE LOWER(CONCAT('%', :autor, '%'))", nativeQuery = true)
    Optional<Autor> buscarAutorPorNombre(@Param("autor") String autor);
}
