package com.immobili.backend.controller;

import com.immobili.backend.entity.Annuncio;
import com.immobili.backend.entity.Foto;
import com.immobili.backend.repository.AnnuncioRepository;
import com.immobili.backend.repository.FotoRepository;
import com.immobili.backend.service.IFotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/foto")
@CrossOrigin(origins = "http://localhost:4200")
public class FotoController {

    @Autowired
    private IFotoService fotoService;

    @Autowired
    private FotoRepository fotoRepository;

    @Autowired
    private AnnuncioRepository annuncioRepository;

    // GET metadati foto di un annuncio (passa dal Proxy)
    @GetMapping("/annuncio/{annuncioId}")
    public List<Foto> getByAnnuncio(@PathVariable Long annuncioId) {
        return fotoService.getMetadatiByAnnuncio(annuncioId);
    }

    // GET i byte di una foto come immagine (passa dal Proxy con caching)
    @GetMapping("/{id}/raw")
    public ResponseEntity<byte[]> getRawImage(@PathVariable Long id) {
        var metadati = fotoService.getFotoMetadati(id);
        if (metadati.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return fotoService.getDatiFoto(id)
                .map(dati -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(metadati.get().getContentType()))
                        .body(dati))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST upload di una foto per un annuncio
    // multipart/form-data con campo "file"
    @PostMapping("/annuncio/{annuncioId}")
    public ResponseEntity<?> upload(
            @PathVariable Long annuncioId,
            @RequestParam("file") MultipartFile file) {
        try {
            Annuncio annuncio = annuncioRepository.findById(annuncioId)
                    .orElse(null);
            if (annuncio == null) {
                return ResponseEntity.badRequest().body(Map.of("errore", "Annuncio non trovato"));
            }

            Foto foto = new Foto();
            foto.setDati(file.getBytes());
            foto.setContentType(file.getContentType());
            foto.setNomeFile(file.getOriginalFilename());
            foto.setOrdine(0);
            foto.setAnnuncio(annuncio);

            Foto salvata = fotoRepository.save(foto);
            // Restituisco solo l'id e i metadati, non i byte
            return ResponseEntity.ok(Map.of(
                    "id", salvata.getId(),
                    "nomeFile", salvata.getNomeFile(),
                    "contentType", salvata.getContentType()
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("errore", "Errore durante l'upload"));
        }
    }

    // DELETE una foto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> elimina(@PathVariable Long id) {
        if (!fotoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        fotoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}