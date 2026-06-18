package com.immobili.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messaggio")
public class Messaggio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String oggetto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String testo;

    @Column(name = "nome_mittente", nullable = false, length = 150)
    private String nomeMittente;

    @Column(name = "email_mittente", nullable = false, length = 150)
    private String emailMittente;

    @Column(name = "telefono_mittente", length = 30)
    private String telefonoMittente;

    @Column(nullable = false, updatable = false)
    private LocalDateTime data = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean letto = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annuncio_id", nullable = false)
    @JsonIgnoreProperties({"foto", "venditore"})
    private Annuncio annuncio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinatario_id", nullable = false)
    @JsonIgnoreProperties({"password"})
    private Utente destinatario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mittente_id")
    @JsonIgnoreProperties({"password"})
    private Utente mittente;

    public Messaggio() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOggetto() { return oggetto; }
    public void setOggetto(String oggetto) { this.oggetto = oggetto; }

    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }

    public String getNomeMittente() { return nomeMittente; }
    public void setNomeMittente(String nomeMittente) { this.nomeMittente = nomeMittente; }

    public String getEmailMittente() { return emailMittente; }
    public void setEmailMittente(String emailMittente) { this.emailMittente = emailMittente; }

    public String getTelefonoMittente() { return telefonoMittente; }
    public void setTelefonoMittente(String telefonoMittente) { this.telefonoMittente = telefonoMittente; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }

    public Boolean getLetto() { return letto; }
    public void setLetto(Boolean letto) { this.letto = letto; }

    public Annuncio getAnnuncio() { return annuncio; }
    public void setAnnuncio(Annuncio annuncio) { this.annuncio = annuncio; }

    public Utente getDestinatario() { return destinatario; }
    public void setDestinatario(Utente destinatario) { this.destinatario = destinatario; }

    public Utente getMittente() { return mittente; }
    public void setMittente(Utente mittente) { this.mittente = mittente; }
}