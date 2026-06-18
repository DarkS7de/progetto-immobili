package com.immobili.backend.repository;

import com.immobili.backend.entity.Messaggio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessaggioRepository extends JpaRepository<Messaggio, Long> {

    // Messaggi ricevuti da un venditore, più recenti prima
    List<Messaggio> findByDestinatarioIdOrderByDataDesc(Long destinatarioId);

    // Conta i non letti (per il badge in navbar)
    long countByDestinatarioIdAndLettoFalse(Long destinatarioId);
}