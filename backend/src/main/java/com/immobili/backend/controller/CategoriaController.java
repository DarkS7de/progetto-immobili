package com.immobili.backend.controller;

import com.immobili.backend.entity.Categoria;
import com.immobili.backend.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorie")
@CrossOrigin(origins = "http://localhost:4200")  // permette ad Angular di chiamarci
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    // GET /api/categorie  → lista di tutte le categorie
    @GetMapping
    public List<Categoria> getAll() {
        return categoriaRepository.findAll();
    }

    // GET /api/categorie/{id}  → singola categoria per id
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getById(@PathVariable Long id) {
        return categoriaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}