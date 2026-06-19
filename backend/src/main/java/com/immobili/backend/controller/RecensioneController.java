package com.immobili.backend.controller;

import com.immobili.backend.entity.Recensione;
import com.immobili.backend.repository.RecensioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recensioni")
@CrossOrigin(origins = "http://localhost:4200")
public class RecensioneController {

    @Autowired
    private RecensioneRepository recensioneRepository;


    @GetMapping("/annuncio/{annuncioId}")
    public List<Recensione> getByAnnuncio(@PathVariable Long annuncioId) {
        return recensioneRepository.findByAnnuncioIdOrderByDataDesc(annuncioId);
    }


    @PostMapping
    public ResponseEntity<Recensione> crea(@RequestBody Recensione recensione) {
        Recensione salvata = recensioneRepository.save(recensione);
        return ResponseEntity.ok(salvata);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> elimina(@PathVariable Long id) {
        if (!recensioneRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        recensioneRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}