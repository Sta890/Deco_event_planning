package com.onana.decoevent.repositories;

import com.onana.decoevent.models.Facture;
import com.onana.decoevent.enums.StatutFacture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {
    Optional<Facture> findByDevisId(Long devisId);
    List<Facture> findByStatutFacture(StatutFacture statutFacture);
    List<Facture> findByDevisPrestationClientId(Long clientId);
    List<Facture> findByDevisPrestationClientEmail(String email);
}
