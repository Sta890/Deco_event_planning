package com.onana.decoevent.controller;
import com.onana.decoevent.dto.reponse.ArticleResponse;
import com.onana.decoevent.dto.reponse.DevisResponse;
import com.onana.decoevent.dto.reponse.FactureResponse;
import com.onana.decoevent.enums.TypeEvenement;
import com.onana.decoevent.service.ArticleService;
import com.onana.decoevent.service.DevisService;
import com.onana.decoevent.service.FactureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientPortalController {

    private final ArticleService articleService;
    private final DevisService devisService;
    private final FactureService factureService;

    @GetMapping("/articles")
    public ResponseEntity<List<ArticleResponse>> getCatalogue(
            @RequestParam(required = false) TypeEvenement typeEvenement) {
        if (typeEvenement != null) {
            return ResponseEntity.ok(articleService.findByTypeEvenement(typeEvenement));
        }
        return ResponseEntity.ok(articleService.findAll());
    }

    @GetMapping("/devis/{clientId}")
    public ResponseEntity<List<DevisResponse>> getMesDevis(@PathVariable Long clientId) {
        return ResponseEntity.ok(devisService.findByClientId(clientId));
    }

    @GetMapping("/factures/{clientId}")
    public ResponseEntity<List<FactureResponse>> getMesFactures(@PathVariable Long clientId) {
        return ResponseEntity.ok(factureService.findByClientId(clientId));
    }
}