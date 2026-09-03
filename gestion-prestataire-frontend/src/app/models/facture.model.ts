export interface Facture {
  idFacture: number;
  dateFacture: Date;
  montantTotal: number;
  statut: 'Payé' | 'En attente' | 'En retard';
  idDevis: number;
}