package com.onana.decoevent.service;
import com.onana.decoevent.dto.response.PaiementResponse;
import com.onana.decoevent.dto.request.PaiementRequest;
import com.onana.decoevent.enums.StatutFacture;
import com.onana.decoevent.exceptions.ResourceNotFoundException;
import com.onana.decoevent.mapper.PaiementMapper;
import com.onana.decoevent.models.Facture;
import com.onana.decoevent.models.Paiement;
import com.onana.decoevent.repositories.FactureRepository;
import com.onana.decoevent.repositories.PaiementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.onana.decoevent.exceptions.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final FactureRepository factureRepository;
    private final PaiementMapper paiementMapper;
    private final HistoriqueService historiqueService;

    @Transactional(readOnly = true)
    public List<PaiementResponse> findAll() {
        log.info("Récupération de tous les paiements");
        return paiementMapper.toResponseList(paiementRepository.findAll());
    }

    @Transactional(readOnly = true)
    public PaiementResponse findById(Long id) {
        log.info("Récupération du paiement avec l'id : {}", id);
        return paiementMapper.toResponse(
                paiementRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé avec l'id : " + id))
        );
    }

    @Transactional
    public PaiementResponse create(PaiementRequest dto) throws BadRequestException {
        log.info("Création paiement - FactureId: {}, Montant: {}", dto.getFactureId(), dto.getMontant());

        Facture facture = factureRepository.findById(dto.getFactureId())
                .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée avec l'id : " + dto.getFactureId()));

        if (facture.getStatutFacture() == StatutFacture.PAYE) {
            log.warn("Echec création paiement : la facture ID {} est déjà payée", dto.getFactureId());
            throw new BadRequestException("Cette facture est déjà payée");
        }

        if (paiementRepository.findByFactureId(dto.getFactureId()).isPresent()) {
            log.warn("Echec création paiement : un paiement existe déjà pour la facture ID {}", dto.getFactureId());
            throw new BadRequestException("Un paiement existe déjà pour cette facture");
        }

        // Le modèle impose un paiement unique et intégral par facture :
        // le montant saisi doit correspondre exactement au montant de la facture.
        // compareTo() et non equals() : BigDecimal.equals() est sensible à l'échelle
        // (new BigDecimal("100.0").equals(new BigDecimal("100.00")) == false).
        if (dto.getMontant().compareTo(facture.getMontantTotal()) != 0) {
            log.warn("Echec création paiement : montant {} différent du montant de la facture {} (ID {})",
                    dto.getMontant(), facture.getMontantTotal(), dto.getFactureId());
            throw new BadRequestException(
                    "Le montant du paiement (" + dto.getMontant().stripTrailingZeros().toPlainString() + " XAF) "
                            + "ne correspond pas au montant de la facture #" + dto.getFactureId()
                            + " (" + facture.getMontantTotal().stripTrailingZeros().toPlainString() + " XAF)");
        }

        Paiement paiement = paiementMapper.toEntity(dto, facture);
        paiement = paiementRepository.save(paiement);

        facture.setStatutFacture(StatutFacture.PAYE);
        factureRepository.save(facture);

        log.info("Paiement créé avec succès - ID: {}, Montant: {}", paiement.getId(), paiement.getMontant());
        historiqueService.enregistrer("Paiement reçu de : " + facture.getDevis().getPrestation().getClient().getNom() + " — " + paiement.getMontant().stripTrailingZeros().toPlainString() + " XAF");
        return paiementMapper.toResponse(paiement);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Suppression paiement - ID: {}", id);
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé avec l'id : " + id));

        Facture facture = paiement.getFacture();

        historiqueService.enregistrer("Paiement supprimé #" + paiement.getId());
        paiementRepository.deleteById(id);

        // La facture ne doit pas rester marquée payée sans paiement associé.
        facture.setStatutFacture(StatutFacture.EN_ATTENTE);
        factureRepository.save(facture);

        historiqueService.enregistrer("Facture #" + facture.getId() + " remise en attente (paiement supprimé)");
        log.info("Paiement supprimé avec succès - ID: {}", id);
    }
}