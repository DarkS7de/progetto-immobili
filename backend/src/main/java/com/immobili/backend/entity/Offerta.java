package com.immobili.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "offerta")
public class Offerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal importo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime data = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asta_id", nullable = false)
    @JsonIgnoreProperties({"annuncio"})
    private Asta asta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acquirente_id", nullable = false)
    @JsonIgnoreProperties({"password"})
    private Utente acquirente;

    public Offerta() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getImporto() { return importo; }
    public void setImporto(BigDecimal importo) { this.importo = importo; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }

    public Asta getAsta() { return asta; }
    public void setAsta(Asta asta) { this.asta = asta; }

    public Utente getAcquirente() { return acquirente; }
    public void setAcquirente(Utente acquirente) { this.acquirente = acquirente; }
}