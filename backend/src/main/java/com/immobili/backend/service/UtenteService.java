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

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public Utente registra(Utente nuovo) {
        if (utenteRepository.existsByEmail(nuovo.getEmail())) {
            throw new IllegalArgumentException("Email già registrata");
        }

        if (nuovo.getRuolo() == null) {
            nuovo.setRuolo(Utente.Ruolo.ACQUIRENTE);
        }

        if (nuovo.getRuolo() == Utente.Ruolo.ADMIN) {
            throw new IllegalArgumentException("Non è possibile registrarsi come admin");
        }

        nuovo.setPassword(passwordEncoder.encode(nuovo.getPassword()));
        nuovo.setBannato(false);
        return utenteRepository.save(nuovo);
    }

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

    @Transactional
    public Utente sbannaUtente(Long utenteId, Long adminId) {
        verificaCheSiaAdmin(adminId);
        Utente u = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        u.setBannato(false);
        return utenteRepository.save(u);
    }

    @Transactional
    public Utente nominaAdmin(Long utenteId, Long adminId) {
        verificaCheSiaAdmin(adminId);
        Utente u = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        u.setRuolo(Utente.Ruolo.ADMIN);
        return utenteRepository.save(u);
    }

    private void verificaCheSiaAdmin(Long adminId) {
        Utente admin = utenteRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin non trovato"));
        if (admin.getRuolo() != Utente.Ruolo.ADMIN) {
            throw new SecurityException("Solo gli admin possono effettuare questa operazione");
        }
    }
}