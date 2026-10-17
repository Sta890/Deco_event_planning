package com.onana.decoevent.service;

import com.onana.decoevent.dto.response.NotificationResponse;
import com.onana.decoevent.enums.TypeNotification;
import com.onana.decoevent.exceptions.ResourceNotFoundException;
import com.onana.decoevent.mapper.NotificationMapper;
import com.onana.decoevent.models.Devis;
import com.onana.decoevent.models.Facture;
import com.onana.decoevent.models.Notification;
import com.onana.decoevent.models.Utilisateur;
import com.onana.decoevent.repositories.NotificationRepository;
import com.onana.decoevent.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Notifications adressées aux comptes clients.
 *
 * Résolution du destinataire : une fiche Client ne référence pas son compte
 * Utilisateur, le seul lien étant l'email. On cherche donc le compte par email
 * et, s'il n'existe pas, on ne notifie personne — c'est le seul point de
 * défaillance connu du modèle, et il ne doit pas faire échouer l'opération
 * métier qui a déclenché la notification.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationMapper notificationMapper;

    @Transactional(readOnly = true)
    public List<NotificationResponse> findAll(String emailUtilisateur) {
        Long utilisateurId = utilisateurIdPour(emailUtilisateur);
        return notificationMapper.toResponseList(
                notificationRepository.findByUtilisateurIdOrderByCreatedAtDesc(utilisateurId));
    }

    @Transactional(readOnly = true)
    public long countNonLues(String emailUtilisateur) {
        return notificationRepository.countByUtilisateurIdAndLueFalse(utilisateurIdPour(emailUtilisateur));
    }

    @Transactional
    public NotificationResponse marquerCommeLue(Long id, String emailUtilisateur) {
        Long utilisateurId = utilisateurIdPour(emailUtilisateur);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification non trouvée avec l'id : " + id));

        // Un client ne doit pas pouvoir lire la notification d'un autre.
        if (!notification.getUtilisateur().getId().equals(utilisateurId)) {
            throw new ResourceNotFoundException("Notification non trouvée avec l'id : " + id);
        }

        notification.setLue(true);
        notification = notificationRepository.save(notification);
        return notificationMapper.toResponse(notification);
    }

    @Transactional
    public long marquerToutesCommeLues(String emailUtilisateur) {
        List<Notification> nonLues = notificationRepository
                .findByUtilisateurIdAndLueFalseOrderByCreatedAtDesc(utilisateurIdPour(emailUtilisateur));
        nonLues.forEach(n -> n.setLue(true));
        notificationRepository.saveAll(nonLues);
        return nonLues.size();
    }

    // --------------------------------------------------------------- Émetteurs

    /** Notifie la validation ou le refus d'un devis par le prestataire. */
    @Transactional
    public void notifierDecisionDevis(Devis devis, boolean valide) {
        String email = devis.getPrestation().getClient().getEmail();

        String titre = valide
                ? "Devis #" + devis.getId() + " validé"
                : "Devis #" + devis.getId() + " refusé";

        String message = valide
                ? "Votre devis #" + devis.getId() + " a été validé pour "
                        + devis.getMontantTotal().stripTrailingZeros().toPlainString() + " XAF."
                : "Votre devis #" + devis.getId() + " n'a pas été retenu. Contactez le prestataire pour en discuter.";

        creer(email, titre, message,
                valide ? TypeNotification.DEVIS_VALIDE : TypeNotification.DEVIS_REFUSE,
                devis.getId());
    }

    /** Notifie la génération d'une facture. */
    @Transactional
    public void notifierFactureEmise(Facture facture) {
        String email = facture.getDevis().getPrestation().getClient().getEmail();

        creer(email,
                "Facture #" + facture.getId() + " disponible",
                "Une facture de " + facture.getMontantTotal().stripTrailingZeros().toPlainString()
                        + " XAF a été générée pour votre devis #" + facture.getDevis().getId() + ".",
                TypeNotification.FACTURE_EMISE,
                facture.getId());
    }

    /** Notifie l'enregistrement d'un paiement. */
    @Transactional
    public void notifierPaiementRecu(Facture facture) {
        String email = facture.getDevis().getPrestation().getClient().getEmail();

        creer(email,
                "Facture #" + facture.getId() + " réglée",
                "Le paiement de votre facture #" + facture.getId() + " a été enregistré.",
                TypeNotification.PAIEMENT_RECUS,
                facture.getId());
    }

    /**
     * Crée la notification si un compte correspond à l'email de la fiche client.
     * Un échec de résolution est journalisé puis ignoré : la notification est
     * un complément, elle ne doit jamais faire échouer l'opération métier.
     */
    private void creer(String email, String titre, String message, TypeNotification type, Long referenceId) {
        if (email == null || email.isBlank()) {
            log.warn("Notification non délivrée ({}) : la fiche client n'a pas d'email", type);
            return;
        }

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElse(null);
        if (utilisateur == null) {
            log.warn("Notification non délivrée ({}) : aucun compte ne correspond à l'email '{}'", type, email);
            return;
        }

        notificationRepository.save(Notification.builder()
                .titre(titre)
                .message(message)
                .type(type)
                .utilisateur(utilisateur)
                .referenceId(referenceId)
                .lue(false)
                .build());

        log.info("Notification créée pour {} : {}", utilisateur.getEmail(), titre);
    }

    private Long utilisateurIdPour(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé : " + email))
                .getId();
    }
}