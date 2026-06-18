package com.immobili.backend.controller;

import com.immobili.backend.entity.Offerta;
import com.immobili.backend.repository.OffertaRepository;
import com.immobili.backend.service.AstaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/offerte")
@CrossOrigin(origins = "http://localhost:4200")
public class OffertaController {

    @Autowired
    private OffertaRepository offertaRepository;

    @Autowired
    private AstaService astaService;

    @GetMapping("/asta/{astaId}")
    public List<Offerta> getByAsta(@PathVariable Long astaId) {
        return offertaRepository.findByAstaIdOrderByImportoDesc(astaId);
    }

    @GetMapping("/asta/{astaId}/top")
    public ResponseEntity<Offerta> getTop(@PathVariable Long astaId) {
        return offertaRepository.findFirstByAstaIdOrderByImportoDesc(astaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/asta/{astaId}")
    public ResponseEntity<?> fai(@PathVariable Long astaId,
                                 @RequestBody Map<String, BigDecimal> body,
                                 @RequestParam Long acquirenteId) {
        try {
            BigDecimal importo = body.get("importo");
            return ResponseEntity.ok(astaService.faiOfferta(astaId, importo, acquirenteId));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("errore", ex.getMessage()));
        }
    }
}