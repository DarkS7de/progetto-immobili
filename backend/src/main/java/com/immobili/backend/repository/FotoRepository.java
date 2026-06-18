package com.immobili.backend.repository;

import com.immobili.backend.entity.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FotoRepository extends JpaRepository<Foto, Long> {

    // Foto di un annuncio ordinate per il campo "ordine"
    List<Foto> findByAnnuncioIdOrderByOrdineAsc(Long annuncioId);
}