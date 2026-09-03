package com.onana.decoevent.service;

import com.onana.decoevent.dto.reponse.ClientResponse;
import com.onana.decoevent.dto.request.ClientRequest;
import com.onana.decoevent.exceptions.ResourceNotFoundException;
import com.onana.decoevent.mapper.ClientMapper;
import com.onana.decoevent.models.Client;
import com.onana.decoevent.repostories.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final HistoriqueService historiqueService;

    @Transactional(readOnly = true)
    public List<ClientResponse> findAll() {
        log.info("Récupération de tous les clients");
        return clientMapper.toResponseList(clientRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ClientResponse findById(Long id) {
        log.info("Récupération du client avec l'id : {}", id);
        return clientMapper.toResponse(
                clientRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'id : " + id))
        );
    }

    @Transactional
    public ClientResponse create(ClientRequest dto) throws BadRequestException {
        log.info("Création client - Nom: {}, Email: {}", dto.getNom(), dto.getEmail());
        if (clientRepository.existsByEmail(dto.getEmail())) {
            log.warn("Echec création client : l'email {} existe déjà", dto.getEmail());
            throw new BadRequestException("Email déjà utilisé : " + dto.getEmail());
        }

        Client client = clientMapper.toEntity(dto);
        client = clientRepository.save(client);

        log.info("Client créé avec succès - ID: {}, Nom: {}", client.getId(), client.getNom());
        historiqueService.enregistrer("Nouveau client ajouté : " + client.getNom());
        return clientMapper.toResponse(client);
    }

    @Transactional
    public ClientResponse update(Long id, ClientRequest dto) throws BadRequestException {
        log.info("Mise à jour client - ID: {}", id);
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'id : " + id));

        if (!client.getEmail().equals(dto.getEmail()) && clientRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email déjà utilisé : " + dto.getEmail());
        }

        clientMapper.updateEntityFromDto(dto, client);
        client = clientRepository.save(client);

        log.info("Client mis à jour avec succès - ID: {}, Nom: {}", client.getId(), client.getNom());
        historiqueService.enregistrer("Client modifié : " + client.getNom());
        return clientMapper.toResponse(client);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Suppression client - ID: {}", id);
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'id : " + id));

        historiqueService.enregistrer("Client supprimé : " + client.getNom());
        clientRepository.deleteById(id);
        log.info("Client supprimé avec succès - ID: {}", id);
    }
}