package com.immobili.backend.repository;

import com.immobili.backend.entity.Recensione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecensioneRepository extends JpaRepository<Recensione, Long> {

    List<Recensione> findByAnnuncioIdOrderByDataDesc(Long annuncioId);

    List<Recensione> findByAcquirenteId(Long acquirenteId);

    boolean existsByAcquirenteIdAndAnnuncioId(Long acquirenteId, Long annuncioId);
}