package com.immobili.backend.controller;

import com.immobili.backend.entity.Utente;
import com.immobili.backend.repository.UtenteRepository;
import com.immobili.backend.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/utenti")
@CrossOrigin(origins = "http://localhost:4200")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private UtenteRepository utenteRepository;


    @GetMapping
    public List<Utente> getAll() {
        return utenteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Utente> getById(@PathVariable Long id) {
        return utenteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/registrazione")
    public ResponseEntity<?> registra(@RequestBody Utente nuovo) {
        try {
            Utente salvato = utenteService.registra(nuovo);
            return ResponseEntity.ok(salvato);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("errore", ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenziali) {
        try {
            return utenteService.login(credenziali.get("email"), credenziali.get("password"))
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(401).body(Map.of("errore", "Credenziali errate")));
        } catch (SecurityException ex) {
            return ResponseEntity.status(403).body(Map.of("errore", ex.getMessage()));
        }
    }

    @PostMapping("/{id}/banna")
    public ResponseEntity<?> banna(@PathVariable Long id, @RequestParam Long adminId) {
        try {
            return ResponseEntity.ok(utenteService.bannaUtente(id, adminId));
        } catch (SecurityException ex) {
            return ResponseEntity.status(403).body(Map.of("errore", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("errore", ex.getMessage()));
        }
    }

    @PostMapping("/{id}/sbanna")
    public ResponseEntity<?> sbanna(@PathVariable Long id, @RequestParam Long adminId) {
        try {
            return ResponseEntity.ok(utenteService.sbannaUtente(id, adminId));
        } catch (SecurityException ex) {
            return ResponseEntity.status(403).body(Map.of("errore", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("errore", ex.getMessage()));
        }
    }

    @PostMapping("/{id}/nomina-admin")
    public ResponseEntity<?> nominaAdmin(@PathVariable Long id, @RequestParam Long adminId) {
        try {
            return ResponseEntity.ok(utenteService.nominaAdmin(id, adminId));
        } catch (SecurityException ex) {
            return ResponseEntity.status(403).body(Map.of("errore", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("errore", ex.getMessage()));
        }
    }
}