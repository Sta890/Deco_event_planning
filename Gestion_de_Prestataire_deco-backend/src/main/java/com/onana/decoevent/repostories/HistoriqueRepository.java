package com.onana.decoevent.repostories;


import com.onana.decoevent.models.Historique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueRepository extends JpaRepository<Historique, Long> {
    List<Historique> findByUtilisateurIdOrderByDateActionDesc(Long utilisateurId);
    List<Historique> findAllByOrderByDateActionDesc();
}