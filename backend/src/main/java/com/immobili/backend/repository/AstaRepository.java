package com.immobili.backend.repository;

import com.immobili.backend.entity.Asta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AstaRepository extends JpaRepository<Asta, Long> {

    Optional<Asta> findByAnnuncioId(Long annuncioId);

    List<Asta> findByAttivaTrue();
}