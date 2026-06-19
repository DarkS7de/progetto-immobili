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

    List<Annuncio> findByAttivoTrue();

    List<Annuncio> findByVenditoreId(Long venditoreId);

    List<Annuncio> findByCategoriaId(Long categoriaId);

    List<Annuncio> findByTipoTransazione(TipoTransazione tipo);

    Annuncio findByCodice(String codice);

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