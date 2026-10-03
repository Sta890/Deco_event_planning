package com.onana.decoevent.controller;
import com.onana.decoevent.dto.request.DevisRequest;
import com.onana.decoevent.dto.request.LigneDevisRequest;
import com.onana.decoevent.enums.StatutDevis;
import com.onana.decoevent.dto.response.DevisResponse;
import com.onana.decoevent.service.DevisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestataire/devis")
@RequiredArgsConstructor
public class DevisController {

    private final DevisService devisService;

    @GetMapping
    public ResponseEntity<List<DevisResponse>> findAll() {
        return ResponseEntity.ok(devisService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DevisResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(devisService.findById(id));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<DevisResponse>> findByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(devisService.findByClientId(clientId));
    }

    @PostMapping
    public ResponseEntity<DevisResponse> create(@Valid @RequestBody DevisRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(devisService.create(request));
    }

    @PutMapping("/{id}/lignes")
    public ResponseEntity<DevisResponse> updateLignes(@PathVariable Long id, @Valid @RequestBody List<LigneDevisRequest> lignes) {
        return ResponseEntity.ok(devisService.updateLignes(id, lignes));
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<DevisResponse> updateStatut(@PathVariable Long id, @RequestParam StatutDevis statut) {
        return ResponseEntity.ok(devisService.updateStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        devisService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
