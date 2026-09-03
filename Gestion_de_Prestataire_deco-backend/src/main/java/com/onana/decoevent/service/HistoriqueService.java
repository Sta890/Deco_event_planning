package com.onana.decoevent.service;
import com.onana.decoevent.models.Historique;
import com.onana.decoevent.dto.reponse.HistoriqueResponse;
import com.onana.decoevent.mapper.HistoriqueMapper;
import com.onana.decoevent.repostories.HistoriqueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoriqueService {

    private final HistoriqueRepository historiqueRepository;
    private final HistoriqueMapper historiqueMapper;

    @Transactional(readOnly = true)
    public List<HistoriqueResponse> findAll() {
        log.info("Récupération de tout l'historique");
        return historiqueMapper.toResponseList(
                historiqueRepository.findAllByOrderByDateActionDesc()
        );
    }

    @Transactional
    public void enregistrer(String action) {
        log.info("Enregistrement historique : {}", action);
        Historique historique = Historique.builder()
                .action(action)
                .dateAction(LocalDateTime.now())
                .build();
        historiqueRepository.save(historique);
    }
}