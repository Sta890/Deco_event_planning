package com.onana.decoevent.repositories;



import com.onana.decoevent.models.Devis;
import com.onana.decoevent.enums.StatutDevis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DevisRepository extends JpaRepository<Devis, Long> {
    List<Devis> findByPrestationId(Long prestationId);
    List<Devis> findByStatutDevis(StatutDevis statutDevis);
    List<Devis> findByPrestationClientId(Long clientId);
    List<Devis> findByPrestationClientEmail(String email);
}
