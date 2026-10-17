package com.onana.decoevent.models;

import com.onana.decoevent.enums.TypeNotification;
import jakarta.persistence.*;
import lombok.*;

/**
 * Notification destinée à un compte client.
 *
 * Elle cible volontairement un Utilisateur (l'identité d'authentification) et non
 * une fiche Client (l'identité métier) : la notification est donc adressée à la
 * personne qui s'est réellement connectée, sans dépendre de la correspondance
 * par email entre les deux tables.
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String titre;

    @Column(nullable = false, length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeNotification type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    /** Identifiant de la ressource concernée (devis ou facture), pour un lien ultérieur. */
    private Long referenceId;

    @Column(nullable = false)
    private boolean lue;
}