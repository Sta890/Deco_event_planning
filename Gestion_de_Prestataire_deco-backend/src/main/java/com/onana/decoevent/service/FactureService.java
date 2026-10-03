package com.onana.decoevent.service;
import com.onana.decoevent.dto.response.FactureResponse;
import com.onana.decoevent.enums.StatutDevis;
import com.onana.decoevent.enums.StatutFacture;
import com.onana.decoevent.exceptions.ResourceNotFoundException;
import com.onana.decoevent.mapper.FactureMapper;
import com.onana.decoevent.models.Devis;
import com.onana.decoevent.models.Facture;
import com.onana.decoevent.repositories.DevisRepository;
import com.onana.decoevent.repositories.FactureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.onana.decoevent.exceptions.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FactureService {

    private final FactureRepository factureRepository;
    private final DevisRepository devisRepository;
    private final FactureMapper factureMapper;
    private final HistoriqueService historiqueService;

    @Transactional(readOnly = true)
    public List<FactureResponse> findAll() {
        log.info("Récupération de toutes les factures");
        return factureMapper.toResponseList(factureRepository.findAll());
    }

    @Transactional(readOnly = true)
    public FactureResponse findById(Long id) {
        log.info("Récupération de la facture avec l'id : {}", id);
        return factureMapper.toResponse(
                factureRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée avec l'id : " + id))
        );
    }

    @Transactional(readOnly = true)
    public List<FactureResponse> findByClientId(Long clientId) {
        log.info("Récupération des factures du client ID: {}", clientId);
        return factureMapper.toResponseList(factureRepository.findByDevisPrestationClientId(clientId));
    }

    @Transactional(readOnly = true)
    public List<FactureResponse> findByClientEmail(String email) {
        log.info("Récupération des factures du client (email): {}", email);
        return factureMapper.toResponseList(factureRepository.findByDevisPrestationClientEmail(email));
    }

    @Transactional
    public FactureResponse genererDepuisDevis(Long devisId) throws BadRequestException {
        log.info("Génération facture depuis devis ID: {}", devisId);

        Devis devis = devisRepository.findById(devisId)
                .orElseThrow(() -> new ResourceNotFoundException("Devis non trouvé avec l'id : " + devisId));

        if (devis.getStatutDevis() != StatutDevis.VALIDE) {
            log.warn("Echec génération facture : le devis ID {} n'est pas validé", devisId);
            throw new BadRequestException("Le devis doit être validé pour générer une facture");
        }

        if (factureRepository.findByDevisId(devisId).isPresent()) {
            log.warn("Echec génération facture : une facture existe déjà pour le devis ID {}", devisId);
            throw new BadRequestException("Une facture existe déjà pour ce devis");
        }

        Facture facture = Facture.builder()
                .dateFacture(LocalDate.now())
                .montantTotal(devis.getMontantTotal())
                .statutFacture(StatutFacture.EN_ATTENTE)
                .devis(devis)
                .build();

        facture = factureRepository.save(facture);

        log.info("Facture générée avec succès - ID: {}, MontantTotal: {}", facture.getId(), facture.getMontantTotal());
        historiqueService.enregistrer("Facture générée pour : " + devis.getPrestation().getClient().getNom());
        return factureMapper.toResponse(facture);
    }

    @Transactional
    public FactureResponse updateStatut(Long id, StatutFacture statut) throws BadRequestException {
        log.info("Mise à jour statut facture - ID: {}, Statut: {}", id, statut);

        if (statut == StatutFacture.PAYE) {
            log.warn("Echec mise à jour statut facture ID {} : PAYE ne peut pas être set manuellement", id);
            throw new BadRequestException("Le statut PAYE est positionné automatiquement lors de l'enregistrement d'un paiement");
        }

        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée avec l'id : " + id));

        if (facture.getStatutFacture() == StatutFacture.PAYE) {
            log.warn("Echec mise à jour statut facture ID {} : facture déjà payée", id);
            throw new BadRequestException("Impossible de modifier le statut d'une facture déjà payée");
        }

        facture.setStatutFacture(statut);
        facture = factureRepository.save(facture);

        log.info("Statut facture mis à jour avec succès - ID: {}", facture.getId());
        historiqueService.enregistrer("Facture #" + id + " statut mis à jour : " + statut);
        return factureMapper.toResponse(facture);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Suppression facture - ID: {}", id);
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée avec l'id : " + id));

        historiqueService.enregistrer("Facture supprimée #" + facture.getId());
        factureRepository.deleteById(id);
        log.info("Facture supprimée avec succès - ID: {}", id);
    }
}