package com.onana.decoevent.enums;

/**
 * Nature d'une notification adressée à un client.
 *
 * Les valeurs sont persistées en base via @Enumerated(EnumType.STRING) :
 * elles ne doivent donc pas être renommées sans migration.
 */
public enum TypeNotification {
    /** Le prestataire a validé le devis du client. */
    DEVIS_VALIDE,
    /** Le prestataire a refusé le devis du client. */
    DEVIS_REFUSE,
    /** Une facture a été générée à partir d'un devis validé. */
    FACTURE_EMISE,
    /** Le paiement de la facture a été enregistré. */
    PAIEMENT_RECUS
}