package com.onana.decoevent.service;

import com.onana.decoevent.dto.response.PanierResponse;
import com.onana.decoevent.dto.request.AjouterArticlePanierRequest;

import com.onana.decoevent.exceptions.ResourceNotFoundException;
import com.onana.decoevent.mapper.PanierMapper;
import com.onana.decoevent.models.*;
import com.onana.decoevent.repositories.ArticleRepository;
import com.onana.decoevent.repositories.LignePanierRepository;
import com.onana.decoevent.repositories.PanierRepository;
import com.onana.decoevent.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PanierService {

    private final PanierRepository panierRepository;
    private final LignePanierRepository lignePanierRepository;
    private final ArticleRepository articleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PanierMapper panierMapper;

    @Transactional
    public PanierResponse getPanier(Long utilisateurId) {
        Panier panier = panierRepository.findByUtilisateurId(utilisateurId)
                .orElseGet(() -> creerPanier(utilisateurId));
        return panierMapper.toPanierResponse(panier);
    }

    @Transactional
    public PanierResponse ajouterArticle(Long utilisateurId, AjouterArticlePanierRequest request) {
        Panier panier = panierRepository.findByUtilisateurId(utilisateurId)
                .orElseGet(() -> creerPanier(utilisateurId));

        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article non trouvé"));

        Optional<LignePanier> existante = panier.getLignes().stream()
                .filter(l -> l.getArticle().getId().equals(request.getArticleId()))
                .findFirst();

        if (existante.isPresent()) {
            LignePanier ligne = existante.get();
            ligne.setQuantite(ligne.getQuantite() + request.getQuantite());
            ligne.setSousTotal(article.getPrixUnitaire().multiply(BigDecimal.valueOf(ligne.getQuantite())));
        } else {
            LignePanier nouvelleLigne = LignePanier.builder()
                    .panier(panier)
                    .article(article)
                    .quantite(request.getQuantite())
                    .sousTotal(article.getPrixUnitaire().multiply(BigDecimal.valueOf(request.getQuantite())))
                    .build();

            panier.getLignes().add(nouvelleLigne);
        }

        // On sauvegarde et on force le flush pour que les ID soient générés
        Panier panierEnregistre = panierRepository.saveAndFlush(panier);

        // On recharge explicitement depuis la BDD pour renvoyer le panier complet à jour
        Panier panierComplet = panierRepository.findByUtilisateurId(utilisateurId)
                .orElse(panierEnregistre);

        return panierMapper.toPanierResponse(panierComplet);
    }

    @Transactional
    public PanierResponse modifierQuantite(Long utilisateurId, Long ligneId, Integer quantite) {
        Panier panier = panierRepository.findByUtilisateurId(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Panier non trouvé"));

        LignePanier ligne = lignePanierRepository.findById(ligneId)
                .orElseThrow(() -> new ResourceNotFoundException("Ligne non trouvée avec l'id : " + ligneId));

        if (!ligne.getPanier().getId().equals(panier.getId())) {
            throw new ResourceNotFoundException("Ligne non trouvée avec l'id : " + ligneId);
        }

        if (quantite <= 0) {
            panier.getLignes().remove(ligne);
            lignePanierRepository.delete(ligne);
        } else {
            ligne.setQuantite(quantite);
            ligne.setSousTotal(ligne.getArticle().getPrixUnitaire().multiply(BigDecimal.valueOf(quantite)));
            lignePanierRepository.save(ligne);
        }

        return panierMapper.toPanierResponse(panierRepository.save(panier));
    }

    @Transactional
    public PanierResponse supprimerArticle(Long utilisateurId, Long ligneId) {
        Panier panier = panierRepository.findByUtilisateurId(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Panier non trouvé"));

        LignePanier ligne = lignePanierRepository.findById(ligneId)
                .orElseThrow(() -> new ResourceNotFoundException("Ligne non trouvée avec l'id : " + ligneId));

        if (!ligne.getPanier().getId().equals(panier.getId())) {
            throw new ResourceNotFoundException("Ligne non trouvée avec l'id : " + ligneId);
        }

        panier.getLignes().remove(ligne);
        lignePanierRepository.delete(ligne);

        return panierMapper.toPanierResponse(panierRepository.save(panier));
    }

    @Transactional
    public void viderPanier(Long utilisateurId) {
        Panier panier = panierRepository.findByUtilisateurId(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Panier non trouvé"));
        panier.getLignes().clear();
        lignePanierRepository.deleteByPanierId(panier.getId());
        panierRepository.save(panier);
    }

    private Panier creerPanier(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id : " + utilisateurId));
        return panierRepository.save(
                Panier.builder()
                        .utilisateur(utilisateur)
                        .build()
        );
    }
}
