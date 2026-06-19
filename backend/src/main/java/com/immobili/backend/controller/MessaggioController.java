package com.immobili.backend.controller;

import com.immobili.backend.entity.Annuncio;
import com.immobili.backend.entity.Messaggio;
import com.immobili.backend.entity.Utente;
import com.immobili.backend.repository.AnnuncioRepository;
import com.immobili.backend.repository.MessaggioRepository;
import com.immobili.backend.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messaggi")
@CrossOrigin(origins = "http://localhost:4200")
public class MessaggioController {

    @Autowired
    private MessaggioRepository messaggioRepository;

    @Autowired
    private AnnuncioRepository annuncioRepository;

    @Autowired
    private UtenteRepository utenteRepository;


    @PostMapping("/annuncio/{annuncioId}")
    public ResponseEntity<?> invia(@PathVariable Long annuncioId, @RequestBody Map<String, Object> body) {
        Annuncio annuncio = annuncioRepository.findById(annuncioId).orElse(null);
        if (annuncio == null) {
            return ResponseEntity.badRequest().body(Map.of("errore", "Annuncio non trovato"));
        }

        Messaggio msg = new Messaggio();
        msg.setOggetto((String) body.get("oggetto"));
        msg.setTesto((String) body.get("testo"));
        msg.setNomeMittente((String) body.get("nomeMittente"));
        msg.setEmailMittente((String) body.get("emailMittente"));
        msg.setTelefonoMittente((String) body.getOrDefault("telefonoMittente", null));
        msg.setAnnuncio(annuncio);
        msg.setDestinatario(annuncio.getVenditore());

        if (body.get("mittenteId") != null) {
            Long mittenteId = Long.valueOf(body.get("mittenteId").toString());
            Utente mittente = utenteRepository.findById(mittenteId).orElse(null);
            msg.setMittente(mittente);
        }

        Messaggio salvato = messaggioRepository.save(msg);
        return ResponseEntity.ok(Map.of("id", salvato.getId(), "messaggio", "Messaggio inviato"));
    }


    @GetMapping("/ricevuti/{utenteId}")
    public List<Messaggio> ricevuti(@PathVariable Long utenteId) {
        return messaggioRepository.findByDestinatarioIdOrderByDataDesc(utenteId);
    }

    @GetMapping("/non-letti/{utenteId}")
    public Map<String, Long> nonLetti(@PathVariable Long utenteId) {
        return Map.of("count", messaggioRepository.countByDestinatarioIdAndLettoFalse(utenteId));
    }


    @PatchMapping("/{id}/letto")
    public ResponseEntity<?> segnaLetto(@PathVariable Long id) {
        return messaggioRepository.findById(id)
                .map(m -> {
                    m.setLetto(true);
                    messaggioRepository.save(m);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}