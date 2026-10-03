package com.onana.decoevent.repositories;

import com.onana.decoevent.models.LignePanier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LignePanierRepository extends JpaRepository<LignePanier, Long> {
    List<LignePanier> findByPanierId(Long panierId);
    void deleteByPanierId(Long panierId);
}
