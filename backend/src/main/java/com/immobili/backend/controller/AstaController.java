package com.immobili.backend.controller;

import com.immobili.backend.entity.Asta;
import com.immobili.backend.repository.AstaRepository;
import com.immobili.backend.service.AstaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/aste")
@CrossOrigin(origins = "http://localhost:4200")
public class AstaController {

    @Autowired
    private AstaRepository astaRepository;

    @Autowired
    private AstaService astaService;

    @GetMapping
    public List<Asta> getAttive() {
        return astaRepository.findByAttivaTrue();
    }

    @GetMapping("/annuncio/{annuncioId}")
    public ResponseEntity<Asta> getByAnnuncio(@PathVariable Long annuncioId) {
        return astaRepository.findByAnnuncioId(annuncioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/annuncio/{annuncioId}")
    public ResponseEntity<?> crea(@PathVariable Long annuncioId,
                                  @RequestBody Asta asta,
                                  @RequestParam Long utenteId) {
        try {
            return ResponseEntity.ok(astaService.creaAsta(annuncioId, asta, utenteId));
        } catch (SecurityException ex) {
            return ResponseEntity.status(403).body(Map.of("errore", ex.getMessage()));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("errore", ex.getMessage()));
        }
    }

    @PatchMapping("/{id}/chiudi")
    public ResponseEntity<?> chiudi(@PathVariable Long id, @RequestParam Long utenteId) {
        try {
            return ResponseEntity.ok(astaService.chiudiAsta(id, utenteId));
        } catch (SecurityException ex) {
            return ResponseEntity.status(403).body(Map.of("errore", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("errore", ex.getMessage()));
        }
    }
}