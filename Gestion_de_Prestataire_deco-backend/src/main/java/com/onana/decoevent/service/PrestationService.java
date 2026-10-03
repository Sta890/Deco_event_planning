package com.onana.decoevent.service;

import com.onana.decoevent.dto.response.PrestationResponse;
import com.onana.decoevent.dto.request.DemandeDevisRequest;
import com.onana.decoevent.dto.request.PrestationRequest;
import com.onana.decoevent.exceptions.ResourceNotFoundException;

import com.onana.decoevent.mapper.PrestationMapper;
import com.onana.decoevent.models.Client;
import com.onana.decoevent.models.Prestation;
import com.onana.decoevent.repositories.ClientRepository;
import com.onana.decoevent.repositories.PrestationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrestationService {

    private final PrestationRepository prestationRepository;
    private final ClientRepository clientRepository;
    private final PrestationMapper prestationMapper;
    private final HistoriqueService historiqueService;
    private final DevisService devisService;

    @Transactional(readOnly = true)
    public List<PrestationResponse> findAll() {
        log.info("Récupération de toutes les prestations");
        return prestationMapper.toResponseList(prestationRepository.findAll());
    }

    @Transactional(readOnly = true)
    public PrestationResponse findById(Long id) {
        log.info("Récupération de la prestation avec l'id : {}", id);
        return prestationMapper.toResponse(
                prestationRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Prestation non trouvée avec l'id : " + id))
        );
    }

    @Transactional(readOnly = true)
    public List<PrestationResponse> findByClientId(Long clientId) {
        log.info("Récupération des prestations du client ID: {}", clientId);
        return prestationMapper.toResponseList(prestationRepository.findByClientId(clientId));
    }

    @Transactional
    public PrestationResponse create(PrestationRequest dto) {
        log.info("Création prestation - Type: {}, ClientId: {}", dto.getTypeEvenement(), dto.getClientId());

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'id : " + dto.getClientId()));

        Prestation prestation = prestationMapper.toEntity(dto, client);
        prestation = prestationRepository.save(prestation);

        log.info("Prestation créée avec succès - ID: {}, Type: {}", prestation.getId(), prestation.getTypeEvenement());
        historiqueService.enregistrer("Nouvelle prestation créée : " + prestation.getTypeEvenement() + " pour " + client.getNom());
        return prestationMapper.toResponse(prestation);
    }

    @Transactional
    public PrestationResponse creerDepuisDemande(DemandeDevisRequest dto) {
        log.info("Demande de devis - Type: {}, Client: {}", dto.getTypeEvenement(), dto.getEmail());

        Client client = clientRepository.findByEmail(dto.getEmail())
                .orElseGet(() -> clientRepository.save(
                        Client.builder()
                                .nom(dto.getNom())
                                .telephone(dto.getTelephone())
                                .email(dto.getEmail())
                                .build()
                ));

        StringBuilder description = new StringBuilder();
        if (dto.getMessage() != null && !dto.getMessage().isBlank()) {
            description.append(dto.getMessage());
        }
        if (dto.getNombrePersonnes() != null) {
            if (description.length() > 0) {
                description.append(" — ");
            }
            description.append(dto.getNombrePersonnes()).append(" personnes");
        }

        Prestation prestation = Prestation.builder()
                .typeEvenement(dto.getTypeEvenement())
                .dateEvenement(dto.getDateEvenement())
                .lieu(dto.getLieu())
                .description(description.toString())
                .client(client)
                .build();
        prestation = prestationRepository.save(prestation);

        log.info("Demande de devis enregistrée - Prestation ID: {}", prestation.getId());
        historiqueService.enregistrer("Demande de devis reçue : " + dto.getTypeEvenement() + " pour " + client.getNom());
        devisService.createDepuisPrestation(prestation);
        return prestationMapper.toResponse(prestation);
    }

    @Transactional
    public PrestationResponse update(Long id, PrestationRequest dto) {
        log.info("Mise à jour prestation - ID: {}", id);
        Prestation prestation = prestationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prestation non trouvée avec l'id : " + id));

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'id : " + dto.getClientId()));

        prestationMapper.updateEntityFromDto(dto, prestation);
        prestation.setClient(client);
        prestation = prestationRepository.save(prestation);

        log.info("Prestation mise à jour avec succès - ID: {}", prestation.getId());
        historiqueService.enregistrer("Prestation modifiée : " + prestation.getTypeEvenement());
        return prestationMapper.toResponse(prestation);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Suppression prestation - ID: {}", id);
        Prestation prestation = prestationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prestation non trouvée avec l'id : " + id));

        historiqueService.enregistrer("Prestation supprimée : " + prestation.getTypeEvenement());
        prestationRepository.deleteById(id);
        log.info("Prestation supprimée avec succès - ID: {}", id);
    }
}