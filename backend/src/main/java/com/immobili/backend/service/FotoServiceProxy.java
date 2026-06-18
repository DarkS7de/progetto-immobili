package com.immobili.backend.service;

import com.immobili.backend.entity.Foto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * PROXY del pattern Proxy (Gang of Four).
 *
 * Implementa la STESSA interfaccia del RealSubject, ma aggiunge due benefici:
 *  1. LAZY LOADING ESPLICITO: i dati binari (byte[]) di una foto vengono
 *     richiesti al servizio reale solo quando qualcuno li chiede davvero.
 *  2. CACHE: se la stessa foto viene richiesta più volte, la seconda volta
 *     i byte sono già in memoria e non si tocca il DB.
 *
 * L'annotazione @Primary dice a Spring: "quando qualcuno chiede un IFotoService
 * senza specificare quale, usa il Proxy (questo bean) come default".
 * Cosi' il controller userà automaticamente il Proxy senza saperlo,
 * che è esattamente lo spirito del pattern: il client è ignaro.
 */
@Service
@Primary
public class FotoServiceProxy implements IFotoService {

    @Autowired
    @Qualifier("fotoServiceReal")
    private IFotoService realSubject;   // <-- il RealSubject che il proxy "wrappa"

    // Cache in memoria: id foto → byte[]
    private final Map<Long, byte[]> cacheByte = new HashMap<>();

    @Override
    public List<Foto> getMetadatiByAnnuncio(Long annuncioId) {
        // I metadati sono leggeri, li deleghiamo direttamente al servizio reale.
        System.out.println("[PROXY] Richiesta metadati annuncio " + annuncioId + " -> delego al servizio reale");
        return realSubject.getMetadatiByAnnuncio(annuncioId);
    }

    @Override
    public Optional<byte[]> getDatiFoto(Long fotoId) {
        // Qui sta la magia del proxy: prima controlla la cache.
        if (cacheByte.containsKey(fotoId)) {
            System.out.println("[PROXY] Foto " + fotoId + " trovata in CACHE - nessuna query al DB");
            return Optional.of(cacheByte.get(fotoId));
        }

        // Cache miss: chiede al RealSubject (che andrà al DB).
        System.out.println("[PROXY] Foto " + fotoId + " NON in cache, chiedo al servizio reale...");
        Optional<byte[]> datiOpt = realSubject.getDatiFoto(fotoId);

        // Se l'abbiamo trovata, la salviamo in cache per le richieste future.
        datiOpt.ifPresent(dati -> cacheByte.put(fotoId, dati));

        return datiOpt;
    }

    @Override
    public Optional<Foto> getFotoMetadati(Long fotoId) {
        return realSubject.getFotoMetadati(fotoId);
    }
}