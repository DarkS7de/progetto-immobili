package com.immobili.backend.service;

import com.immobili.backend.entity.Annuncio;
import com.immobili.backend.entity.Utente;
import com.immobili.backend.repository.AnnuncioRepository;
import com.immobili.backend.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
public class AnnuncioService {

    @Autowired
    private AnnuncioRepository annuncioRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Transactional
    public Annuncio creaAnnuncio(Annuncio annuncio, Long venditoreId) {
        Utente venditore = utenteRepository.findById(venditoreId)
                .orElseThrow(() -> new IllegalArgumentException("Venditore non trovato"));

        if (venditore.getRuolo() == Utente.Ruolo.ACQUIRENTE) {
            throw new IllegalStateException("Gli acquirenti non possono creare annunci");
        }
        if (Boolean.TRUE.equals(venditore.getBannato())) {
            throw new IllegalStateException("Utente bannato, impossibile creare annunci");
        }

        annuncio.setCodice(generaProssimoCodice());
        annuncio.setVenditore(venditore);
        annuncio.setAttivo(true);
        annuncio.setPrezzoVecchio(null);

        return annuncioRepository.save(annuncio);
    }

    private String generaProssimoCodice() {
        int anno = Year.now().getValue();
        long count = annuncioRepository.count() + 1;
        return String.format("ANN-%d-%04d", anno, count);
    }

    @Transactional
    public Annuncio modificaAnnuncio(Long annuncioId, Annuncio datiNuovi, Long utenteRichiedente) {
        Annuncio esistente = annuncioRepository.findById(annuncioId)
                .orElseThrow(() -> new IllegalArgumentException("Annuncio non trovato"));

        verificaPermessoModifica(esistente, utenteRichiedente);

        esistente.setTitolo(datiNuovi.getTitolo());
        esistente.setDescrizione(datiNuovi.getDescrizione());
        esistente.setMetriQuadri(datiNuovi.getMetriQuadri());
        esistente.setLatitudine(datiNuovi.getLatitudine());
        esistente.setLongitudine(datiNuovi.getLongitudine());
        esistente.setIndirizzo(datiNuovi.getIndirizzo());
        esistente.setTipoTransazione(datiNuovi.getTipoTransazione());
        esistente.setCategoria(datiNuovi.getCategoria());

        return annuncioRepository.save(esistente);
    }

    @Transactional
    public Annuncio ribassaPrezzo(Long annuncioId, BigDecimal nuovoPrezzo, Long utenteRichiedente) {
        Annuncio annuncio = annuncioRepository.findById(annuncioId)
                .orElseThrow(() -> new IllegalArgumentException("Annuncio non trovato"));

        verificaPermessoModifica(annuncio, utenteRichiedente);

        if (nuovoPrezzo.compareTo(annuncio.getPrezzo()) >= 0) {
            throw new IllegalArgumentException(
                    "Il nuovo prezzo deve essere inferiore al prezzo attuale (" + annuncio.getPrezzo() + ")"
            );
        }

        annuncio.setPrezzoVecchio(annuncio.getPrezzo());
        annuncio.setPrezzo(nuovoPrezzo);

        return annuncioRepository.save(annuncio);
    }

    @Transactional
    public void eliminaAnnuncio(Long annuncioId, Long utenteRichiedente) {
        Annuncio annuncio = annuncioRepository.findById(annuncioId)
                .orElseThrow(() -> new IllegalArgumentException("Annuncio non trovato"));

        verificaPermessoModifica(annuncio, utenteRichiedente);
        annuncioRepository.delete(annuncio);
    }

    public List<Annuncio> tuttiAttivi() {
        return annuncioRepository.findByAttivoTrue();
    }

    public Optional<Annuncio> trovaPerId(Long id) {
        return annuncioRepository.findById(id);
    }

    public List<Annuncio> ricerca(
            Annuncio.TipoTransazione tipo, Long categoriaId,
            BigDecimal prezzoMin, BigDecimal prezzoMax,
            Integer mqMin, Integer mqMax) {
        return annuncioRepository.ricercaAvanzata(tipo, categoriaId, prezzoMin, prezzoMax, mqMin, mqMax);
    }

    private void verificaPermessoModifica(Annuncio annuncio, Long utenteRichiedente) {
        Utente richiedente = utenteRepository.findById(utenteRichiedente)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        boolean isAdmin = richiedente.getRuolo() == Utente.Ruolo.ADMIN;
        boolean isProprietario = annuncio.getVenditore().getId().equals(utenteRichiedente);

        if (!isAdmin && !isProprietario) {
            throw new SecurityException("Non hai i permessi per modificare questo annuncio");
        }
    }
}