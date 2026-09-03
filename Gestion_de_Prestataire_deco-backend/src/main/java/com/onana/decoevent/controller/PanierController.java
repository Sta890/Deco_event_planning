package com.onana.decoevent.controller;



import com.onana.decoevent.dto.reponse.PanierResponse;
import com.onana.decoevent.dto.request.AjouterArticlePanierRequest;
import com.onana.decoevent.service.PanierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/panier")
@RequiredArgsConstructor
public class PanierController {

    private final PanierService panierService;

    @GetMapping("/{utilisateurId}")
    public ResponseEntity<PanierResponse> getPanier(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(panierService.getPanier(utilisateurId));
    }

    @PostMapping("/{utilisateurId}/ajouter")
    public ResponseEntity<PanierResponse> ajouterArticle(
            @PathVariable Long utilisateurId,
            @Valid @RequestBody AjouterArticlePanierRequest request) {
        return ResponseEntity.ok(panierService.ajouterArticle(utilisateurId, request));
    }

    @PutMapping("/{utilisateurId}/ligne/{ligneId}")
    public ResponseEntity<PanierResponse> modifierQuantite(
            @PathVariable Long utilisateurId,
            @PathVariable Long ligneId,
            @RequestParam Integer quantite) {
        return ResponseEntity.ok(panierService.modifierQuantite(utilisateurId, ligneId, quantite));
    }

    @DeleteMapping("/{utilisateurId}/ligne/{ligneId}")
    public ResponseEntity<PanierResponse> supprimerArticle(
            @PathVariable Long utilisateurId,
            @PathVariable Long ligneId) {
        return ResponseEntity.ok(panierService.supprimerArticle(utilisateurId, ligneId));
    }

    @DeleteMapping("/{utilisateurId}/vider")
    public ResponseEntity<Void> viderPanier(@PathVariable Long utilisateurId) {
        panierService.viderPanier(utilisateurId);
        return ResponseEntity.noContent().build();
    }
}
