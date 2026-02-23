package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    @Query("SELECT a FROM Autor a WHERE a.fechaDeNacimiento <= :numeroDeAnno AND (a.fechaDeFallecimiento >= :numeroDeAnno OR a.fechaDeFallecimiento IS NULL)")
    List<Autor> listarAutoresVivosEnAnno(int numeroDeAnno);

    Optional<Autor> findByNombreContainsIgnoreCase(String nombreAutor);

    List<Autor> findByFechaDeNacimientoBetween(Integer inicio, Integer fin);

    List<Autor> findByFechaDeFallecimientoIsNull();
}
