import { LigneDevis } from './ligne-devis.model';

export interface Devis {
  idDevis: number;
  dateCreation: Date;
  statut: 'En attente' | 'Validé' | 'Refusé';
  idPrestation: number;
  idClient: number;
  lignes: LigneDevis[];
  montantTotal: number;
}