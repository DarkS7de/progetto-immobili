package com.immobili.backend.repository;

import com.immobili.backend.entity.Annuncio;
import com.immobili.backend.entity.Annuncio.TipoTransazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AnnuncioRepository extends JpaRepository<Annuncio, Long> {

    // Annunci attivi (utile per la home)
    List<Annuncio> findByAttivoTrue();

    // Annunci di un venditore
    List<Annuncio> findByVenditoreId(Long venditoreId);

    // Ricerca per categoria
    List<Annuncio> findByCategoriaId(Long categoriaId);

    // Ricerca per tipo transazione (VENDITA / AFFITTO)
    List<Annuncio> findByTipoTransazione(TipoTransazione tipo);

    // Ricerca per codice (univoco)
    Annuncio findByCodice(String codice);

    // Ricerca filtrata flessibile: tutti i parametri sono opzionali, se null vengono ignorati.
    // Questa serve per la pagina "Cerca immobili" lato acquirente.
    @Query("""
            SELECT a FROM Annuncio a
            WHERE a.attivo = true
              AND (:tipo IS NULL OR a.tipoTransazione = :tipo)
              AND (:categoriaId IS NULL OR a.categoria.id = :categoriaId)
              AND (:prezzoMin IS NULL OR a.prezzo >= :prezzoMin)
              AND (:prezzoMax IS NULL OR a.prezzo <= :prezzoMax)
              AND (:mqMin IS NULL OR a.metriQuadri >= :mqMin)
              AND (:mqMax IS NULL OR a.metriQuadri <= :mqMax)
           """)
    List<Annuncio> ricercaAvanzata(
            @Param("tipo") TipoTransazione tipo,
            @Param("categoriaId") Long categoriaId,
            @Param("prezzoMin") BigDecimal prezzoMin,
            @Param("prezzoMax") BigDecimal prezzoMax,
            @Param("mqMin") Integer mqMin,
            @Param("mqMax") Integer mqMax
    );
}