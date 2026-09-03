export interface Paiement {
  idPaiement: number;
  datePaiement: Date;
  montant: number;
  modePaiement: 'Cash' | 'Mobile Money' | 'Virement';
  idFacture: number;
}