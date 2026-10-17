package com.onana.decoevent.controller;
import com.onana.decoevent.dto.response.HistoriqueResponse;
import com.onana.decoevent.service.HistoriqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestataire/historique")
@RequiredArgsConstructor
public class HistoriqueController {

    private final HistoriqueService historiqueService;

    @GetMapping
    public ResponseEntity<List<HistoriqueResponse>> findAll() {
        return ResponseEntity.ok(historiqueService.findAll());
    }
}
