package com.onana.decoevent.controller;


import com.onana.decoevent.dto.response.PanierResponse;
import com.onana.decoevent.dto.request.AjouterArticlePanierRequest;
import com.onana.decoevent.exceptions.ResourceNotFoundException;
import com.onana.decoevent.models.Utilisateur;
import com.onana.decoevent.repositories.UtilisateurRepository;
import com.onana.decoevent.service.PanierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/panier")
@RequiredArgsConstructor
public class PanierController {

    private final PanierService panierService;
    private final UtilisateurRepository utilisateurRepository;

    @GetMapping("/{utilisateurId}")
    public ResponseEntity<PanierResponse> getPanier(@PathVariable Long utilisateurId, Authentication authentication) {
        verifierProprietaire(utilisateurId, authentication);
        return ResponseEntity.ok(panierService.getPanier(utilisateurId));
    }

    @PostMapping("/{utilisateurId}/ajouter")
    public ResponseEntity<PanierResponse> ajouterArticle(
            @PathVariable Long utilisateurId,
            @Valid @RequestBody AjouterArticlePanierRequest request,
            Authentication authentication) {
        verifierProprietaire(utilisateurId, authentication);
        return ResponseEntity.ok(panierService.ajouterArticle(utilisateurId, request));
    }

    @PutMapping("/{utilisateurId}/ligne/{ligneId}")
    public ResponseEntity<PanierResponse> modifierQuantite(
            @PathVariable Long utilisateurId,
            @PathVariable Long ligneId,
            @RequestParam Integer quantite,
            Authentication authentication) {
        verifierProprietaire(utilisateurId, authentication);
        return ResponseEntity.ok(panierService.modifierQuantite(utilisateurId, ligneId, quantite));
    }

    @DeleteMapping("/{utilisateurId}/ligne/{ligneId}")
    public ResponseEntity<PanierResponse> supprimerArticle(
            @PathVariable Long utilisateurId,
            @PathVariable Long ligneId,
            Authentication authentication) {
        verifierProprietaire(utilisateurId, authentication);
        return ResponseEntity.ok(panierService.supprimerArticle(utilisateurId, ligneId));
    }

    @DeleteMapping("/{utilisateurId}/vider")
    public ResponseEntity<Void> viderPanier(@PathVariable Long utilisateurId, Authentication authentication) {
        verifierProprietaire(utilisateurId, authentication);
        panierService.viderPanier(utilisateurId);
        return ResponseEntity.noContent().build();
    }

    private void verifierProprietaire(Long utilisateurId, Authentication authentication) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        if (!utilisateur.getId().equals(utilisateurId)) {
            throw new AccessDeniedException("Accès refusé au panier d'un autre utilisateur");
        }
    }
}
