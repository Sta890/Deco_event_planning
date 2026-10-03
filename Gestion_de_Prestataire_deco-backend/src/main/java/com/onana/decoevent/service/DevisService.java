package com.onana.decoevent.service;

import com.onana.decoevent.dto.response.DevisResponse;
import com.onana.decoevent.dto.request.DevisRequest;
import com.onana.decoevent.dto.request.LigneDevisRequest;
import com.onana.decoevent.enums.StatutDevis;
import com.onana.decoevent.exceptions.ResourceNotFoundException;
import com.onana.decoevent.mapper.DevisMapper;
import com.onana.decoevent.mapper.LigneDevisMapper;
import com.onana.decoevent.models.Article;
import com.onana.decoevent.models.Devis;
import com.onana.decoevent.models.LigneDevis;
import com.onana.decoevent.models.Prestation;
import com.onana.decoevent.repositories.ArticleRepository;
import com.onana.decoevent.repositories.DevisRepository;
import com.onana.decoevent.repositories.LigneDevisRepository;
import com.onana.decoevent.repositories.PrestationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DevisService {

    private final DevisRepository devisRepository;
    private final PrestationRepository prestationRepository;
    private final ArticleRepository articleRepository;
    private final LigneDevisRepository ligneDevisRepository;
    private final DevisMapper devisMapper;
    private final LigneDevisMapper ligneDevisMapper;
    private final HistoriqueService historiqueService;

    @Transactional(readOnly = true)
    public List<DevisResponse> findAll() {
        log.info("Récupération de tous les devis");
        return devisMapper.toResponseList(devisRepository.findAll());
    }

    @Transactional(readOnly = true)
    public DevisResponse findById(Long id) {
        log.info("Récupération du devis avec l'id : {}", id);
        return devisMapper.toResponse(
                devisRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Devis non trouvé avec l'id : " + id))
        );
    }

    @Transactional(readOnly = true)
    public List<DevisResponse> findByClientId(Long clientId) {
        log.info("Récupération des devis du client ID: {}", clientId);
        return devisMapper.toResponseList(devisRepository.findByPrestationClientId(clientId));
    }

    @Transactional(readOnly = true)
    public List<DevisResponse> findByClientEmail(String email) {
        log.info("Récupération des devis du client (email): {}", email);
        return devisMapper.toResponseList(devisRepository.findByPrestationClientEmail(email));
    }

    @Transactional
    public DevisResponse create(DevisRequest dto) {
        log.info("Création devis - PrestationId: {}", dto.getPrestationId());

        Prestation prestation = prestationRepository.findById(dto.getPrestationId())
                .orElseThrow(() -> new ResourceNotFoundException("Prestation non trouvée avec l'id : " + dto.getPrestationId()));

        Devis devis = Devis.builder()
                .dateCreation(LocalDate.now())
                .statutDevis(StatutDevis.EN_ATTENTE)
                .prestation(prestation)
                .montantTotal(BigDecimal.ZERO)
                .lignes(new ArrayList<>())
                .build();

        devis = devisRepository.save(devis);

        BigDecimal montantTotal = BigDecimal.ZERO;
        if (dto.getLignes() != null) {
            for (LigneDevisRequest ligneDto : dto.getLignes()) {
                Article article = articleRepository.findById(ligneDto.getArticleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Article non trouvé avec l'id : " + ligneDto.getArticleId()));

                LigneDevis ligne = ligneDevisMapper.toEntity(article, devis, ligneDto.getQuantite());
                ligne = ligneDevisRepository.save(ligne);
                devis.getLignes().add(ligne);
                montantTotal = montantTotal.add(ligne.getSousTotal());
            }
        }

        devis.setMontantTotal(montantTotal);
        devis = devisRepository.save(devis);

        log.info("Devis créé avec succès - ID: {}, MontantTotal: {}", devis.getId(), devis.getMontantTotal());
        historiqueService.enregistrer("Nouveau devis créé pour : " + prestation.getClient().getNom());
        return devisMapper.toResponse(devis);
    }

    @Transactional
    public DevisResponse createDepuisPrestation(Prestation prestation) {
        log.info("Création automatique du devis pour la prestation ID: {}", prestation.getId());
        Devis devis = Devis.builder()
                .dateCreation(LocalDate.now())
                .statutDevis(StatutDevis.EN_ATTENTE)
                .prestation(prestation)
                .montantTotal(BigDecimal.ZERO)
                .lignes(new ArrayList<>())
                .build();
        devis = devisRepository.save(devis);
        historiqueService.enregistrer("Devis créé automatiquement pour la demande de : " + prestation.getClient().getNom());
        return devisMapper.toResponse(devis);
    }

    @Transactional
    public DevisResponse updateLignes(Long id, List<LigneDevisRequest> lignes) {
        log.info("Mise à jour des lignes du devis - ID: {}", id);
        Devis devis = devisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Devis non trouvé avec l'id : " + id));

        for (LigneDevis ligne : new ArrayList<>(devis.getLignes())) {
            devis.getLignes().remove(ligne);
            ligneDevisRepository.delete(ligne);
        }

        BigDecimal montantTotal = BigDecimal.ZERO;
        if (lignes != null) {
            for (LigneDevisRequest ligneDto : lignes) {
                Article article = articleRepository.findById(ligneDto.getArticleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Article non trouvé avec l'id : " + ligneDto.getArticleId()));

                LigneDevis ligne = ligneDevisMapper.toEntity(article, devis, ligneDto.getQuantite());
                ligne = ligneDevisRepository.save(ligne);
                devis.getLignes().add(ligne);
                montantTotal = montantTotal.add(ligne.getSousTotal());
            }
        }

        devis.setMontantTotal(montantTotal);
        devis = devisRepository.save(devis);

        log.info("Lignes du devis mises à jour - ID: {}, MontantTotal: {}", devis.getId(), devis.getMontantTotal());
        historiqueService.enregistrer("Devis #" + id + " modifié (lignes mises à jour)");
        return devisMapper.toResponse(devis);
    }

    @Transactional
    public DevisResponse updateStatut(Long id, StatutDevis statut) {
        log.info("Mise à jour statut devis - ID: {}, Statut: {}", id, statut);
        Devis devis = devisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Devis non trouvé avec l'id : " + id));

        devis.setStatutDevis(statut);
        devis = devisRepository.save(devis);

        log.info("Statut devis mis à jour avec succès - ID: {}", devis.getId());
        historiqueService.enregistrer("Devis #" + id + " statut mis à jour : " + statut);
        return devisMapper.toResponse(devis);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Suppression devis - ID: {}", id);
        Devis devis = devisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Devis non trouvé avec l'id : " + id));

        historiqueService.enregistrer("Devis supprimé #" + devis.getId());
        devisRepository.deleteById(id);
        log.info("Devis supprimé avec succès - ID: {}", id);
    }
}