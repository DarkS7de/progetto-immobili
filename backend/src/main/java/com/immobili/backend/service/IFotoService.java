package com.immobili.backend.service;

import com.immobili.backend.entity.Foto;
import java.util.List;
import java.util.Optional;

/**
 * SUBJECT del pattern Proxy (Gang of Four).
 * Definisce l'interfaccia comune che sia il RealSubject (FotoService)
 * sia il Proxy (FotoServiceProxy) devono implementare.
 * Il client (controller) dipende solo da questa interfaccia.
 */
public interface IFotoService {

    /** Restituisce i metadati delle foto di un annuncio (no dati binari). */
    List<Foto> getMetadatiByAnnuncio(Long annuncioId);

    /** Restituisce SOLO i byte di una singola foto (operazione "pesante"). */
    Optional<byte[]> getDatiFoto(Long fotoId);

    /** Restituisce i metadati di una singola foto (per content-type ecc.). */
    Optional<Foto> getFotoMetadati(Long fotoId);
}