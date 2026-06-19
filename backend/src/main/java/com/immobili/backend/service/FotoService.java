package com.immobili.backend.service;

import com.immobili.backend.entity.Foto;
import com.immobili.backend.repository.FotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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