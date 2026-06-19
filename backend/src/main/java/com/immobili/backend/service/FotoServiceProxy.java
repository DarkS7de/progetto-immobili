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

@Service
@Primary
public class FotoServiceProxy implements IFotoService {

    @Autowired
    @Qualifier("fotoServiceReal")
    private IFotoService realSubject;

    private final Map<Long, byte[]> cacheByte = new HashMap<>();

    @Override
    public List<Foto> getMetadatiByAnnuncio(Long annuncioId) {
        System.out.println("[PROXY] Richiesta metadati annuncio " + annuncioId + " -> delego al servizio reale");
        return realSubject.getMetadatiByAnnuncio(annuncioId);
    }

    @Override
    public Optional<byte[]> getDatiFoto(Long fotoId) {
        if (cacheByte.containsKey(fotoId)) {
            System.out.println("[PROXY] Foto " + fotoId + " trovata in CACHE - nessuna query al DB");
            return Optional.of(cacheByte.get(fotoId));
        }

        System.out.println("[PROXY] Foto " + fotoId + " NON in cache, chiedo al servizio reale...");
        Optional<byte[]> datiOpt = realSubject.getDatiFoto(fotoId);
        datiOpt.ifPresent(dati -> cacheByte.put(fotoId, dati));

        return datiOpt;
    }

    @Override
    public Optional<Foto> getFotoMetadati(Long fotoId) {
        return realSubject.getFotoMetadati(fotoId);
    }
}