package com.immobili.backend.repository;

import com.immobili.backend.entity.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FotoRepository extends JpaRepository<Foto, Long> {

    List<Foto> findByAnnuncioIdOrderByOrdineAsc(Long annuncioId);
}