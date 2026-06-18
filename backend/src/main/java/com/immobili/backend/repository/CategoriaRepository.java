package com.immobili.backend.repository;

import com.immobili.backend.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Spring Data deduce la query dal nome del metodo: SELECT * FROM categoria WHERE nome = ?
    Optional<Categoria> findByNome(String nome);
}