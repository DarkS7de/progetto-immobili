package com.immobili.backend.service;

import com.immobili.backend.entity.Utente;
import com.immobili.backend.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    // BCrypt: standard de facto per l'hashing delle password.
    // Genera hash diversi per la stessa password (salt automatico) e
    // resiste agli attacchi brute-force.
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ================================================================
    // REGISTRAZIONE
    // ================================================================
    @Transactional
    public Utente registra(Utente nuovo) {
        if (utenteRepository.existsByEmail(nuovo.getEmail())) {
            throw new IllegalArgumentException("Email già registrata");
        }
        // Default: ACQUIRENTE. Per registrarsi come VENDITORE servirebbe un
        // flag esplicito dal frontend (la traccia non vieta che siano gli utenti
        // a scegliere il loro ruolo alla registrazione).
        if (nuovo.getRuolo() == null) {
            nuovo.setRuolo(Utente.Ruolo.ACQUIRENTE);
        }
        // Niente ADMIN tramite registrazione pubblica!
        if (nuovo.getRuolo() == Utente.Ruolo.ADMIN) {
            throw new IllegalArgumentException("Non è possibile registrarsi come admin");
        }
        // Hash della password prima di salvare
        nuovo.setPassword(passwordEncoder.encode(nuovo.getPassword()));
        nuovo.setBannato(false);
        return utenteRepository.save(nuovo);
    }

    // ================================================================
    // LOGIN - controlla credenziali, restituisce l'utente se ok
    // ================================================================
    public Optional<Utente> login(String email, String passwordInChiaro) {
        Optional<Utente> utenteOpt = utenteRepository.findByEmail(email);
        if (utenteOpt.isEmpty()) return Optional.empty();

        Utente u = utenteOpt.get();
        if (Boolean.TRUE.equals(u.getBannato())) {
            throw new SecurityException("Utente bannato");
        }
        if (!passwordEncoder.matches(passwordInChiaro, u.getPassword())) {
            return Optional.empty();
        }
        return Optional.of(u);
    }

    // ================================================================
    // BAN UTENTE - solo admin (verifica fatta nel controller)
    // ================================================================
    @Transactional
    public Utente bannaUtente(Long utenteId, Long adminId) {
        verificaCheSiaAdmin(adminId);
        Utente u = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        if (u.getRuolo() == Utente.Ruolo.ADMIN) {
            throw new IllegalStateException("Non puoi bannare un admin");
        }
        u.setBannato(true);
        return utenteRepository.save(u);
    }

    // ================================================================
    // SBANNA - utile per ripristinare un utente bannato
    // ================================================================
    @Transactional
    public Utente sbannaUtente(Long utenteId, Long adminId) {
        verificaCheSiaAdmin(adminId);
        Utente u = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        u.setBannato(false);
        return utenteRepository.save(u);
    }

    // ================================================================
    // NOMINA ADMIN - solo un admin esistente può creare altri admin
    // ================================================================
    @Transactional
    public Utente nominaAdmin(Long utenteId, Long adminId) {
        verificaCheSiaAdmin(adminId);
        Utente u = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        u.setRuolo(Utente.Ruolo.ADMIN);
        return utenteRepository.save(u);
    }

    // ================================================================
    // HELPER
    // ================================================================
    private void verificaCheSiaAdmin(Long adminId) {
        Utente admin = utenteRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin non trovato"));
        if (admin.getRuolo() != Utente.Ruolo.ADMIN) {
            throw new SecurityException("Solo gli admin possono effettuare questa operazione");
        }
    }
}