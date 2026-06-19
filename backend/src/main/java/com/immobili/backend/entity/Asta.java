package com.immobili.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "asta")
public class Asta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_inizio", nullable = false)
    private LocalDateTime dataInizio;

    @Column(name = "data_fine", nullable = false)
    private LocalDateTime dataFine;

    @Column(name = "offerta_minima", nullable = false, precision = 12, scale = 2)
    private BigDecimal offertaMinima;

    @Column(nullable = false)
    private Boolean attiva = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annuncio_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"foto", "venditore"})
    private Annuncio annuncio;

    public Asta() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDataInizio() { return dataInizio; }
    public void setDataInizio(LocalDateTime dataInizio) { this.dataInizio = dataInizio; }

    public LocalDateTime getDataFine() { return dataFine; }
    public void setDataFine(LocalDateTime dataFine) { this.dataFine = dataFine; }

    public BigDecimal getOffertaMinima() { return offertaMinima; }
    public void setOffertaMinima(BigDecimal offertaMinima) { this.offertaMinima = offertaMinima; }

    public Boolean getAttiva() { return attiva; }
    public void setAttiva(Boolean attiva) { this.attiva = attiva; }

    public Annuncio getAnnuncio() { return annuncio; }
    public void setAnnuncio(Annuncio annuncio) { this.annuncio = annuncio; }
}