package com.onana.decoevent.repositories;



import com.onana.decoevent.models.Prestation;
import com.onana.decoevent.enums.TypeEvenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestationRepository extends JpaRepository<Prestation, Long> {
    List<Prestation> findByClientId(Long clientId);
    List<Prestation> findByTypeEvenement(TypeEvenement typeEvenement);
}
