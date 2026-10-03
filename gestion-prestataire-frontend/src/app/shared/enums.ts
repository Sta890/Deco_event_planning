/**
 * Miroir TypeScript des enums Java du backend
 * (com.onana.decoevent.enums).
 *
 * Les enums Java sont persistés en base via @Enumerated(EnumType.STRING) :
 * les valeurs ici doivent donc rester identiques aux constantes Java.
 */

export interface EnumOption<T extends string = string> {
  label: string;
  value: T;
}

/**
 * Valeurs acceptées par `severity` de p-tag / p-message (PrimeNG).
 * Aligner les tables SEVERITY ci-dessous sur ce type permet de les passer
 * directement au template sans cast.
 */
export type PrimeSeverity =
  | 'success'
  | 'info'
  | 'warn'
  | 'secondary'
  | 'contrast'
  | 'danger';

// ---------------------------------------------------------------- TypeEvenement
export const TYPE_EVENEMENT = {
  MARIAGE: 'MARIAGE',
  BAPTEME: 'BAPTEME',
  CEREMONIE: 'CEREMONIE',
  ANNIVERSAIRE: 'ANNIVERSAIRE',
  AUTRE: 'AUTRE',
} as const;
export type TypeEvenement = (typeof TYPE_EVENEMENT)[keyof typeof TYPE_EVENEMENT];

export const TYPE_EVENEMENT_LABELS: Record<TypeEvenement, string> = {
  MARIAGE: 'Mariage',
  BAPTEME: 'Baptême',
  CEREMONIE: 'Cérémonie',
  ANNIVERSAIRE: 'Anniversaire',
  AUTRE: 'Autre',
};

export const TYPE_EVENEMENT_OPTIONS: EnumOption<TypeEvenement>[] = [
  { label: TYPE_EVENEMENT_LABELS.MARIAGE, value: TYPE_EVENEMENT.MARIAGE },
  { label: TYPE_EVENEMENT_LABELS.BAPTEME, value: TYPE_EVENEMENT.BAPTEME },
  { label: TYPE_EVENEMENT_LABELS.CEREMONIE, value: TYPE_EVENEMENT.CEREMONIE },
  { label: TYPE_EVENEMENT_LABELS.ANNIVERSAIRE, value: TYPE_EVENEMENT.ANNIVERSAIRE },
  { label: TYPE_EVENEMENT_LABELS.AUTRE, value: TYPE_EVENEMENT.AUTRE },
];

// ----------------------------------------------------------------- StatutDevis
export const STATUT_DEVIS = {
  EN_ATTENTE: 'EN_ATTENTE',
  VALIDE: 'VALIDE',
  REFUSE: 'REFUSE',
} as const;
export type StatutDevis = (typeof STATUT_DEVIS)[keyof typeof STATUT_DEVIS];

export const STATUT_DEVIS_LABELS: Record<StatutDevis, string> = {
  EN_ATTENTE: 'En attente',
  VALIDE: 'Validé',
  REFUSE: 'Refusé',
};

export const STATUT_DEVIS_OPTIONS: EnumOption<StatutDevis>[] = [
  { label: STATUT_DEVIS_LABELS.EN_ATTENTE, value: STATUT_DEVIS.EN_ATTENTE },
  { label: STATUT_DEVIS_LABELS.VALIDE, value: STATUT_DEVIS.VALIDE },
  { label: STATUT_DEVIS_LABELS.REFUSE, value: STATUT_DEVIS.REFUSE },
];

export const STATUT_DEVIS_SEVERITY: Record<StatutDevis, PrimeSeverity> = {
  EN_ATTENTE: 'warn',
  VALIDE: 'success',
  REFUSE: 'danger',
};

// --------------------------------------------------------------- StatutFacture
export const STATUT_FACTURE = {
  PAYE: 'PAYE',
  EN_ATTENTE: 'EN_ATTENTE',
  EN_RETARD: 'EN_RETARD',
} as const;
export type StatutFacture = (typeof STATUT_FACTURE)[keyof typeof STATUT_FACTURE];

export const STATUT_FACTURE_LABELS: Record<StatutFacture, string> = {
  PAYE: 'Payé',
  EN_ATTENTE: 'En attente',
  EN_RETARD: 'En retard',
};

export const STATUT_FACTURE_OPTIONS: EnumOption<StatutFacture>[] = [
  { label: STATUT_FACTURE_LABELS.EN_ATTENTE, value: STATUT_FACTURE.EN_ATTENTE },
  { label: STATUT_FACTURE_LABELS.EN_RETARD, value: STATUT_FACTURE.EN_RETARD },
  { label: STATUT_FACTURE_LABELS.PAYE, value: STATUT_FACTURE.PAYE },
];

export const STATUT_FACTURE_SEVERITY: Record<StatutFacture, PrimeSeverity> = {
  PAYE: 'success',
  EN_ATTENTE: 'warn',
  EN_RETARD: 'danger',
};

// ---------------------------------------------------------------- ModePaiement
export const MODE_PAIEMENT = {
  CASH: 'CASH',
  MOBILE_MONEY: 'MOBILE_MONEY',
  VIREMENT: 'VIREMENT',
} as const;
export type ModePaiement = (typeof MODE_PAIEMENT)[keyof typeof MODE_PAIEMENT];

export const MODE_PAIEMENT_LABELS: Record<ModePaiement, string> = {
  CASH: 'Cash',
  MOBILE_MONEY: 'Mobile Money',
  VIREMENT: 'Virement',
};

export const MODE_PAIEMENT_OPTIONS: EnumOption<ModePaiement>[] = [
  { label: MODE_PAIEMENT_LABELS.CASH, value: MODE_PAIEMENT.CASH },
  { label: MODE_PAIEMENT_LABELS.MOBILE_MONEY, value: MODE_PAIEMENT.MOBILE_MONEY },
  { label: MODE_PAIEMENT_LABELS.VIREMENT, value: MODE_PAIEMENT.VIREMENT },
];

export const MODE_PAIEMENT_SEVERITY: Record<ModePaiement, PrimeSeverity> = {
  CASH: 'success',
  MOBILE_MONEY: 'warn',
  VIREMENT: 'info',
};

// ------------------------------------------------------------------------ Role
export const ROLE = {
  ADMIN: 'ADMIN',
  PRESTATAIRE: 'PRESTATAIRE',
  CLIENT: 'CLIENT',
} as const;
export type Role = (typeof ROLE)[keyof typeof ROLE];

export const ROLE_LABELS: Record<Role, string> = {
  ADMIN: 'Administrateur',
  PRESTATAIRE: 'Prestataire',
  CLIENT: 'Client',
};