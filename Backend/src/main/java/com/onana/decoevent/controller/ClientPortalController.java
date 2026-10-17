package com.onana.decoevent.controller;
import com.onana.decoevent.dto.response.ArticleResponse;
import com.onana.decoevent.dto.response.DevisResponse;
import com.onana.decoevent.dto.response.FactureResponse;
import com.onana.decoevent.dto.response.PrestationResponse;
import com.onana.decoevent.dto.request.DemandeDevisRequest;
import com.onana.decoevent.dto.response.NotificationResponse;
import com.onana.decoevent.enums.TypeEvenement;
import com.onana.decoevent.service.ArticleService;
import com.onana.decoevent.service.DevisService;
import com.onana.decoevent.service.FactureService;
import com.onana.decoevent.service.NotificationService;
import com.onana.decoevent.service.PrestationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientPortalController {

    private final ArticleService articleService;
    private final DevisService devisService;
    private final FactureService factureService;
    private final PrestationService prestationService;
    private final NotificationService notificationService;

    @GetMapping("/articles")
    public ResponseEntity<List<ArticleResponse>> getCatalogue(
            @RequestParam(required = false) TypeEvenement typeEvenement) {
        if (typeEvenement != null) {
            return ResponseEntity.ok(articleService.findByTypeEvenement(typeEvenement));
        }
        return ResponseEntity.ok(articleService.findAll());
    }

    @GetMapping("/mes-devis")
    public ResponseEntity<List<DevisResponse>> getMesDevis(Authentication authentication) {
        return ResponseEntity.ok(devisService.findByClientEmail(authentication.getName()));
    }

    @GetMapping("/mes-factures")
    public ResponseEntity<List<FactureResponse>> getMesFactures(Authentication authentication) {
        return ResponseEntity.ok(factureService.findByClientEmail(authentication.getName()));
    }

    @PostMapping("/demandes-devis")
    public ResponseEntity<PrestationResponse> demanderDevis(
            @Valid @RequestBody DemandeDevisRequest request,
            Authentication authentication) {
        // Un client authentifié ne peut demander un devis que pour son propre compte
        request.setEmail(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(prestationService.creerDepuisDemande(request));
    }

    // ---------------------------------------------------------- Notifications

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationResponse>> getMesNotifications(Authentication authentication) {
        return ResponseEntity.ok(notificationService.findAll(authentication.getName()));
    }

    /** Nombre de notifications non lues, pour le badge de la cloche. */
    @GetMapping("/notifications/non-lues")
    public ResponseEntity<Map<String, Long>> getNombreNonLues(Authentication authentication) {
        return ResponseEntity.ok(Map.of("count", notificationService.countNonLues(authentication.getName())));
    }

    @PutMapping("/notifications/{id}/lue")
    public ResponseEntity<NotificationResponse> marquerCommeLue(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(notificationService.marquerCommeLue(id, authentication.getName()));
    }

    @PutMapping("/notifications/tout-lire")
    public ResponseEntity<Map<String, Long>> marquerToutesCommeLues(Authentication authentication) {
        long marquees = notificationService.marquerToutesCommeLues(authentication.getName());
        return ResponseEntity.ok(Map.of("count", marquees));
    }
}
