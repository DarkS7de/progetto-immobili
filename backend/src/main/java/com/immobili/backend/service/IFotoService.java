package com.immobili.backend.service;

import com.immobili.backend.entity.Foto;
import java.util.List;
import java.util.Optional;

public interface IFotoService {

    List<Foto> getMetadatiByAnnuncio(Long annuncioId);

    Optional<byte[]> getDatiFoto(Long fotoId);

    Optional<Foto> getFotoMetadati(Long fotoId);
}