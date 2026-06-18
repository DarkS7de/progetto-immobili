package com.immobili.backend.service;

import com.immobili.backend.entity.Annuncio;
import com.immobili.backend.entity.Asta;
import com.immobili.backend.entity.Offerta;
import com.immobili.backend.entity.Utente;
import com.immobili.backend.repository.AnnuncioRepository;
import com.immobili.backend.repository.AstaRepository;
import com.immobili.backend.repository.OffertaRepository;
import com.immobili.backend.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AstaService {

    @Autowired private AstaRepository astaRepository;
    @Autowired private OffertaRepository offertaRepository;
    @Autowired private AnnuncioRepository annuncioRepository;
    @Autowired private UtenteRepository utenteRepository;

    // ============ CREAZIONE ASTA (solo venditore proprietario o admin) ============
    @Transactional
    public Asta creaAsta(Long annuncioId, Asta dati, Long utenteId) {
        Annuncio annuncio = annuncioRepository.findById(annuncioId)
                .orElseThrow(() -> new IllegalArgumentException("Annuncio non trovato"));

        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        boolean isAdmin = utente.getRuolo() == Utente.Ruolo.ADMIN;
        boolean isProprietario = annuncio.getVenditore().getId().equals(utenteId);
        if (!isAdmin && !isProprietario) {
            throw new SecurityException("Solo il proprietario può creare un'asta per questo annuncio");
        }

        // Un annuncio può avere una sola asta (vincolo UNIQUE nel DB)
        if (astaRepository.findByAnnuncioId(annuncioId).isPresent()) {
            throw new IllegalStateException("Esiste già un'asta per questo annuncio");
        }

        if (dati.getDataFine().isBefore(dati.getDataInizio())) {
            throw new IllegalArgumentException("La data di fine deve essere successiva all'inizio");
        }

        dati.setAnnuncio(annuncio);
        dati.setAttiva(true);
        return astaRepository.save(dati);
    }

    // ============ FARE UN'OFFERTA (acquirenti) ============
    @Transactional
    public Offerta faiOfferta(Long astaId, BigDecimal importo, Long acquirenteId) {
        Asta asta = astaRepository.findById(astaId)
                .orElseThrow(() -> new IllegalArgumentException("Asta non trovata"));

        Utente acquirente = utenteRepository.findById(acquirenteId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Validazioni
        if (!Boolean.TRUE.equals(asta.getAttiva())) {
            throw new IllegalStateException("L'asta non è attiva");
        }
        LocalDateTime ora = LocalDateTime.now();
        if (ora.isBefore(asta.getDataInizio())) {
            throw new IllegalStateException("L'asta non è ancora iniziata");
        }
        if (ora.isAfter(asta.getDataFine())) {
            throw new IllegalStateException("L'asta è terminata");
        }

        // L'offerta deve superare l'offerta minima
        if (importo.compareTo(asta.getOffertaMinima()) < 0) {
            throw new IllegalArgumentException(
                    "L'offerta deve essere almeno " + asta.getOffertaMinima());
        }

        // L'offerta deve superare l'offerta più alta esistente
        var offertaTop = offertaRepository.findFirstByAstaIdOrderByImportoDesc(astaId);
        if (offertaTop.isPresent() && importo.compareTo(offertaTop.get().getImporto()) <= 0) {
            throw new IllegalArgumentException(
                    "L'offerta deve superare l'offerta attuale di " + offertaTop.get().getImporto());
        }

        Offerta offerta = new Offerta();
        offerta.setImporto(importo);
        offerta.setAsta(asta);
        offerta.setAcquirente(acquirente);
        return offertaRepository.save(offerta);
    }

    // ============ CHIUSURA ASTA (proprietario o admin) ============
    @Transactional
    public Asta chiudiAsta(Long astaId, Long utenteId) {
        Asta asta = astaRepository.findById(astaId)
                .orElseThrow(() -> new IllegalArgumentException("Asta non trovata"));

        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        boolean isAdmin = utente.getRuolo() == Utente.Ruolo.ADMIN;
        boolean isProprietario = asta.getAnnuncio().getVenditore().getId().equals(utenteId);
        if (!isAdmin && !isProprietario) {
            throw new SecurityException("Non hai i permessi per chiudere questa asta");
        }

        asta.setAttiva(false);
        return astaRepository.save(asta);
    }
}