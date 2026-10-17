package com.onana.decoevent.repositories;
import com.onana.decoevent.models.Panier;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PanierRepository extends JpaRepository<Panier, Long> {

    @EntityGraph(attributePaths = {"lignes", "lignes.article"})
    Optional<Panier> findByUtilisateurId(Long utilisateurId);
}