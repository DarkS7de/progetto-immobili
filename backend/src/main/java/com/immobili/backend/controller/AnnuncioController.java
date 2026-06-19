package com.immobili.backend.controller;

import com.immobili.backend.entity.Annuncio;
import com.immobili.backend.entity.Annuncio.TipoTransazione;
import com.immobili.backend.repository.AnnuncioRepository;
import com.immobili.backend.service.AnnuncioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/annunci")
@CrossOrigin(origins = "http://localhost:4200")
public class AnnuncioController {

    @Autowired
    private AnnuncioService annuncioService;

    @Autowired
    private AnnuncioRepository annuncioRepository;


    @GetMapping
    public List<Annuncio> getAll() {
        return annuncioService.tuttiAttivi();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Annuncio> getById(@PathVariable Long id) {
        return annuncioService.trovaPerId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/venditore/{venditoreId}")
    public List<Annuncio> getByVenditore(@PathVariable Long venditoreId) {
        return annuncioRepository.findByVenditoreId(venditoreId);
    }

    @GetMapping("/ricerca")
    public List<Annuncio> ricerca(
            @RequestParam(required = false) TipoTransazione tipo,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) BigDecimal prezzoMin,
            @RequestParam(required = false) BigDecimal prezzoMax,
            @RequestParam(required = false) Integer mqMin,
            @RequestParam(required = false) Integer mqMax
    ) {
        return annuncioService.ricerca(tipo, categoriaId, prezzoMin, prezzoMax, mqMin, mqMax);
    }


    @PostMapping
    public ResponseEntity<?> crea(@RequestBody Annuncio annuncio, @RequestParam Long venditoreId) {
        try {
            return ResponseEntity.ok(annuncioService.creaAnnuncio(annuncio, venditoreId));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("errore", ex.getMessage()));
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> modifica(
            @PathVariable Long id,
            @RequestBody Annuncio annuncio,
            @RequestParam Long utenteId) {
        try {
            return ResponseEntity.ok(annuncioService.modificaAnnuncio(id, annuncio, utenteId));
        } catch (SecurityException ex) {
            return ResponseEntity.status(403).body(Map.of("errore", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("errore", ex.getMessage()));
        }
    }


    @PatchMapping("/{id}/ribasso")
    public ResponseEntity<?> ribassa(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> body,
            @RequestParam Long utenteId) {
        try {
            BigDecimal nuovoPrezzo = body.get("nuovoPrezzo");
            return ResponseEntity.ok(annuncioService.ribassaPrezzo(id, nuovoPrezzo, utenteId));
        } catch (SecurityException ex) {
            return ResponseEntity.status(403).body(Map.of("errore", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("errore", ex.getMessage()));
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> elimina(@PathVariable Long id, @RequestParam Long utenteId) {
        try {
            annuncioService.eliminaAnnuncio(id, utenteId);
            return ResponseEntity.noContent().build();
        } catch (SecurityException ex) {
            return ResponseEntity.status(403).body(Map.of("errore", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("errore", ex.getMessage()));
        }
    }
}