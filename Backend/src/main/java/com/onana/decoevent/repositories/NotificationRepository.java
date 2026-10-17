package com.onana.decoevent.repositories;

import com.onana.decoevent.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUtilisateurIdOrderByCreatedAtDesc(Long utilisateurId);

    List<Notification> findByUtilisateurIdAndLueFalseOrderByCreatedAtDesc(Long utilisateurId);

    long countByUtilisateurIdAndLueFalse(Long utilisateurId);
}