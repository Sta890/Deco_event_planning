package com.onana.decoevent.repositories;

import com.onana.decoevent.models.Paiement;
import com.onana.decoevent.enums.ModePaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    Optional<Paiement> findByFactureId(Long factureId);
    List<Paiement> findByModePaiement(ModePaiement modePaiement);
}
