package com.immobili.backend.repository;

import com.immobili.backend.entity.Messaggio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessaggioRepository extends JpaRepository<Messaggio, Long> {

    List<Messaggio> findByDestinatarioIdOrderByDataDesc(Long destinatarioId);

    long countByDestinatarioIdAndLettoFalse(Long destinatarioId);
}