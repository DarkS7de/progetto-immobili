package com.immobili.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "annuncio")
public class Annuncio {

    public enum TipoTransazione {
        VENDITA, AFFITTO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codice;     // es. "ANN-2026-0001"

    @Column(nullable = false, length = 200)
    private String titolo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descrizione;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prezzo;

    @Column(name = "prezzo_vecchio", precision = 12, scale = 2)
    private BigDecimal prezzoVecchio;

    @Column(name = "metri_quadri", nullable = false)
    private Integer metriQuadri;

    @Column(nullable = false)
    private Double latitudine;

    @Column(nullable = false)
    private Double longitudine;

    @Column(length = 255)
    private String indirizzo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_transazione", nullable = false, length = 20)
    private TipoTransazione tipoTransazione;

    @Column(name = "data_pubblicazione", nullable = false, updatable = false)
    private LocalDateTime dataPubblicazione = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean attivo = true;

    // RELAZIONE molti-a-uno con Utente (un venditore ha molti annunci)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venditore_id", nullable = false)
    @JsonIgnoreProperties({"password"})  // non esporre la password nel JSON
    private Utente venditore;

    // RELAZIONE molti-a-uno con Categoria
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    // RELAZIONE uno-a-molti con Foto.
    // FetchType.LAZY = Hibernate restituisce un proxy invece della lista vera;
    // la lista viene popolata solo se chiamo annuncio.getFoto().
    @OneToMany(mappedBy = "annuncio", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Foto> foto = new ArrayList<>();

    // Costruttori
    public Annuncio() {}

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodice() { return codice; }
    public void setCodice(String codice) { this.codice = codice; }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public BigDecimal getPrezzo() { return prezzo; }
    public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo; }

    public BigDecimal getPrezzoVecchio() { return prezzoVecchio; }
    public void setPrezzoVecchio(BigDecimal prezzoVecchio) { this.prezzoVecchio = prezzoVecchio; }

    public Integer getMetriQuadri() { return metriQuadri; }
    public void setMetriQuadri(Integer metriQuadri) { this.metriQuadri = metriQuadri; }

    public Double getLatitudine() { return latitudine; }
    public void setLatitudine(Double latitudine) { this.latitudine = latitudine; }

    public Double getLongitudine() { return longitudine; }
    public void setLongitudine(Double longitudine) { this.longitudine = longitudine; }

    public String getIndirizzo() { return indirizzo; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }

    public TipoTransazione getTipoTransazione() { return tipoTransazione; }
    public void setTipoTransazione(TipoTransazione tipoTransazione) { this.tipoTransazione = tipoTransazione; }

    public LocalDateTime getDataPubblicazione() { return dataPubblicazione; }
    public void setDataPubblicazione(LocalDateTime dataPubblicazione) { this.dataPubblicazione = dataPubblicazione; }

    public Boolean getAttivo() { return attivo; }
    public void setAttivo(Boolean attivo) { this.attivo = attivo; }

    public Utente getVenditore() { return venditore; }
    public void setVenditore(Utente venditore) { this.venditore = venditore; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public List<Foto> getFoto() { return foto; }
    public void setFoto(List<Foto> foto) { this.foto = foto; }
}