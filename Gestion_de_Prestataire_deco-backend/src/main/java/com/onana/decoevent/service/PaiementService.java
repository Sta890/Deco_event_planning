package com.onana.decoevent.service;
import com.onana.decoevent.dto.reponse.PaiementResponse;
import com.onana.decoevent.dto.request.PaiementRequest;
import com.onana.decoevent.enums.StatutFacture;
import com.onana.decoevent.exceptions.ResourceNotFoundException;
import com.onana.decoevent.mapper.PaiementMapper;
import com.onana.decoevent.models.Facture;
import com.onana.decoevent.models.Paiement;
import com.onana.decoevent.repostories.FactureRepository;
import com.onana.decoevent.repostories.PaiementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
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

        Paiement paiement = paiementMapper.toEntity(dto, facture);
        paiement = paiementRepository.save(paiement);

        facture.setStatutFacture(StatutFacture.PAYE);
        factureRepository.save(facture);

        log.info("Paiement créé avec succès - ID: {}, Montant: {}", paiement.getId(), paiement.getMontant());
        historiqueService.enregistrer("Paiement reçu de : " + facture.getDevis().getPrestation().getClient().getNom() + " — " + paiement.getMontant() + " XAF");
        return paiementMapper.toResponse(paiement);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Suppression paiement - ID: {}", id);
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé avec l'id : " + id));

        historiqueService.enregistrer("Paiement supprimé #" + paiement.getId());
        paiementRepository.deleteById(id);
        log.info("Paiement supprimé avec succès - ID: {}", id);
    }
}