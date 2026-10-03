package com.onana.decoevent.controller;
import com.onana.decoevent.dto.response.FactureResponse;
import com.onana.decoevent.enums.StatutFacture;
import com.onana.decoevent.service.FactureService;
import lombok.RequiredArgsConstructor;
import com.onana.decoevent.exceptions.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestataire/factures")
@RequiredArgsConstructor
public class FactureController {

    private final FactureService factureService;

    @GetMapping
    public ResponseEntity<List<FactureResponse>> findAll() {
        return ResponseEntity.ok(factureService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FactureResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(factureService.findById(id));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<FactureResponse>> findByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(factureService.findByClientId(clientId));
    }

    @PostMapping("/generer/{devisId}")
    public ResponseEntity<FactureResponse> genererDepuisDevis(@PathVariable Long devisId) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED).body(factureService.genererDepuisDevis(devisId));
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<FactureResponse> updateStatut(@PathVariable Long id, @RequestParam StatutFacture statut) throws BadRequestException {
        return ResponseEntity.ok(factureService.updateStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        factureService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
