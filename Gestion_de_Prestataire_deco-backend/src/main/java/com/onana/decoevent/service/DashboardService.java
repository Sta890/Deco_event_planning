package com.onana.decoevent.service;
import com.onana.decoevent.dto.response.DashboardResponse;
import com.onana.decoevent.enums.StatutDevis;
import com.onana.decoevent.enums.StatutFacture;
import com.onana.decoevent.models.Paiement;
import com.onana.decoevent.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final ClientRepository clientRepository;
    private final PrestationRepository prestationRepository;
    private final DevisRepository devisRepository;
    private final FactureRepository factureRepository;
    private final PaiementRepository paiementRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getStats() {
        log.info("Récupération des statistiques du dashboard");

        long totalClients = clientRepository.count();
        long totalPrestations = prestationRepository.count();
        long devisEnAttente = devisRepository.findByStatutDevis(StatutDevis.EN_ATTENTE).size();
        long facturesEnRetard = factureRepository.findByStatutFacture(StatutFacture.EN_RETARD).size();

        BigDecimal chiffreAffaires = paiementRepository.findAll()
                .stream()
                .map(Paiement::getMontant)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalDevis = devisRepository.count();
        long devisValides = devisRepository.findByStatutDevis(StatutDevis.VALIDE).size();
        long tauxConversion = totalDevis > 0 ? Math.round((devisValides * 100.0) / totalDevis) : 0;

        log.info("Stats - Clients: {}, Prestations: {}, DevisEnAttente: {}, CA: {}",
                totalClients, totalPrestations, devisEnAttente, chiffreAffaires);

        return DashboardResponse.builder()
                .totalClients(totalClients)
                .totalPrestations(totalPrestations)
                .devisEnAttente(devisEnAttente)
                .facturesEnRetard(facturesEnRetard)
                .chiffreAffaires(chiffreAffaires)
                .tauxConversion(tauxConversion)
                .build();
    }
}