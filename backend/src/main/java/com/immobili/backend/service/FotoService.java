package com.immobili.backend.service;

import com.immobili.backend.entity.Foto;
import com.immobili.backend.repository.FotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * REAL SUBJECT del pattern Proxy.
 * Implementa la logica vera: parla al DB tramite il repository.
 * Non sa nulla del Proxy che gli sta davanti.
 *
 * NOTA: l'annotazione "fotoServiceReal" è il NOME del bean Spring.
 * Serve perchè avremo due implementazioni della stessa interfaccia
 * (questa e il Proxy) e Spring deve poterle distinguere.
 */
@Service("fotoServiceReal")
public class FotoService implements IFotoService {

    @Autowired
    private FotoRepository fotoRepository;

    @Override
    public List<Foto> getMetadatiByAnnuncio(Long annuncioId) {
        System.out.println("[REAL]  Carico metadati foto per annuncio " + annuncioId);
        return fotoRepository.findByAnnuncioIdOrderByOrdineAsc(annuncioId);
    }

    @Override
    public Optional<byte[]> getDatiFoto(Long fotoId) {
        System.out.println("[REAL]  Carico BYTE della foto " + fotoId + " dal DB (operazione pesante)");
        return fotoRepository.findById(fotoId).map(Foto::getDati);
    }

    @Override
    public Optional<Foto> getFotoMetadati(Long fotoId) {
        return fotoRepository.findById(fotoId);
    }
}