package com.immobili.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recensione")
public class Recensione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String testo;

    @Column(nullable = false)
    private Integer voto;   // 1..5, vincolo già nel DB

    @Column(nullable = false, updatable = false)
    private LocalDateTime data = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acquirente_id", nullable = false)
    @JsonIgnoreProperties({"password"})
    private Utente acquirente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annuncio_id", nullable = false)
    @JsonIgnoreProperties({"foto", "venditore"})  // evita catene troppo lunghe nel JSON
    private Annuncio annuncio;

    public Recensione() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }

    public Integer getVoto() { return voto; }
    public void setVoto(Integer voto) { this.voto = voto; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }

    public Utente getAcquirente() { return acquirente; }
    public void setAcquirente(Utente acquirente) { this.acquirente = acquirente; }

    public Annuncio getAnnuncio() { return annuncio; }
    public void setAnnuncio(Annuncio annuncio) { this.annuncio = annuncio; }
}