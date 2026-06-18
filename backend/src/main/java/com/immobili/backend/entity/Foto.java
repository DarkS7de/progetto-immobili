package com.immobili.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "foto")
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // I dati binari della foto. @JsonIgnore = NON li serializziamo nel JSON di lista,
    // ce li scarichiamo solo via endpoint dedicato /api/foto/{id}/raw
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, columnDefinition = "BYTEA")
    @JsonIgnore
    private byte[] dati;

    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType;

    @Column(name = "nome_file", length = 255)
    private String nomeFile;

    @Column(nullable = false)
    private Integer ordine = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annuncio_id", nullable = false)
    @JsonIgnore   // evitiamo loop infinito Annuncio ↔ Foto
    private Annuncio annuncio;

    // Costruttori
    public Foto() {}

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public byte[] getDati() { return dati; }
    public void setDati(byte[] dati) { this.dati = dati; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getNomeFile() { return nomeFile; }
    public void setNomeFile(String nomeFile) { this.nomeFile = nomeFile; }

    public Integer getOrdine() { return ordine; }
    public void setOrdine(Integer ordine) { this.ordine = ordine; }

    public Annuncio getAnnuncio() { return annuncio; }
    public void setAnnuncio(Annuncio annuncio) { this.annuncio = annuncio; }
}