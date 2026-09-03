package com.onana.decoevent.controller;
import com.onana.decoevent.dto.reponse.PrestationResponse;
import com.onana.decoevent.dto.request.PrestationRequest;
import com.onana.decoevent.service.PrestationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestataire/prestations")
@RequiredArgsConstructor
public class PrestationController {

    private final PrestationService prestationService;

    @GetMapping
    public ResponseEntity<List<PrestationResponse>> findAll() {
        return ResponseEntity.ok(prestationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestationResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(prestationService.findById(id));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<PrestationResponse>> findByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(prestationService.findByClientId(clientId));
    }

    @PostMapping
    public ResponseEntity<PrestationResponse> create(@Valid @RequestBody PrestationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prestationService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrestationResponse> update(@PathVariable Long id, @Valid @RequestBody PrestationRequest request) {
        return ResponseEntity.ok(prestationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        prestationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
